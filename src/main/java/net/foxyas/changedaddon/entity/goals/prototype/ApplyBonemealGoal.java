package net.foxyas.changedaddon.entity.goals.prototype;

import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.foxyas.changedaddon.init.ChangedAddonSounds;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ApplyBonemealGoal extends Goal {

    private static final int searchRange = 12;
    private final PrototypeEntity entity;
    private final PathNavigation navigation;
    private BlockPos targetPos;

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
        if (findBoneMeal().isEmpty()) return false;

        // Find a growable crop nearby
        targetPos = findGrowableCrop(entity.getLevel(), entity.blockPosition(), searchRange);
        return targetPos != null;
    }

    @Override
    public void start() {
        if (targetPos == null) return;

        entity.getLevel().playSound(null, entity.blockPosition(), ChangedAddonSounds.PROTOTYPE_IDEA, SoundSource.MASTER, 1, 1);

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
        if (entity.blockPosition().closerThan(targetPos, 1)) {
            applyBoneMeal(targetPos);
            targetPos = null; // reset target after applying
        }
    }

    private ItemStack findBoneMeal() {
        ItemStack boneMeal = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!boneMeal.isEmpty() && boneMeal.is(Items.BONE_MEAL)) return boneMeal;

        boneMeal = entity.getItemBySlot(EquipmentSlot.OFFHAND);
        if (!boneMeal.isEmpty() && boneMeal.is(Items.BONE_MEAL)) return boneMeal;

        for (int i = 0; i < entity.getInventory().getContainerSize(); i++) {
            boneMeal = entity.getInventory().getItem(i);
            if (!boneMeal.isEmpty() && boneMeal.is(Items.BONE_MEAL)) {
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

            if (state.getBlock() instanceof BonemealableBlock fertilizable
                    && fertilizable.isValidBonemealTarget(level, pos, state, level.isClientSide())) {
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

        ItemStack boneMeal = findBoneMeal();
        if (boneMeal.isEmpty()) return;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (!(block instanceof BonemealableBlock fertilizable)
                || !fertilizable.isValidBonemealTarget(level, pos, state, false)) return;

        this.entity.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atCenterOf(pos).subtract(0,1,0));
        entity.swing(entity.isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        fertilizable.performBonemeal(serverLevel, level.getRandom(), pos, state);
        serverLevel.levelEvent(1505, targetPos, 1); // Bone meal particles
        boneMeal.shrink(1);
    }
}
