package net.foxyas.changedaddon.entity.goals.prototype;

import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.foxyas.changedaddon.util.DelayedTask;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;

import java.util.EnumSet;

public class PlantSeedsGoal extends Goal {

    private static final int searchRange = 6;

    private final PrototypeEntity entity;
    private final PathNavigation navigation;

    private boolean lock;
    private BlockPos targetPos;
    private int plantCooldown;

    public PlantSeedsGoal(PrototypeEntity entity) {
        this.entity = entity;
        this.navigation = entity.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        if(lock) return false;

        return !findSeeds(false).isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        if(targetPos == null){
            lock = true;
            new DelayedTask(100, ()-> lock = false);
            return false;
        }

        return !findSeeds(false).isEmpty();
    }

    @Override
    public void start() {// Look for farmland with air above to plant
        targetPos = findPlantableFarmland(entity.getLevel(), entity.blockPosition(), searchRange);
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
        if(targetPos == null || isBlockInvalid(level, level.getBlockState(targetPos.below()), targetPos)){
            targetPos = findPlantableFarmland(level, entity.blockPosition(), searchRange);
            if(targetPos == null) return;
        }

        navigation.moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 0.25f);
        entity.getLookControl().setLookAt(
                targetPos.getX(), targetPos.getY(), targetPos.getZ(),
                30.0F, // yaw change speed (degrees per tick)
                30.0F  // pitch change speed
        );

        if(plantCooldown > 0){
            plantCooldown--;
            return;
        }

        if (entity.blockPosition().closerThan(targetPos, 3)) {
            plantSeedAt();
            targetPos = null; // reset target after planting
        }
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
        targetPos = null;
        plantCooldown = 0;
    }

    private boolean isBlockInvalid(Level level, BlockState state, BlockPos above){
        return state.getBlock() != Blocks.FARMLAND || !level.getBlockState(above).isAir();
    }

    private ItemStack findSeeds(boolean extract) {
        IItemHandler handsInv = entity.getHandsAndInv();
        ItemStack seeds;
        for (int i = 0; i < handsInv.getSlots(); i++) {
            seeds = handsInv.getStackInSlot(i);
            if (isSeed(seeds)) {
                if(extract) return handsInv.extractItem(i, 1, false);
                return seeds;
            }
        }

        return ItemStack.EMPTY;
    }

    private boolean isSeed(ItemStack stack) {
        // Check if the item is a seed (BlockItem and the block is a CropBlock)
        if (stack.isEmpty()) return false;
        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;
        Block block = blockItem.getBlock();
        return block instanceof CropBlock;
    }

    private BlockPos findPlantableFarmland(Level level, BlockPos center, int range) {
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-range, -1, -range),
                center.offset(range, 1, range))) {
            BlockState soil = level.getBlockState(pos);
            if(isBlockInvalid(level, soil, pos.above())) continue;
            return pos.above();
        }
        return null;
    }

    private void plantSeedAt() {
        Level level = entity.getLevel();

        ItemStack seeds = findSeeds(true);
        if (seeds.isEmpty()) return;

        if(isBlockInvalid(level, level.getBlockState(targetPos.below()), targetPos)) return;

        // Place the crop block at target position
        entity.getLookControl().setLookAt(
                targetPos.getX(), targetPos.getY(), targetPos.getZ(),
                30.0F, // yaw change speed (degrees per tick)
                30.0F  // pitch change speed
        );
        entity.swing(entity.isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        Block block = ((BlockItem) seeds.getItem()).getBlock();
        level.setBlock(targetPos, block.defaultBlockState(), 3);
        level.playSound(null, targetPos, block.defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1, 1);
    }
}
