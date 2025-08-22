package net.foxyas.changedaddon.entity.goals.exp9;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.Random;

@ParametersAreNonnullByDefault
public class SummonLightningGoal extends Goal {

    protected static final EnumSet<Flag> FLAGS = EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK);

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
        holder.level.playSound(null, strikePos.x, strikePos.y, strikePos.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1, 1);

        setFlags(FLAGS);
    }

    @Override
    public void tick() {
        if(lightnings <= 0) return;

        Level level = holder.level;
        if(castDuration > 0){
            castDuration--;

            holder.getLookControl().setLookAt(target);

            if(holder.tickCount % 20 == 0) {
                Vec3 holderPos = holder.position();
                ((ServerLevel) level).sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        holderPos.x - 0.5, holderPos.y, holderPos.z - 0.5,
                        25, 1, 0, 1, 0);
            }
            return;
        }

        if(!getFlags().isEmpty()) getFlags().clear();

        Random random = holder.getRandom();
        if(strikePos == null){
            strikePos = target.position();
            lightningDelay = lightningDelayProvider.sample(random);
        }

        if(lightningDelay > 0){
            lightningDelay--;

            int gameTime = holder.tickCount;
            if(gameTime % 20 == 0) ((ServerLevel)level).sendParticles(ParticleTypes.ELECTRIC_SPARK, strikePos.x - 1.5, strikePos.y, strikePos.z - 1.5,
                    50, 3, 0, 3, 0);

            if((gameTime + 10) % 40 == 0) level.playSound(null, strikePos.x, strikePos.y, strikePos.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.5f, 1);
            return;
        }

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        lightning.moveTo(strikePos);
        lightning.setDamage(damageProvider.sample(random));
        level.addFreshEntity(lightning);

        if (holder instanceof ChangedEntity changedEntity) {
            lightning.setCause((ServerPlayer) changedEntity.getUnderlyingPlayer());
        }

        strikePos = null;
    }

    @Override
    public void stop() {
        target = null;
        cooldown = cooldownProvider.sample(holder.getRandom());
        lightnings = 0;
        strikePos = null;
    }
}
