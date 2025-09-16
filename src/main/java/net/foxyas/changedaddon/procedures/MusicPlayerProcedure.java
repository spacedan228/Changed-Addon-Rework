package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.configuration.ChangedAddonClientConfiguration;
import net.foxyas.changedaddon.entity.bosses.Experiment009BossEntity;
import net.foxyas.changedaddon.entity.bosses.Experiment10BossEntity;
import net.foxyas.changedaddon.entity.defaults.AbstractLuminarcticLeopard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber
public class MusicPlayerProcedure {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            newPlayBossMusic(event.player.level, event.player);
        }
    }

    public static void newPlayBossMusic(LevelAccessor world, Player player) {
        if (player == null) {
            return;
        }

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        // Lista de entidades por tipo
        List<Experiment10BossEntity> exp10Entities = world.getEntitiesOfClass(Experiment10BossEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true);
        List<AbstractLuminarcticLeopard> LumiEntities = world.getEntitiesOfClass(AbstractLuminarcticLeopard.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true);
        List<Experiment009BossEntity> ketExp9Entities = world.getEntitiesOfClass(Experiment009BossEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true);

        // Verificações de proximidade
        boolean exp10Close = !exp10Entities.isEmpty();
        boolean LumiClose = !LumiEntities.isEmpty();
        boolean ketExp9Close = !ketExp9Entities.isEmpty();

        if (world.isClientSide() && ChangedAddonClientConfiguration.MUSIC_PLAYER.get()) {
            Minecraft minecraft = Minecraft.getInstance();
            MusicManager musicManager = minecraft.getMusicManager();
            SoundManager soundManager = minecraft.getSoundManager();

            // Verificar se o jogador está no modo espectador
            boolean spectator = isSpectator(player);

            if (spectator) {
                return;
            }

            // Eventos de som
            SoundEvent exp009Music = ForgeRegistries.SOUND_EVENTS.getValue(ChangedAddonMod.resourceLoc("music.boss.exp9"));
            SoundEvent exp10Music = ForgeRegistries.SOUND_EVENTS.getValue(ChangedAddonMod.resourceLoc("experiment10_theme"));
            SoundEvent LumiMusic = ForgeRegistries.SOUND_EVENTS.getValue(ChangedAddonMod.resourceLoc("music.boss.luminarctic_leopard"));

            // Instâncias de música
            Music exp10ThemeMusicInstance = new Music(Objects.requireNonNull(exp10Music), 0, 0, true);
            Music exp009Phase2ThemeMusicInstance = new Music(Objects.requireNonNull(exp009Music), 0, 0, true);
            Music LumiThemeMusicInstance = new Music(Objects.requireNonNull(LumiMusic), 0, 0, true);

            // Verificar se músicas estão tocando
            boolean isExp10ThemePlaying = musicManager.isPlayingMusic(exp10ThemeMusicInstance);
            boolean isExp009Phase2ThemePlaying = musicManager.isPlayingMusic(exp009Phase2ThemeMusicInstance);
            boolean isLumiThemePlaying = musicManager.isPlayingMusic(LumiThemeMusicInstance);


            if ((ketExp9Close)) {
                if (!isExp009Phase2ThemePlaying) {
                    if (!exp10Close && !LumiClose) {
                        musicManager.startPlaying(exp009Phase2ThemeMusicInstance);
                    }
                }

                if (ketExp9Entities.stream().anyMatch(Experiment009BossEntity::isDeadOrDying)) {
                    soundManager.stop(ChangedAddonMod.resourceLoc("music.boss.exp9"), SoundSource.MUSIC);
                }
            } else if (isExp009Phase2ThemePlaying) {
                soundManager.stop(ChangedAddonMod.resourceLoc("music.boss.exp9"), SoundSource.MUSIC);
            }

            if (exp10Close) {
                if (!isExp10ThemePlaying) {
                    if (!ketExp9Close) {
                        musicManager.startPlaying(exp10ThemeMusicInstance);
                    }
                }

                if (exp10Entities.stream().anyMatch(LivingEntity::isDeadOrDying)) {
                    soundManager.stop(ChangedAddonMod.resourceLoc("experiment10_theme"), SoundSource.MUSIC);
                }
            } else if (isExp10ThemePlaying) {
                soundManager.stop(ChangedAddonMod.resourceLoc("experiment10_theme"), SoundSource.MUSIC);
            }

            if (LumiClose && LumiEntities.stream().anyMatch((e) -> e.getTarget() == player)) {
                if (!isLumiThemePlaying) {
                    if (!exp10Close && !ketExp9Close) {
                        musicManager.startPlaying(LumiThemeMusicInstance);
                    }
                }

                if (LumiEntities.stream().anyMatch(LivingEntity::isDeadOrDying)) {
                    soundManager.stop(ChangedAddonMod.resourceLoc("music.boss.luminarctic_leopard"), SoundSource.MUSIC);
                }
            } else if (isLumiThemePlaying) {
                soundManager.stop(ChangedAddonMod.resourceLoc("music.boss.luminarctic_leopard"), SoundSource.MUSIC);
            }
        }
    }

    private static boolean isSpectator(Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
        } else if (player.level.isClientSide()) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPacketListener connection = minecraft.getConnection();
            if (connection != null) {
                var f = connection
                        .getPlayerInfo(player.getGameProfile().getId());
                return f != null && f.getGameMode() == GameType.SPECTATOR;
            }
        }

        return false;
    }
}
