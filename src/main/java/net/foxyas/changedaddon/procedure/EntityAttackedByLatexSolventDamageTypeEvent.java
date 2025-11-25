package net.foxyas.changedaddon.procedure;

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
public class EntityAttackedByLatexSolventDamageTypeEvent {

    @SubscribeEvent
    public static void onEntityAttacked(LivingHurtEvent event) {
        Entity target = event.getEntity();
        DamageSource source = event.getSource();

        if (source.is(ChangedAddonDamageSources.LATEX_SOLVENT.key()) && !source.getMsgId().contains(ChangedAddonDamageSources.LATEX_SOLVENT.source(target.level()).getMsgId()))
            return;

        Level level = target.level;
        if (target instanceof Player player) {
            level.playSound(null, player, SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER, 0.5f, 0);
        } else {
            target.playSound(SoundEvents.FIRE_EXTINGUISH, 0.5f, 0);
        }

        // Emite partículas
        if (!(level instanceof ServerLevel serverLevel)) return;

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