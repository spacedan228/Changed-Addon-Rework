package net.foxyas.changedaddon.entity.goals.prototype;

import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import java.util.EnumSet;

public class FindChestGoal extends Goal {

    private static final int searchRange = 8;
    private final PrototypeEntity entity;
    private final PathNavigation navigation;
    private BlockPos targetChestPos;

    public FindChestGoal(PrototypeEntity entity) {
        this.entity = entity;
        this.navigation = entity.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Use only if inventory full or max harvests reached, and a chest exists nearby
        if (entity.getTargetChestPos() == null
                && (entity.wantsToDeposit() || entity.getHarvestsTimes() >= PrototypeEntity.MAX_HARVEST_TIMES)) {
            targetChestPos = entity.tryFindNearbyChest(entity.getLevel(), entity.blockPosition(), searchRange);
            return targetChestPos != null;
        }

        return false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        if (targetChestPos == null) return;

        BlockPos targetChestPosOld = entity.getTargetChestPos();
        entity.setTargetChestPos(targetChestPos);
        if (targetChestPosOld != entity.getTargetChestPos()) {
            entity.getLevel().playSound(null, entity.blockPosition(), ChangedAddonSoundEvents.PROTOTYPE_IDEA.get(), SoundSource.MASTER, 1, 1);
        }

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
        navigation.moveTo(targetChestPos.getX() + 0.5, targetChestPos.getY(), targetChestPos.getZ() + 0.5, 0.25f);
    }

    @Override
    public void tick() {//Maybe check whether the chest is still there?
        entity.setTargetChestPos(targetChestPos);
        // Just keep moving towards chest
        if (targetChestPos != null && !entity.blockPosition().closerThan(targetChestPos, 2.0)) {
            navigation.moveTo(targetChestPos.getX() + 0.5, targetChestPos.getY(), targetChestPos.getZ() + 0.5, 0.25f);
        }
    }

    @Override
    public boolean canContinueToUse() {
        // Continue until close to chest
        return targetChestPos != null && !entity.blockPosition().closerThan(targetChestPos, 2.0);
    }

    @Override
    public void stop() {
        entity.setTargetChestPos(targetChestPos);
        targetChestPos = null;
    }
}
