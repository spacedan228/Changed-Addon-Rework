package net.foxyas.changedaddon.network.packet;

import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record RespawnAsTransfur(int playerId, List<TransfurVariant<?>> possibleVariants) {

    public RespawnAsTransfur(FriendlyByteBuf buf) {
        this(buf.readVarInt(), getTransfurVariants(buf));
    }

    private static @NotNull List<TransfurVariant<?>> getTransfurVariants(FriendlyByteBuf buf) {
        int size = buf.readVarInt();

        List<TransfurVariant<?>> variants = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            ResourceLocation id = buf.readResourceLocation();
            TransfurVariant<?> variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(id);
            if (variant != null)
                variants.add(variant);
        }
        return variants;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(playerId);
        buf.writeVarInt(possibleVariants.size());
        for (TransfurVariant<?> variant : possibleVariants) {
            buf.writeResourceLocation(variant.getFormId());
        }
    }


    public static void handler(RespawnAsTransfur message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) { // Server Side Packet
                sender.respawn();
                ProcessTransfur.setPlayerTransfurVariant(sender,
                        Util.getRandom(message.possibleVariants(), sender.getRandom()),
                        TransfurContext.hazard(TransfurCause.DEFAULT),
                        1);
            }
        });
        context.setPacketHandled(true);
    }
}
