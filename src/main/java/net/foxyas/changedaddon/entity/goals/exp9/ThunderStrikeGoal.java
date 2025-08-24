package net.foxyas.changedaddon.entity.goals.exp9;

import net.foxyas.changedaddon.effect.particles.ChangedAddonParticles;
import net.foxyas.changedaddon.util.DelayedTask;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ThunderStrikeGoal extends Goal {
    private final PathfinderMob pathfinderMob;
    private final double jumpPower;
    private final int duration; // ticks de duração do ataque
    private int tickCounter;
    private BlockPos groundPos;
    private LivingEntity target;
    protected final IntProvider cooldownProvider;
    public int cooldown = 0;

    public ThunderStrikeGoal(PathfinderMob pathfinderMob, IntProvider cooldownProvider, double jumpPower, int duration) {
        this.pathfinderMob = pathfinderMob;
        this.jumpPower = jumpPower;
        this.duration = duration;
        this.cooldownProvider = cooldownProvider;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        LivingEntity target = pathfinderMob.getTarget();
        return target != null && target.isAlive() && pathfinderMob.isOnGround();
    }

    @Override
    public void start() {
        this.target = pathfinderMob.getTarget();
        this.groundPos = pathfinderMob.blockPosition();
        this.tickCounter = 0;

        // Lança a entidade para cima
        Vec3 velocity = pathfinderMob.position().vectorTo(target.position()).normalize().scale(0.5f);
        pathfinderMob.setDeltaMovement(pathfinderMob.getDeltaMovement().add(velocity.x, jumpPower, velocity.z));
        ChangedSounds.broadcastSound(pathfinderMob, ChangedSounds.BOW2, 1, 1);


        // Slow falling para manter no ar
        pathfinderMob.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration + 40, 10, false, false));

        // Evita que tente andar
        pathfinderMob.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && tickCounter < duration;
    }

    @Override
    public void tick() {
        tickCounter++;

        if (target != null) {
            // olha para o alvo
            pathfinderMob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (tickCounter % 10 == 0) { // a cada 1/2s lança um raio
                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(pathfinderMob.getLevel());
                if (lightning != null) {
                    lightning.moveTo(target.position());
                    if (pathfinderMob instanceof ChangedEntity changedEntity) {
                        lightning.setCause((ServerPlayer) changedEntity.getUnderlyingPlayer());
                    }
                    lightning.setDamage(10);
                    PlayerUtil.ParticlesUtil.sendParticles(pathfinderMob.getLevel(), ChangedAddonParticles.thunderSpark(5), lightning.getEyePosition(), 0.3f, 0.3f, 0.3f, 25, 0.25f);
                    pathfinderMob.getLookControl().setLookAt(lightning, 30, 30);
                    DelayedTask.schedule(10, () -> {
                        pathfinderMob.getLevel().addFreshEntity(lightning);
                        applyKnockBack(lightning);
                        pathfinderMob.swing(pathfinderMob.isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                        // recoil de knockback para trás
                        Vec3 dir = pathfinderMob.position().vectorTo(target.position()).normalize().scale(-0.5);
                        pathfinderMob.push(dir.x, dir.y * 1.25f, dir.z);
                    });
                    pathfinderMob.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration + 40, 10, false, false));
                }
            }
        }
    }

    public void applyKnockBack(Entity lightning) {
        var list = lightning.getLevel()
                .getNearbyEntities(
                        LivingEntity.class,
                        TargetingConditions.DEFAULT
                                .selector((target) -> !target.is(lightning) && !target.is(pathfinderMob)),
                        pathfinderMob, lightning.getBoundingBox().inflate(8)
                );

        for (LivingEntity livingEntity : list) {
            livingEntity.push(0,0.5f,0);
        }
    }

    @Override
    public void stop() {
        if (groundPos != null) {
            pathfinderMob.teleportTo(groundPos.getX() + 0.5, groundPos.getY(), groundPos.getZ() + 0.5);
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(pathfinderMob.getLevel());
            if (lightning != null) {
                lightning.moveTo(groundPos.getX() + 0.5, groundPos.getY(), groundPos.getZ() + 0.5);
                if (pathfinderMob instanceof ChangedEntity changedEntity) {
                    lightning.setCause((ServerPlayer) changedEntity.getUnderlyingPlayer());
                }
                pathfinderMob.getLevel().addFreshEntity(lightning);
            }
        }

        // Remove slow falling
        pathfinderMob.removeEffect(MobEffects.SLOW_FALLING);

        this.target = null;
        this.groundPos = null;
        this.cooldown = cooldownProvider.sample(this.pathfinderMob.getRandom());
    }
}
