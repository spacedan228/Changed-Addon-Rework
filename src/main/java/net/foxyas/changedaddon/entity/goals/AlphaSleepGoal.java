package net.foxyas.changedaddon.entity.goals;

import net.ltxprogrammer.changed.block.Pillow;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.function.IntPredicate;

public class AlphaSleepGoal extends Goal {

    private static final EnumSet<Goal.Flag> FLAGS = EnumSet.allOf(Goal.Flag.class);

    protected final PathfinderMob holder;
    protected final float scanRange;
    protected final IntPredicate fluffyBlocksRequired;
    protected final float noWalkingRange;
    protected final float noWalkingRangeSqr;
    protected final IntProvider sleepDurationProvider;

    protected int lastScan;
    protected boolean enoughFluffyBlocks;
    protected int sleepDuration;

    public AlphaSleepGoal(PathfinderMob holder, float scanRange, IntPredicate fluffyBlocksRequired, float noWalkingRange, IntProvider sleepDurationProvider) {
        this.holder = holder;
        this.scanRange = scanRange;
        this.fluffyBlocksRequired = fluffyBlocksRequired;
        this.noWalkingRange = noWalkingRange;
        noWalkingRangeSqr = noWalkingRange * noWalkingRange;
        this.sleepDurationProvider = sleepDurationProvider;

        setFlags(FLAGS);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    protected void scanForFluffyBlocks() {
        if (holder.tickCount - lastScan >= 20) {
            lastScan = holder.tickCount;
            Level level = holder.level;
            enoughFluffyBlocks = fluffyBlocksRequired.test((int) BlockPos.betweenClosedStream(holder.getBoundingBox().inflate(scanRange))
                    .filter(pos -> {
                        BlockState state = level.getBlockState(pos);
                        return state.is(BlockTags.BEDS) || state.is(BlockTags.WOOL) || state.getBlock() instanceof Pillow;
                    })
                    .count());
        }
    }

    @Override
    public boolean canUse() {
        scanForFluffyBlocks();
        return enoughFluffyBlocks;
    }

    @Override
    public void start() {
        holder.startSleeping(holder.blockPosition());
        sleepDuration = sleepDurationProvider.sample(holder.getRandom());
    }

    @Override
    public boolean canContinueToUse() {
        if (sleepDuration-- <= 0 || !canUse()) return false;

        Level level = holder.level;
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, holder.getBoundingBox().inflate(noWalkingRange),
                EntitySelector.NO_SPECTATORS.and(e -> e != holder && e.distanceToSqr(holder) <= noWalkingRangeSqr));

        Vec3 movement;
        for (LivingEntity entity : entities) {
            if (entity.isSleeping()) continue;

            movement = entity.getDeltaMovement();//Will not work on players tho
            if (movement.x == 0 && movement.z == 0) continue;

            if (entity.isCrouching()) continue;

            if (movement.x >= 1 || movement.z >= 1) return false;//just some random speed threshold

            if (entity.getRandom().nextFloat() > 0.2f + (0.4f * (1 - entity.distanceToSqr(holder) / noWalkingRangeSqr))) return false;
        }

        return true;
    }

    @Override
    public void stop() {
        holder.stopSleeping();
    }
}
