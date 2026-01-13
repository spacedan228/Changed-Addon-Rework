package net.foxyas.changedaddon.entity.api;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface ICrawlAbleEntity {

    default void crawlingSystem(ChangedEntity livingEntity, LivingEntity target, float swimSpeed) {
        updateSwimmingMovement(livingEntity, swimSpeed);
        if (target != null) {
            setCrawlingPoseIfNeeded(livingEntity, target);
            crawlToTarget(livingEntity, target);
        } else switchToSafePose(livingEntity);
    }

    default void switchToSafePose(ChangedEntity livingEntity) {
        Pose currentPose = livingEntity.getPose();
        Pose safePose = currentPose;

        if (canEnterPose(livingEntity, Pose.STANDING)) {
            safePose = Pose.STANDING;
        } else if (canEnterPose(livingEntity, Pose.CROUCHING)) {
            safePose = Pose.CROUCHING;
        } else if (canEnterPose(livingEntity, Pose.SWIMMING)) {
            safePose = Pose.SWIMMING;
        }

        if (safePose != currentPose) {
            livingEntity.setPose(safePose);
            //this.refreshDimensions();
        }
    }

    static boolean canEnterPose(ChangedEntity entity, Pose pose) {
        return (entity.overridePose == null || entity.overridePose == pose) && entity.level.noCollision(entity, entity.getBoundingBoxForPose(pose).deflate(1.0E-7D));
    }


    default void crawlingSystem(ChangedEntity livingEntity, LivingEntity target) {
        crawlingSystem(livingEntity, target, 0.015f);
    }

    default void crawlingSystem() {
        if (this instanceof ChangedEntity changedEntity) {
            crawlingSystem(changedEntity, changedEntity.getTarget(), 0.015f);
        }
    }

    default void crawlingSystem(LivingEntity target) {
        if (this instanceof ChangedEntity changedEntity) {
            crawlingSystem(changedEntity, target, 0.015f);
        }
    }

    default void crawlingSystem(LivingEntity target, float speed) {
        if (this instanceof ChangedEntity changedEntity) {
            crawlingSystem(changedEntity, target, speed);
        }
    }

    default void crawlingSystem(float speed) {
        if (this instanceof ChangedEntity changedEntity) {
            crawlingSystem(changedEntity, changedEntity.getTarget(), speed);
        }
    }

    default void onlyCrawlingSystem() {
        if (this instanceof ChangedEntity changedEntity) {
            onlyCrawlingSystem(changedEntity, changedEntity.getTarget());
        }
    }

    default void onlyCrawlingSystem(LivingEntity target) {
        if (this instanceof ChangedEntity changedEntity) {
            onlyCrawlingSystem(changedEntity, target);
        }
    }

    default void onlyCrawlingSystem(ChangedEntity livingEntity, LivingEntity target) {
        if (target != null) {
            setCrawlingPoseIfNeeded(livingEntity, target);
            crawlToTarget(livingEntity, target);
        } else switchToSafePose(livingEntity);
    }

    default void setCrawlingPoseIfNeeded(ChangedEntity livingEntity, LivingEntity target) {
        if (target.getPose() == Pose.SWIMMING && livingEntity.getPose() != Pose.SWIMMING) {
            if (target.getY() < livingEntity.getEyeY() && !target.level.getBlockState(new BlockPos(target.getX(), target.getEyeY(), target.getZ()).above()).isAir()) {
                livingEntity.setPose(Pose.SWIMMING);
            }
        } else {
            switchToSafePose(livingEntity);
        }
    }

    default void crawlToTarget(LivingEntity livingEntity, LivingEntity target) {
        if (target.getPose() == Pose.SWIMMING && livingEntity.getPose() == Pose.SWIMMING) {
            Vec3 direction = target.position().subtract(livingEntity.position()).normalize();
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(direction.scale(0.05)));
        }
    }

    default void updateSwimmingMovement(ChangedEntity livingEntity, float speed) {
        if (livingEntity.isInWater()) {
            if (livingEntity.getTarget() != null) {
                Vec3 direction = livingEntity.getTarget().position().subtract(livingEntity.position()).normalize();
                if (livingEntity.isEyeInFluid(FluidTags.WATER)) {
                    livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(direction.scale(speed)));
                } else {
                    livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(direction.scale(speed / 4)));
                }
                livingEntity.getLookControl().setLookAt(livingEntity.getTarget(), 30, 30);
            }
            if (livingEntity.isEyeInFluid(FluidTags.WATER)) {
                livingEntity.setPose(Pose.SWIMMING);
                livingEntity.setSwimming(true);
            } else {
                livingEntity.setPose(Pose.STANDING);
                livingEntity.setSwimming(false);
            }
        } else {
            BlockPos above = new BlockPos(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ()).above();
            BlockState blockState = livingEntity.level.getBlockState(above);
            if (livingEntity.getPose() == Pose.SWIMMING && !livingEntity.isInWater() && (blockState.isAir() || !blockState.isSuffocating(livingEntity.level, above) || !blockState.isSolidRender(livingEntity.level, above))) {
                livingEntity.setPose(Pose.STANDING);
            }
        }
    }
}
