package net.foxyas.changedaddon.network;

import net.foxyas.changedaddon.client.renderer.layers.features.SonarOutlineLayer;
import net.foxyas.changedaddon.network.packets.ClientboundOpenFTKCScreenPacket;
import net.foxyas.changedaddon.network.packets.ClientboundSonarUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientPacketHandler {

    public static void handleOpenFTKCScreenPacket(ClientboundOpenFTKCScreenPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> Minecraft.getInstance().setScreen(packet.minigameType().screen.get()));
        context.setPacketHandled(true);
    }

    public static void handleSonarUpdatePacket(ClientboundSonarUpdatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> SonarOutlineLayer.ClientState.setTicksToRenderEntities(packet.ticks(), packet.fadeInTicks(), packet.maxDist(), packet.mode()));
        context.setPacketHandled(true);
    }
}
