package net.foxyas.changedaddon.entity.goals.prototype;

import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class ApplyBonemealGoal extends Goal {

    private static final int searchRange = 12;
    private final PrototypeEntity entity;
    private final PathNavigation navigation;

    private int cooldown;
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
        if(cooldown > 0){
            cooldown--;
            return false;
        }

        if (findBoneMeal(false).isEmpty()) return false;

        // Find a growable crop nearby
        targetPos = findGrowableCrop(entity.getLevel(), entity.blockPosition(), searchRange);
        return targetPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if(targetPos == null) return false;

        BlockState state = entity.level.getBlockState(targetPos);
        return state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state);
    }

    @Override
    public void start() {
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
        navigation.moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 0.25f);
    }

    @Override
    public void tick() {
        if (targetPos == null) return;
        navigation.moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 0.25f);

        if(boneMealCooldown > 0){
            boneMealCooldown--;
            return;
        }

        if (entity.blockPosition().closerThan(targetPos, 3)) {
            applyBoneMeal(targetPos);
            boneMealCooldown = 10;
        }
    }

    @Override
    public void stop() {
        cooldown = 20;
        boneMealCooldown = 0;
    }

    private ItemStack findBoneMeal(boolean extract) {
        ItemStack boneMeal;
        for (int i = 0; i < entity.getItemHandler().getSlots(); i++) {
            boneMeal = entity.getItemHandler().getStackInSlot(i);
            if (!boneMeal.isEmpty() && boneMeal.is(Items.BONE_MEAL)) {
                if(extract) return entity.getItemHandler().extractItem(i, 1, false);
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

            if (state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state)
                    && crop.isValidBonemealTarget(level, pos, state, level.isClientSide())) {
                double dist = pos.distSqr(center);
                if (dist < closestDist) {
                    closestDist = dist;
                    closestGrowableCrop = pos.immutable();
                }
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
        Block block = state.getBlock();

        if (!(block instanceof BonemealableBlock fertilizable)
                || !fertilizable.isValidBonemealTarget(level, pos, state, false)) return;

        this.entity.getLookControl().setLookAt(
                pos.getX(), pos.getY(), pos.getZ(),
                30.0F, // yaw change speed (degrees per tick)
                30.0F  // pitch change speed
        );
        entity.swing(entity.isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        fertilizable.performBonemeal(serverLevel, level.getRandom(), pos, state);
        serverLevel.levelEvent(1505, targetPos, 8); // Bone meal particles
    }
}
