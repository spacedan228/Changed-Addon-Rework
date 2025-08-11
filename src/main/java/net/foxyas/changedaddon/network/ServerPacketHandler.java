package net.foxyas.changedaddon.network;

import net.foxyas.changedaddon.network.packets.ServerboundProgressFTKCPacket;
import net.foxyas.changedaddon.qte.FightToKeepConsciousness;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerPacketHandler {

    public static void handleProgressFTKCPacket(ServerboundProgressFTKCPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            ChangedAddonModVariables.PlayerVariables vars = ChangedAddonModVariables.PlayerVariables.ofOrDefault(player);
            if(vars.FTKCminigameType == null) return;

            if(!ProcessTransfur.isPlayerTransfurred(player)){
                FightToKeepConsciousness.successFTKC(vars, player);
                return;
            }

            vars.consciousnessFightProgress += vars.FTKCminigameType.progressAmount;

            if(vars.consciousnessFightProgress >= FightToKeepConsciousness.STRUGGLE_NEED){
                player.level.playSound(null, player, ChangedSounds.BLOW1, SoundSource.PLAYERS, 1, 1);
                FightToKeepConsciousness.successFTKC(vars, player);
                return;
            }

            player.level.playSound(null, player, ChangedSounds.BLOW1, SoundSource.PLAYERS, 1, 1);
            vars.syncPlayerVariables(player);
        });
        context.setPacketHandled(true);
    }
}
