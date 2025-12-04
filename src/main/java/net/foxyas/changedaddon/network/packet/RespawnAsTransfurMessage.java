package net.foxyas.changedaddon.network.packet;

import net.ltxprogrammer.changed.Changed;
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

public record RespawnAsTransfurMessage(int playerId, List<ResourceLocation> possibleVariants) {

    public RespawnAsTransfurMessage(FriendlyByteBuf buf) {
        this(buf.readVarInt(), getTransfurVariants(buf));
    }

    private static @NotNull List<ResourceLocation> getTransfurVariants(FriendlyByteBuf buf) {
        int size = buf.readVarInt();

        List<ResourceLocation> variantsIds = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            ResourceLocation id = buf.readResourceLocation();
            variantsIds.add(id);
        }
        return variantsIds;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(playerId);
        buf.writeVarInt(possibleVariants.size());
        for (ResourceLocation variant : possibleVariants) {
            buf.writeResourceLocation(variant);
        }
    }


    public static void handler(RespawnAsTransfurMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) { // Server Side Packet
                sender.respawn();
                List<TransfurVariant<?>> list = getTransfurVariants(message, sender);

                TransfurVariant<?> random = Util.getRandom(list, sender.getRandom());
                ProcessTransfur.setPlayerTransfurVariant(sender,
                        random,
                        TransfurContext.hazard(TransfurCause.DEFAULT),
                        1);
            }
        });
        context.setPacketHandled(true);
    }

    private static @NotNull List<TransfurVariant<?>> getTransfurVariants(RespawnAsTransfurMessage message, ServerPlayer sender) {
        List<TransfurVariant<?>> list = new ArrayList<>();

        boolean hasRandom = message.possibleVariants()
                .stream()
                .anyMatch(id -> id.equals(Changed.modResource("random")));

        boolean onlyRandom =
                message.possibleVariants().size() == 1 &&
                        hasRandom;

        // 1. Case: only exist changed:random
        if (onlyRandom) {
            list.addAll(TransfurVariant.getPublicTransfurVariants().toList());
        }

        // 2. Case: exist random + other variants
        else if (hasRandom) {
            // add real variants
            for (ResourceLocation id : message.possibleVariants()) {
                if (!id.equals(Changed.modResource("random"))) {
                    TransfurVariant<?> variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(id);
                    if (variant != null)
                        list.add(variant);
                }
            }

            // add one random variant
            list.add(Util.getRandom(
                    TransfurVariant.getPublicTransfurVariants().toList(),
                    sender.getRandom()
            ));
        }

        // 3. normal case (no random)
        else {
            for (ResourceLocation id : message.possibleVariants()) {
                TransfurVariant<?> variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(id);
                if (variant != null)
                    list.add(variant);
            }
        }

        // 4. Fail-safe final: if nothing left
        if (list.isEmpty()) {
            list.addAll(TransfurVariant.getPublicTransfurVariants().toList());
        }
        return list;
    }
}