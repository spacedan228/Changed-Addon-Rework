package net.foxyas.changedaddon.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class PacketUtil {

    public static void playSound(ServerLevel level, Predicate<ServerPlayer> send, double x, double y, double z, SoundEvent sound, SoundSource soundSource, float volume, float pitch){
        PlaySoundAtEntityEvent event = ForgeEventFactory.onPlaySoundAtEntity(null, sound, soundSource, volume, pitch);
        if (event.isCanceled() || event.getSound() == null) return;

        sound = event.getSound();
        soundSource = event.getCategory();
        volume = event.getVolume();

        broadcast(level, send.and(distance(x, y, z, volume > 1 ? volume * 16 : 16)), level.dimension(), new ClientboundSoundPacket(sound, soundSource, x, y, z, volume, pitch));
    }

    public static Predicate<ServerPlayer> distance(double x, double y, double z, double distance){
        return player -> {
            double dX = x - player.getX();
            double dY = y - player.getY();
            double dZ = z - player.getZ();
            return dX * dX + dY * dY + dZ * dZ < distance * distance;
        };
    }

    public static void broadcast(ServerLevel level, Predicate<ServerPlayer> send, ResourceKey<Level> pDimension, Packet<?> packet){
        List<ServerPlayer> players = level.getServer().getPlayerList().getPlayers();

        for (ServerPlayer serverplayer : players) {
            if(serverplayer.level.dimension() != pDimension) continue;

            if(send.test(serverplayer)) serverplayer.connection.send(packet);
        }
    }
}
