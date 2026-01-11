package net.foxyas.changedaddon.network;

import net.foxyas.changedaddon.network.packet.SyncGrabSafeModePacket;
import net.foxyas.changedaddon.network.packet.VariantSecondAbilityActivate;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.CustomFallable;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.network.packet.*;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangedAddonPackets {
    private final SimpleChannel packetHandler;
    private int messageID = 0;

    public ChangedAddonPackets(SimpleChannel packetHandler) {
        this.packetHandler = packetHandler;
    }

    public void registerPackets() {
        addNetworkMessage(SyncGrabSafeModePacket.class, SyncGrabSafeModePacket::new);
    }

    private <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        this.packetHandler.registerMessage(this.messageID++, messageType, encoder, decoder, messageConsumer);
    }

    private <T extends ChangedPacket> void addNetworkMessage(Class<T> messageType, Function<FriendlyByteBuf, T> ctor) {
        this.addNetworkMessage(messageType, ChangedPacket::write, ctor, ChangedPacket::handle);
    }
}
