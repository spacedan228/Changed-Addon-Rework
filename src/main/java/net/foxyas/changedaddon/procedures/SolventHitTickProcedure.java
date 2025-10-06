package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonDamageSources;
import net.foxyas.changedaddon.init.ChangedAddonParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SolventHitTickProcedure {

    @SubscribeEvent
    public static void onEntityAttacked(LivingHurtEvent event) {
        Entity target = event.getEntity();
        DamageSource source = event.getSource();

        // Verifica se o atacante possui o encantamento Solvent
        if (source == ChangedAddonDamageSources.SOLVENT || (source.getMsgId().equals("latex_solvent") || source.getMsgId().startsWith("latex_solvent"))) {
            Level level = target.level;

            // Toca som de extinção de fogo
            if (target instanceof Player player) {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER, 0.5f, 0);
                } else {
                    level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER, 0.5f, 0, true);
                }
            } else {
                target.playSound(SoundEvents.FIRE_EXTINGUISH, 0.5f, 0);
            }

            // Emite partículas
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        (SimpleParticleType) ChangedAddonParticleTypes.SOLVENT_PARTICLE.get(),
                        target.getX(),
                        target.getY() + 1,
                        target.getZ(),
                        10,
                        0.2, 0.3, 0.2, 0.1
                );
            }
        }
    }
}