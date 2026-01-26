package net.foxyas.changedaddon.network.packet;

import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.util.DelayedTask;
import net.foxyas.changedaddon.variant.TransfurSoundsDetails.TransfurSoundAction;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static net.foxyas.changedaddon.variant.TransfurSoundsDetails.getSoundFor;

public record TransfurSoundsGuiButtonPacket(int actionId) {

    public TransfurSoundsGuiButtonPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(actionId);
    }

    public static void handler(TransfurSoundsGuiButtonPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                handleButtonAction(ctx.get().getSender(), msg.actionId)
        );
        ctx.get().setPacketHandled(true);
    }

    // ===============================
    // Core logic
    // ===============================

    public static void handleButtonAction(Player player, int actionId) {
        if (player == null) return;
        if (!ProcessTransfur.isPlayerTransfurred(player)) return;

        ChangedAddonVariables.PlayerVariables vars =
                ChangedAddonVariables.ofOrDefault(player);

        if (vars.actCooldown) return;

        TransfurSoundAction action = getAction(actionId);
        if (action == null) return;

        if (!action.canUse(player)) return;

        SoundEvent sound = getSoundFor(player, action);
        if (sound == null) return;

        playSound(player.level(), player, sound, vars, action.getCooldown());
    }

    private static TransfurSoundAction getAction(int id) {
        TransfurSoundAction[] values = TransfurSoundAction.values();
        return id >= 0 && id < values.length ? values[id] : null;
    }

    // ===============================
    // Sound + cooldown
    // ===============================

    private static void playSound(
            Level level,
            Entity entity,
            SoundEvent sound,
            ChangedAddonVariables.PlayerVariables vars,
            int cooldown
    ) {
        level.playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                sound,
                SoundSource.PLAYERS,
                2f,
                1f
        );

        vars.actCooldown = true;
        vars.syncPlayerVariables(entity);

        new DelayedTask(cooldown, () -> {
            vars.actCooldown = false;
            vars.syncPlayerVariables(entity);
        });
    }
}