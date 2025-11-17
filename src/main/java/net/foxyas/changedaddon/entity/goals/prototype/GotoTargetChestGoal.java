package net.foxyas.changedaddon.entity.goals.prototype;

import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.EnumSet;

public class GotoTargetChestGoal extends Goal {

    private final PrototypeEntity entity;
    private final PathNavigation navigation;
    private int ticks = 0;

    public GotoTargetChestGoal(PrototypeEntity entity) {
        this.entity = entity;
        this.navigation = entity.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Use only if inventory full or max harvests reached, and a chest exists nearby
        return entity.getTargetChestPos() != null
                && (entity.wantsToDeposit() || entity.getHarvestsTimes() >= PrototypeEntity.MAX_HARVEST_TIMES);
    }

    @Override
    public boolean canContinueToUse() {
        // Continue until close to chest
        return entity.getTargetChestPos() != null && !entity.blockPosition().closerThan(entity.getTargetChestPos(), 2.0) && ticks <= 200;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    /*@Override
    public void start() {
        entity.setTargetChestPos(targetChestPos);
        if (entity.getTargetChestPos() != null) {
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
        }
    }*/

    @Override
    public void tick() {
        BlockPos targetChestPos = entity.getTargetChestPos();
        if(targetChestPos == null) return;

        if (!(entity.getLevel().getBlockState(targetChestPos).getBlock() instanceof ChestBlock)
                || !(entity.getLevel().getBlockEntity(targetChestPos) instanceof ChestBlockEntity)) {
            entity.tryFindNearbyChest(entity.getLevel(), entity.getOnPos(), 8);
            return;
        }

        if (entity.blockPosition().closerThan(targetChestPos, 2.0)) return;

        navigation.moveTo(targetChestPos.getX() + 0.5, targetChestPos.getY(), targetChestPos.getZ() + 0.5, 0.25f);
        entity.getLookControl().setLookAt(
                targetChestPos.getX(), targetChestPos.getY(), targetChestPos.getZ(),
                30.0F, // yaw change speed (degrees per tick)
                30.0F  // pitch change speed
        );
        ticks++;
    }

    @Override
    public void stop() {
        navigation.stop();
        ticks = 0;
    }

    // Helper Method
    private boolean isChestFull(ChestBlockEntity chest) {
        for (int i = 0; i < chest.getContainerSize(); i++) {
            ItemStack stack = chest.getItem(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return false; // Slot vazio ou incompleto => não está cheio
            }
        }
        return true;
    }
}
