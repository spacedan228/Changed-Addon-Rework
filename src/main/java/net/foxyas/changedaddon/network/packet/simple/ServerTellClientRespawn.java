package net.foxyas.changedaddon.network.packet.simple;

import net.foxyas.changedaddon.client.gui.RespawnAsTransfurScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerTellClientRespawn {

    public ServerTellClientRespawn() {
    }

    public ServerTellClientRespawn(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public static boolean handle(ServerTellClientRespawn ignoredTellClientRespawn, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                // ====================
                //  CLIENT ONLY
                // ====================
                Minecraft minecraft = Minecraft.getInstance();
                assert minecraft.player != null;
                minecraft.player.respawn();
                if (minecraft.screen instanceof DeathScreen || minecraft.screen instanceof RespawnAsTransfurScreen) {
                    minecraft.setScreen(null);
                }
            });
        }

        return true;
    }
}
