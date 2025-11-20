package net.foxyas.changedaddon.entity.goals.prototype;

import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.foxyas.changedaddon.util.DelayedTask;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;

import java.util.EnumSet;

public class ApplyBonemealGoal extends Goal {

    private static final int searchRange = 12;
    private final PrototypeEntity entity;
    private final PathNavigation navigation;

    private boolean lock;
    private BlockPos targetPos;
    private int boneMealCooldown;

    public ApplyBonemealGoal(PrototypeEntity entity) {
        this.entity = entity;
        this.navigation = entity.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
    protected int adjustedTickDelay(int pAdjustment) {
        return 0;
    }

    @Override
    public boolean canUse() {
        if(lock) return false;

        return !findBoneMeal(false).isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        if(targetPos == null) {
            lock = true;
            new DelayedTask(200, () -> lock = false);
            return false;
        }

        BlockState state = entity.level.getBlockState(targetPos);
        return state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state) && !findBoneMeal(false).isEmpty();
    }

    @Override
    public void start() {// Find a growable crop nearby
        targetPos = findGrowableCrop(entity.getLevel(), entity.blockPosition(), searchRange);
        if (targetPos == null) return;

        entity.getLevel().playSound(null, entity.blockPosition(), ChangedAddonSoundEvents.PROTOTYPE_IDEA.get(), SoundSource.MASTER, 1, 1);

        if (entity.getLevel().isClientSide) {
            entity.getLevel().addParticle(
                    ChangedParticles.emote(entity, Emote.IDEA),
                    entity.getX(),
                    entity.getY() + (double) entity.getDimensions(entity.getPose()).height + 0.65,
                    entity.getZ(),
                    0.0f,
                    0.0f,
                    0.0f
            );
        }
    }

    @Override
    public void tick() {
        Level level = entity.level;
        if (targetPos == null || isBlockInvalid(level, level.getBlockState(targetPos), targetPos)) {
            targetPos = findGrowableCrop(entity.level, entity.blockPosition(), searchRange);
            if(targetPos == null) return;
        }

        navigation.moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 0.25f);
        entity.getLookControl().setLookAt(
                targetPos.getX(), targetPos.getY(), targetPos.getZ(),
                30.0F, // yaw change speed (degrees per tick)
                30.0F  // pitch change speed
        );

        if(boneMealCooldown > 0){
            boneMealCooldown--;
            return;
        }

        if (entity.blockPosition().closerThan(targetPos, 2)) {
            applyBoneMeal(targetPos);
            boneMealCooldown = 10;
            targetPos = findGrowableCrop(entity.level, entity.blockPosition(), searchRange);
        }
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
        targetPos = null;
        boneMealCooldown = 0;
    }

    private boolean isBlockInvalid(Level level, BlockState state, BlockPos pos){
        return !(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)
                || crop.isValidBonemealTarget(level, pos, state, level.isClientSide());
    }

    private ItemStack findBoneMeal(boolean extract) {
        IItemHandler handsInv = entity.getHandsAndInv();
        ItemStack boneMeal;
        for (int i = 0; i < handsInv.getSlots(); i++) {
            boneMeal = handsInv.getStackInSlot(i);
            if (!boneMeal.isEmpty() && boneMeal.is(Items.BONE_MEAL)) {
                if(extract) return handsInv.extractItem(i, 1, false);
                return boneMeal;
            }
        }

        return ItemStack.EMPTY;
    }

    private BlockPos findGrowableCrop(Level level, BlockPos center, int range) {
        BlockPos closestGrowableCrop = null;
        double closestDist = Double.MAX_VALUE;

        BlockState state;
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-range, -1, -range),
                center.offset(range, 1, range))) {
            state = level.getBlockState(pos);

            if(isBlockInvalid(level, state, pos)) continue;

            double dist = pos.distSqr(center);
            if (dist < closestDist) {
                closestDist = dist;
                closestGrowableCrop = pos.immutable();
            }
        }
        return closestGrowableCrop;
    }

    private void applyBoneMeal(BlockPos pos) {
        Level level = entity.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;

        ItemStack boneMeal = findBoneMeal(true);
        if (boneMeal.isEmpty()) return;

        BlockState state = level.getBlockState(pos);
        if(isBlockInvalid(level, state, pos)) return;

        entity.swing(entity.isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        ((CropBlock)state.getBlock()).performBonemeal(serverLevel, level.getRandom(), pos, state);
        level.levelEvent(1505, targetPos, 8); // Bone meal particles
    }
}
