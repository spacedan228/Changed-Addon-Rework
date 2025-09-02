package net.foxyas.changedaddon.entity.goals.generic.attacks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class SimpleAntiFlyingAttack extends Goal {
    private final Mob attacker;
    private final int delay;
    private final IntProvider cooldownProvider;
    public int cooldown;
    private final float minRange;
    private final float maxRange;
    private final float damage;
    private LivingEntity target;
    private int ticks = 0;

    public SimpleAntiFlyingAttack(Mob attacker, IntProvider cooldownProvider, float minRange, float maxRange, float damage, int delay) {
        this.attacker = attacker;
        this.cooldownProvider = cooldownProvider;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.damage = damage;
        this.delay = delay;

        //setFlags(EnumSet.of(Flag.TARGET, Flag.MOVE));
    }

    public Mob getAttacker() {
        return attacker;
    }

    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public boolean canUse() {
        this.target = attacker.getTarget();
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        if (target instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
            return player.getAbilities().flying && attacker.distanceTo(player) <= maxRange;
        }
        return target != null && !target.isOnGround() &&
                attacker.distanceTo(target) >= minRange && attacker.distanceTo(target) <= maxRange;
    }

    @Override
    public boolean canContinueToUse() {
        if (target instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
            return player.isAlive() && !player.isOnGround();
        }
        return target != null && target.isAlive() && !target.isOnGround();
    }

    @Override
    public void start() {
        /*
        *
        attacker.getLevel().playSound(null, attacker, ChangedSounds.BOW2, SoundSource.HOSTILE, 2, 1);
        if (attacker.getEyePosition().distanceTo(target.getEyePosition()) <= 2f) {
            teleportAndKnockbackInAir(1);
        }
        */
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    protected int adjustedTickDelay(int delay) {
        return super.adjustedTickDelay(delay);
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks % delay == 0) {
            if (target instanceof Player player && player.isAlive()
                    && !player.isOnGround() && player.getAbilities().flying) {
                if (attacker.distanceTo(target) <= 2 && attacker.isOnGround()) {
                    slam();
                }
            } else if (target instanceof Player player && player.isAlive()
                    && !player.isOnGround() && player.isFallFlying()) {
                if (attacker.isOnGround()) {
                    slam();
                }
            }
            if (!target.isOnGround() && attacker.isOnGround()) {
                slam();
            }
        } else {
            attacker.getNavigation().stop();
            attacker.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (attacker.getLevel() instanceof ServerLevel serverLevel) {
                BlockState ground = attacker.getFeetBlockState();
                BlockParticleOption dust = new BlockParticleOption(ParticleTypes.BLOCK, ground);

                for (int i = 0; i < 6; i++) {
                    double x = attacker.getX() + (attacker.getRandom().nextDouble() - 0.5) * 0.6;
                    double y = attacker.getY();
                    double z = attacker.getZ() + (attacker.getRandom().nextDouble() - 0.5) * 0.6;

                    double motionX = (attacker.getRandom().nextDouble() - 0.5) * 0.1;
                    double motionY = attacker.getRandom().nextDouble() * 0.1 + 0.05; // poeira sobe um pouco
                    double motionZ = (attacker.getRandom().nextDouble() - 0.5) * 0.1;

                    serverLevel.sendParticles(dust, x, y, z, 1, motionX, motionY, motionZ, 0.0);
                }
            }
        }
    }

    @Override
    public void stop() {
        if (!attacker.isOnGround()) {
            BlockPos pos = attacker.blockPosition();
            int groundY = attacker.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
            attacker.teleportTo(pos.getX() + 0.5, groundY + 0.5, pos.getZ() + 0.5);
            spawnImpactSoundEffect();
            spawnImpactParticleEffect(target.position());
            //target.teleportTo(attacker.getX(), attacker.getY(), attacker.getZ());
        }
        ticks = 0;
        cooldown = cooldownProvider.sample(this.attacker.getRandom());
    }

    private void removeIframesFromTarget() {
        target.invulnerableTime = 0;
        target.hurtDuration = 1;
        target.hurtDir = 1;
        target.hurtTime = 1;
    }

    private void teleportAndKnockbackInAir(float strength) {
        if (target == null) return;
        attacker.teleportTo(target.getX(), target.getY(), target.getZ());
        Vec3 knockDir = attacker.getLookAngle().scale(strength).add(0, 0.2, 0);
        target.setDeltaMovement(knockDir);
        attacker.swing(InteractionHand.MAIN_HAND);
        removeIframesFromTarget();
        if (!target.isBlocking()) {
            target.hurt(DamageSource.mobAttack(attacker), damage / 2);
        } else {
            target.getLevel().playSound(null, target, SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1, 1);
        }
        applySlowFalling(target);
        spawnImpactSoundEffect();
        spawnImpactParticleEffect(target.position());
    }

    private void slam() {
        if (target == null) return;
        if (target instanceof Player player) {
            if (player.getAbilities().flying) {
                player.getAbilities().flying = false;
            }
        }
        attacker.teleportTo(target.getX(), target.getY(), target.getZ());
        target.setDeltaMovement(0, -8, 0);
        attacker.swing(InteractionHand.MAIN_HAND);
        removeIframesFromTarget();
        if (!target.isBlocking()) {
            target.hurt(DamageSource.mobAttack(attacker), damage);
        } else {
            target.getLevel().playSound(null, target, SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1, 1);
        }
        spawnImpactSoundEffect();
        spawnImpactParticleEffect(target.position());
        removeSlowFalling();
    }

    private void removeSlowFalling() {
        target.removeEffect(MobEffects.SLOW_FALLING);
        attacker.removeEffect(MobEffects.SLOW_FALLING);
    }

    private void applySlowFalling(LivingEntity entity) {
        MobEffectInstance slowFalling = new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false);
        entity.addEffect(slowFalling);
        attacker.addEffect(slowFalling);
    }

    private void spawnImpactSoundEffect() {
        if (attacker.level instanceof ServerLevel serverLevel) {
            attacker.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 2, 1);
        }
    }

    private void spawnImpactParticleEffect(Vec3 pos) {
        if (attacker.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLASH, pos.x, pos.y, pos.z, 3, 0, 0, 0, 0);
            doClawsAttackEffect(attacker);
        }
    }

    public void doClawsAttackEffect(LivingEntity livingEntity) {// Efeito visual
        double d0 = (double) (-Mth.sin(livingEntity.getYRot() * 0.017453292F)) * 1;
        double d1 = (double) Mth.cos(livingEntity.getYRot() * 0.017453292F) * 1;
        if (livingEntity.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, livingEntity.getX() + d0, livingEntity.getY(0.5), livingEntity.getZ() + d1, 0, d0, 0.0, d1, 0.0);
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, livingEntity.getX() + d0, livingEntity.getY(0.6), livingEntity.getZ() + d1, 0, d0, 0.0, d1, 0.0);
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, livingEntity.getX() + d0, livingEntity.getY(0.7), livingEntity.getZ() + d1, 0, d0, 0.0, d1, 0.0);
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1f, 0.75f);
        }
    }

}
