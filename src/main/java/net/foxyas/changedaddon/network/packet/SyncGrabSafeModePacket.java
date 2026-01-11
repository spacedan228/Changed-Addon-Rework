package net.foxyas.changedaddon.network.packet;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.ability.api.GrabEntityAbilityExtensor;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class SyncGrabSafeModePacket implements ChangedPacket {

    private final UUID playerUUID;
    private final boolean safeMode;

    public SyncGrabSafeModePacket(UUID playerUUID, boolean safeMode) {
        this.playerUUID = playerUUID;
        this.safeMode = safeMode;
    }

    public SyncGrabSafeModePacket(FriendlyByteBuf buffer) {
        this.playerUUID = buffer.readUUID();
        this.safeMode = buffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerUUID);
        buffer.writeBoolean(safeMode);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.setPacketHandled(true);

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            if (Minecraft.getInstance().level == null) return;

            Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
            if (player == null) return;

            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                var grab = variant.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                if (grab instanceof GrabEntityAbilityExtensor holder) {
                    holder.setSafeMode(safeMode);
                }
            });
        }

        // SERVER
        ServerPlayer sender = context.getSender();
        if (sender == null) return;

        ProcessTransfur.ifPlayerTransfurred(sender, variant -> {
            var grab = variant.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grab instanceof GrabEntityAbilityExtensor holder) {
                holder.setSafeMode(safeMode);
            }

            // rebroadcast
            ChangedAddonMod.PACKET_HANDLER.send(
                    PacketDistributor.ALL.noArg(),
                    new SyncGrabSafeModePacket(sender.getUUID(), safeMode)
            );
        });
    }
}
