package net.foxyas.changedaddon.entity.goals.exp9;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class SummonLightningGoal extends Goal {

    protected final PathfinderMob holder;
    protected final IntProvider cooldownProvider;
    protected final IntProvider lightningCountProvider;
    protected final IntProvider castDurationProvider;
    protected final IntProvider lightningDelayProvider;
    protected final FloatProvider damageProvider;

    protected LivingEntity target;
    protected int cooldown;
    protected int lightnings;
    protected int castDuration;
    protected int lightningDelay;
    protected Vec3 strikePos;
    protected BlockPos aboveWaterPos;

    public SummonLightningGoal(PathfinderMob holder, IntProvider cooldown, IntProvider lightningCount, IntProvider castDuration, IntProvider lightningDelay, FloatProvider damage){
        this.holder = holder;
        cooldownProvider = cooldown;
        assert lightningCount.getMinValue() >= 1;
        lightningCountProvider = lightningCount;
        castDurationProvider = castDuration;
        lightningDelayProvider = lightningDelay;
        damageProvider = damage;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        if(cooldown > 0){
            cooldown--;
            return false;
        }

        target = holder.getTarget();
        return target != null && target.isAlive() && target.isOnGround();
    }

    @Override
    public boolean canContinueToUse() {
        return lightnings > 0 && target.isAlive();
    }

    @Override
    public void start() {
        lightnings = lightningCountProvider.sample(holder.getRandom());
        castDuration = castDurationProvider.sample(holder.getRandom());
        holder.level.playSound(null, holder.getX(), holder.getY(), holder.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1, 1);
        holder.getNavigation().stop();
    }

    @Override
    public void tick() {
        if(lightnings <= 0) return;

        Level level = holder.level;
        if(castDuration > 0){
            castDuration--;

            holder.setDeltaMovement(Vec3.ZERO);
            holder.getLookControl().setLookAt(target);

            if(holder.tickCount % 2 == 0){
                ((ServerLevel) level).sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        holder.getX() - 0.5, holder.getY(), holder.getZ() - 0.5,
                        25, 1, 0.1, 1, 0.5);
            }

            return;
        }

        Random random = holder.getRandom();
        if(strikePos == null){
            strikePos = target.position();

            BlockPos pos = new BlockPos(strikePos);
            if(level.getBlockState(pos).is(Blocks.WATER)){
                do pos = pos.above();
                while (level.getBlockState(pos).is(Blocks.WATER));
                aboveWaterPos = pos;
            }

            lightningDelay = lightningDelayProvider.sample(random);
        }

        if(lightningDelay > 0){
            lightningDelay--;

            int gameTime = holder.tickCount;
            if(gameTime % 2 == 0){
                ((ServerLevel) level).sendParticles(ParticleTypes.ELECTRIC_SPARK, strikePos.x - 1, aboveWaterPos != null ? aboveWaterPos.getY() : strikePos.y, strikePos.z - 1,
                        50, 2, 0.2, 2, 0.5);
            }
            if((gameTime + 10) % 40 == 0) level.playSound(null, strikePos.x, strikePos.y, strikePos.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.5f, 1);
            return;
        }

        lightnings--;
        lightning(level, strikePos.x, strikePos.y, strikePos.z, damageProvider.sample(random));
        lightning(level, strikePos.x + 0.75, strikePos.y, strikePos.z + 0.75, damageProvider.sample(random));
        lightning(level, strikePos.x + 0.75, strikePos.y, strikePos.z - 0.75, damageProvider.sample(random));
        lightning(level, strikePos.x - 0.75, strikePos.y, strikePos.z - 0.75, damageProvider.sample(random));
        lightning(level, strikePos.x - 0.75, strikePos.y, strikePos.z + 0.75, damageProvider.sample(random));

        strikePos = null;
        aboveWaterPos = null;
    }

    protected void lightning(Level level, double x, double y, double z, float damage){
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        lightning.moveTo(x, y, z);
        lightning.setDamage(damage);
        level.addFreshEntity(lightning);
        applyKnockBack(lightning);
    }

    public void applyKnockBack(Entity mob) {
        var list = mob.getLevel()
                .getNearbyEntities(
                        LivingEntity.class,
                        TargetingConditions.DEFAULT
                                .selector((target) -> !target.is(mob)),
                        this.holder, mob.getBoundingBox().inflate(16)
                );

        for (LivingEntity livingEntity : list) {
            Vec3 direction = livingEntity.position().subtract(mob.position());

            direction = direction.normalize();

            double strength = 2.0 / livingEntity.distanceTo(mob);

            livingEntity.push(
                    direction.x * strength,
                    direction.y * strength * 2,
                    direction.z * strength
            );
        }
    }

    @Override
    public void stop() {
        target = null;
        cooldown = cooldownProvider.sample(holder.getRandom());
        lightnings = 0;
        strikePos = null;
        aboveWaterPos = null;
    }
}
