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
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class FindAndHarvestCropsGoal extends Goal {

    private static final int searchRange = 10;
    private final PrototypeEntity entity;
    private final PathNavigation navigation;
    private BlockPos targetCropPos;

    public FindAndHarvestCropsGoal(PrototypeEntity entity) {
        this.entity = entity;
        this.navigation = entity.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Can only harvest if inventory not full and there's a mature crop nearby
        return (!entity.isInventoryFull() && entity.getHarvestsTimes() < PrototypeEntity.MAX_HARVEST_TIMES) && entity.findNearbyCrop(entity.getLevel(), entity.blockPosition(), searchRange) != null;
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
    public void start() {
        targetCropPos = entity.findNearbyCrop(entity.getLevel(), entity.blockPosition(), searchRange);
        if (targetCropPos == null) return;
        entity.getLevel().playSound(null, entity.blockPosition(), ChangedAddonSoundEvents.PROTOTYPE_IDEA, SoundSource.MASTER, 1, 1);
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
        navigation.moveTo(targetCropPos.getX() + 0.5, targetCropPos.getY(), targetCropPos.getZ() + 0.5, 0.25f);
    }

    @Override
    public void tick() {
        Level level = entity.getLevel();

        if (targetCropPos != null) {
            if (entity.blockPosition().closerThan(targetCropPos, 2.5)) {
                entity.harvestCrop((ServerLevel) level, targetCropPos);

                // Look at crop and swing arm

                // Place the crop block at target position
                this.entity.getLookControl().setLookAt(
                        targetCropPos.getX(), targetCropPos.getY(), targetCropPos.getZ(),
                        30.0F, // yaw change speed (degrees per tick)
                        30.0F  // pitch change speed
                );
                entity.swing(entity.isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

                targetCropPos = null; // Reset to find new crop next tick
            } else {
                navigation.moveTo(targetCropPos.getX() + 0.5, targetCropPos.getY(), targetCropPos.getZ() + 0.5, 0.25f);
            }
        } else {
            // No crop found, try again next tick
            targetCropPos = entity.findNearbyCrop(level, entity.blockPosition(), searchRange);
            if (targetCropPos != null) {
                navigation.moveTo(targetCropPos.getX() + 0.5, targetCropPos.getY(), targetCropPos.getZ() + 0.5, 0.25f);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
    }
}
