package net.foxyas.changedaddon.entity.goals.exp10;

import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Random;

public class ClawsComboAttackGoal extends Goal {

    protected final PathfinderMob holder;
    protected final Random random;
    protected final IntProvider cooldownProvider;
    protected final IntProvider attackCountProvider;
    protected final IntProvider castDurationProvider;
    protected final FloatProvider damageProvider;
    protected final DamageSource source;

    protected LivingEntity target;
    protected int cooldown;
    protected int attacks;
    protected Vec3 attackPos;
    protected int castDuration;
    protected int wasBlocked;

    public ClawsComboAttackGoal(PathfinderMob holder, IntProvider cooldown, IntProvider attackCount, IntProvider castDuration, FloatProvider damage) {
        this.holder = holder;
        random = holder.getRandom();
        cooldownProvider = cooldown;
        attackCountProvider = attackCount;
        castDurationProvider = castDuration;
        damageProvider = damage;

        source = new EntityDamageSource("mob", holder) {
            @Override
            public @Nullable Vec3 getSourcePosition() {
                return attackPos;
            }
        };

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        target = holder.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return target.isAlive() && attacks > 0;
    }

    @Override
    public void start() {
        attacks = attackCountProvider.sample(random);
        castDuration = castDurationProvider.sample(random);
        pickAttackPos();

        holder.getNavigation().stop();
        holder.getLookControl().setLookAt(target);
    }

    protected void pickAttackPos() {
        attackPos = target.position();
        Level level = holder.level;
        BlockPos pos = new BlockPos(attackPos);
    }

    @Override
    public void tick() {
        if (attacks <= 0) return;

        if (target != null) {
            holder.getLookControl().setLookAt(target, 30, 30);
        }

        if (castDuration > 0) {
            castDuration--;
            return;
        }

        attacks--;

        holder.teleportTo(attackPos.x, attackPos.y, attackPos.z);
        holder.swing(InteractionHand.MAIN_HAND);

        holder.level.playSound(null, holder, ChangedSounds.BOW2, SoundSource.HOSTILE, 1.0f, 1.0f);

        if (attacks == 0) {
            applyKnockbackAndHurt(6, 2, 3);
        } else {
            castDuration = castDurationProvider.sample(random);
            if (applyKnockbackAndHurt(4, 1, 1)) {
                wasBlocked = 60;
            } else pickAttackPos();
        }
    }

    protected boolean applyKnockbackAndHurt(float radius, float damageMul, float knockbackMul) {
        Level level = holder.level;
        float diameter = radius * 2;
        float radiusSqr = radius * radius;
        var list = level.getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.forCombat().selector(target -> !target.is(holder)),
                holder, AABB.ofSize(attackPos, diameter, diameter, diameter)
        );

        boolean anyBlocked = false, blocked;
        float dist, knockback;
        Vec3 direction;
        for (LivingEntity livingEntity : list) {
            dist = (float) livingEntity.distanceToSqr(attackPos);
            if (dist > radiusSqr) continue;

            dist = Mth.sqrt(dist);
            blocked = livingEntity.isDamageSourceBlocked(source);

            if (livingEntity.hurt(source, damageProvider.sample(random) * damageMul)) {
                holder.getLookControl().setLookAt(livingEntity, 30, 30);
                doClawsAttackEffect();
            }

            direction = livingEntity.position().subtract(attackPos).normalize();
            knockback = radius / dist * knockbackMul;
            if (blocked) {
                knockback *= 0.25f;
                anyBlocked = true;
            }

            livingEntity.push(
                    direction.x * knockback,
                    direction.y * knockback,
                    direction.z * knockback
            );
        }

        return anyBlocked;
    }

    public void doClawsAttackEffect() {// Efeito visual
        double d0 = (double) (-Mth.sin(holder.getYRot() * 0.017453292F)) * 1;
        double d1 = (double) Mth.cos(holder.getYRot() * 0.017453292F) * 1;
        if (holder.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, holder.getX() + d0, holder.getY(0.5), holder.getZ() + d1, 0, d0, 0.0, d1, 0.0);
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, holder.getX() + d0, holder.getY(0.6), holder.getZ() + d1, 0, d0, 0.0, d1, 0.0);
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, holder.getX() + d0, holder.getY(0.7), holder.getZ() + d1, 0, d0, 0.0, d1, 0.0);
            holder.level.playSound(null, holder.getX(), holder.getY(), holder.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1f, 0.75f);
        }
    }

    @Override
    public void stop() {
        target = null;
        cooldown = cooldownProvider.sample(random);
        attacks = 0;
        attackPos = null;
        castDuration = 0;
        wasBlocked = 0;
    }
}
