package net.foxyas.changedaddon.entity.goals.prototype;

import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
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

import java.util.EnumSet;

public class PlantSeedsGoal extends Goal {

    private static final int searchRange = 6;
    private final PrototypeEntity entity;
    private final PathNavigation navigation;
    private BlockPos targetPos;

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
        if (findSeeds(false).isEmpty()) return false;

        // Look for farmland with air above to plant
        targetPos = findPlantableFarmland(entity.getLevel(), entity.blockPosition(), searchRange);
        return targetPos != null;
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

        if (entity.blockPosition().closerThan(targetPos, 3)) {
            plantSeedAt(targetPos);
            targetPos = null; // reset target after planting
            return;
        } else {
            navigation.moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 0.25f);
            // Make entity look at a target position
            this.entity.getLookControl().setLookAt(
                    targetPos.getX(), targetPos.getY(), targetPos.getZ(),
                    30.0F, // yaw change speed (degrees per tick)
                    30.0F  // pitch change speed
            );
        }

        navigation.moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 0.25f);
    }

    @Override
    public void stop() {
        super.stop();
    }

    private ItemStack findSeeds(boolean extract) {
        ItemStack seeds;
        for (int i = 0; i < entity.getItemHandler().getSlots(); i++) {
            seeds = entity.getItemHandler().getStackInSlot(i);
            if (isSeed(seeds)) {
                if(extract) return entity.getItemHandler().extractItem(i, 1, false);
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
            BlockState above = level.getBlockState(pos.above());

            if (soil.getBlock() == Blocks.FARMLAND && above.isAir()) {
                return pos.above();
            }
        }
        return null;
    }

    private void plantSeedAt(BlockPos pos) {
        Level level = entity.getLevel();
        if (level.isClientSide) return;

        ItemStack seeds = findSeeds(true);
        if (seeds.isEmpty()) return;

        // Place the crop block at target position
        this.entity.getLookControl().setLookAt(
                pos.getX(), pos.getY(), pos.getZ(),
                30.0F, // yaw change speed (degrees per tick)
                30.0F  // pitch change speed
        );
        entity.swing(entity.isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        Block block = ((BlockItem) seeds.getItem()).getBlock();
        level.setBlock(pos, block.defaultBlockState(), 3);
        level.playSound(null, pos, block.defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1, 1);
    }
}
