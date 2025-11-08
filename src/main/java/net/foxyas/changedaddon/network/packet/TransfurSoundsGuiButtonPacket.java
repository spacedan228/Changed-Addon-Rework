package net.foxyas.changedaddon.network.packet;

import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.util.DelayedTask;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record TransfurSoundsGuiButtonPacket(int buttonId) {

    private static final List<Triple<ResourceLocation, Predicate<Player>, Integer>> sounds = List.of(
            Triple.of(ResourceLocation.parse("entity.cat.purr"), PlayerUtil::isCatTransfur, 60),
            Triple.of(ResourceLocation.parse("entity.cat.ambient"), PlayerUtil::isCatTransfur, 10),
            Triple.of(ResourceLocation.parse("entity.wolf.growl"), PlayerUtil::isWolfTransfur, 60),
            Triple.of(ResourceLocation.parse("entity.wolf.ambient"), PlayerUtil::isWolfTransfur, 10),
            Triple.of(ResourceLocation.parse("entity.wolf.howl"), PlayerUtil::isWolfTransfur, 80),
            Triple.of(ResourceLocation.parse("entity.cat.hiss"), PlayerUtil::isCatTransfur, 40),
            Triple.of(ResourceLocation.parse("entity.cat.purreow"), PlayerUtil::isCatTransfur, 20)
    );

    public TransfurSoundsGuiButtonPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(buttonId);
    }

    public static void handler(TransfurSoundsGuiButtonPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> handleButtonAction(context.getSender(), message.buttonId));
        context.setPacketHandled(true);
    }

    public static void handleButtonAction(Player player, int buttonID) {
        if (player == null) return;

        if (!ProcessTransfur.isPlayerTransfurred(player)) return;

        ChangedAddonVariables.PlayerVariables vars = player.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY).resolve().orElse(null);
        if (vars == null) return;

        switch (buttonID) {
            case 0, 1, 2, 3, 4, 5, 6 -> {
                if (vars.actCooldown) break;
                Triple<ResourceLocation, Predicate<Player>, Integer> triple = sounds.get(buttonID);
                if (triple.getMiddle().test(player))
                    playSound(player.level, player, ForgeRegistries.SOUND_EVENTS.getValue(triple.getLeft()), vars, triple.getRight());
            }
            case 7 -> {
                if (!vars.actCooldown) break;

                vars.actCooldown = false;
                vars.syncPlayerVariables(player);
            }
            case 8 -> {
                if (vars.actCooldown) return;
                TransfurVariantInstance<?> tf = ProcessTransfur.getPlayerTransfurVariant(player);
                if (tf == null) break;

                if (tf.getFormId().toString().contains("changed_addon:form_experiment009")) {
                    player.getLevel().playSound(null, player.position().x, player.position().y, player.position().z, ChangedSounds.MONSTER2, SoundSource.HOSTILE, 35, 0);
                } else {
                    player.getLevel().playSound(null, player.position().x, player.position().y, player.position().z, ChangedSounds.MONSTER2, SoundSource.HOSTILE, 5, 1);
                }

                vars.actCooldown = true;
                vars.syncPlayerVariables(player);

                new DelayedTask(60, () -> {
                    vars.actCooldown = false;
                    vars.syncPlayerVariables(player);
                });
            }
        }
    }

    private static void playSound(Level level, Entity entity, SoundEvent sound, ChangedAddonVariables.PlayerVariables vars, int cooldown) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.PLAYERS, 2, 1);

        vars.actCooldown = true;
        vars.syncPlayerVariables(entity);

        new DelayedTask(cooldown, () -> {
            vars.actCooldown = false;
            vars.syncPlayerVariables(entity);
        });
    }
}
