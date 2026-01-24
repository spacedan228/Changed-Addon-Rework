package net.foxyas.changedaddon.network;

import net.foxyas.changedaddon.ability.api.GrabEntityAbilityExtensor;
import net.foxyas.changedaddon.client.renderer.layers.features.SonarOutlineLayer;
import net.foxyas.changedaddon.network.packet.ClientboundOpenFTKCScreenPacket;
import net.foxyas.changedaddon.network.packet.ClientboundSonarUpdatePacket;
import net.foxyas.changedaddon.network.packet.SafeGrabSyncPacket;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class SafeClientPacketHandler {

    public static void handleOpenFTKCScreenPacket(ClientboundOpenFTKCScreenPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        ClientPacketHandler.handleOpenFTKCScreenPacket(packet, contextSupplier);
    }

    public static void handleSonarUpdatePacket(ClientboundSonarUpdatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        ClientPacketHandler.handleSonarUpdatePacket(packet, contextSupplier);
    }

    public static void handleSafeGrabSync(SafeGrabSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        ClientPacketHandler.handleSafeGrabSync(packet, contextSupplier);
    }

    public static void handlerVariableSync(ChangedAddonVariables.SyncPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        ClientPacketHandler.handlerVariableSync(message, contextSupplier);
    }
}
