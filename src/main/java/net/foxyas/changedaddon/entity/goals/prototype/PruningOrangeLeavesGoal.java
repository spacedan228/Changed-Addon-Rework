package net.foxyas.changedaddon.entity.goals.prototype;

import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public class PruningOrangeLeavesGoal extends Goal {

    //todo make this work
    private final PrototypeEntity prototypeEntity;
    private BlockPos targetLeave;
    private ItemStack shears;
    private int ticks = 0;

    public PruningOrangeLeavesGoal(PrototypeEntity prototypeEntity) {
        super();
        this.prototypeEntity = prototypeEntity;
    }

    @Override
    public boolean canUse() {
        targetLeave = prototypeEntity.findNearbyOrangeLeaves(prototypeEntity.blockPosition(), 10);
        boolean hasShears = prototypeEntity.getMainHandItem().is(Tags.Items.SHEARS) || prototypeEntity.getOffhandItem().is(Tags.Items.SHEARS);

        if (hasShears) {
            if (prototypeEntity.getMainHandItem().is(Tags.Items.SHEARS)) {
                shears = prototypeEntity.getMainHandItem();
            } else if (prototypeEntity.getOffhandItem().is(Tags.Items.SHEARS)) {
                shears = prototypeEntity.getOffhandItem();
            }
        }

        return targetLeave != null && hasShears;
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (targetLeave != null && prototypeEntity.getLevel() instanceof ServerLevel serverLevel) {
            if (!serverLevel.getBlockState(targetLeave).is(ChangedBlocks.ORANGE_TREE_LEAVES.get())) {
                targetLeave = prototypeEntity.findNearbyOrangeLeaves(prototypeEntity.blockPosition(), 10);
            }
        }

        if (targetLeave != null && targetLeave.distSqr(prototypeEntity.blockPosition()) > (3.5f * 3.5f)) {
            prototypeEntity.getNavigation().moveTo(targetLeave.getX(), targetLeave.getY(), targetLeave.getZ(), 0.25f);
        } else this.PrunOrangeLeaves();

        ticks++;
        if (ticks % 600 == 0) {
            if (targetLeave != null && targetLeave.distSqr(prototypeEntity.blockPosition()) > (16 ^ 2)) {
                targetLeave = prototypeEntity.findNearbyOrangeLeaves(prototypeEntity.blockPosition(), 10);
            }
        }
    }

    public void PrunOrangeLeaves() {
        if (this.prototypeEntity.getLevel() instanceof ServerLevel serverLevel) {
            BlockState state = serverLevel.getBlockState(targetLeave);
            if (state.is(ChangedBlocks.ORANGE_TREE_LEAVES.get())) {
                BlockState newState = Blocks.OAK_LEAVES.defaultBlockState();

                if (state.hasProperty(LeavesBlock.DISTANCE)) {
                    newState = newState.setValue(LeavesBlock.DISTANCE, state.getValue(LeavesBlock.DISTANCE));
                }

                if (state.hasProperty(LeavesBlock.PERSISTENT)) {
                    newState = newState.setValue(LeavesBlock.PERSISTENT, state.getValue(LeavesBlock.PERSISTENT));
                }

                int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, shears);
                UniformInt uniformInt;

                if (fortune > 0) {
                    uniformInt = UniformInt.of(fortune, 8 * fortune);
                } else {
                    uniformInt = UniformInt.of(1, 8);
                }

                ItemStack orangeStack = new ItemStack(ChangedItems.ORANGE.get());
                orangeStack.setCount(uniformInt.sample(prototypeEntity.getRandom()));
                this.shears.hurtAndBreak(1, prototypeEntity, (prototype) -> {
                });

                serverLevel.setBlockAndUpdate(targetLeave, newState);
                LeavesBlock.popResource(serverLevel, targetLeave, orangeStack);
                serverLevel.playSound(null, prototypeEntity, SoundEvents.SNOW_GOLEM_SHEAR, SoundSource.BLOCKS, 1, 1);

            }
        }
    }
}
