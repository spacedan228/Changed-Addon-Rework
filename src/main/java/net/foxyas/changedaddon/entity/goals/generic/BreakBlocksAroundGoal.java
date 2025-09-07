package net.foxyas.changedaddon.entity.goals.generic;

import net.foxyas.changedaddon.entity.bosses.Experiment009BossEntity;
import net.foxyas.changedaddon.entity.bosses.Experiment10BossEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class BreakBlocksAroundGoal extends Goal {
    private final Mob mob;
    private int breakCooldown = 0;

    public BreakBlocksAroundGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob instanceof Experiment009BossEntity experiment009BossEntity) {
            if (experiment009BossEntity.isPhase2()) {
                if (!mob.isAlive() || breakCooldown > 0) {
                    if (breakCooldown > 0) {
                        tickCooldown();
                    }

                    return false;
                }

                if (mob.getDeltaMovement().length() > 0) {
                    if (mob.horizontalCollision || mob.verticalCollision) {
                        return true;
                    }
                }
            }
        } else if (mob instanceof Experiment10BossEntity experiment10BossEntity) {
            if (experiment10BossEntity.isPhase2()) {
                if (!mob.isAlive() || breakCooldown > 0) {
                    if (breakCooldown > 0) {
                        tickCooldown();
                    }

                    return false;
                }

                if (mob.getDeltaMovement().length() > 0) {
                    if (mob.horizontalCollision || mob.verticalCollision) {
                        return true;
                    }
                }
            }
        }

        if (!mob.isAlive() || breakCooldown > 0) {
            if (breakCooldown > 0) {
                tickCooldown();
            }

            return false;
        }

        if (mob.getDeltaMovement().length() > 0) {
            if (mob.horizontalCollision || mob.verticalCollision) return true;
        }

        if (mob.getTarget() != null) {
            BlockPos eyePos = mob.blockPosition().above(Mth.floor(mob.getEyeHeight()));
            return !mob.level.getBlockState(eyePos).isAir();
        }
        return false;
    }

    @Override
    public void tick() {
        if (!(mob.level instanceof ServerLevel serverLevel)) return;
        if (breakCooldown > 0) {
            tickCooldown();
            return;
        }

        BlockPos mobPos = mob.blockPosition();
        int suppedCooldown = 0;
        for (BlockPos pos : BlockPos.betweenClosed(
                mobPos.offset(-1, 0, -1),
                mobPos.offset(1, 1, 1))) {

            if (pos.equals(mobPos.below())) continue;

            var state = serverLevel.getBlockState(pos);
            if (!state.isAir() && state.getDestroySpeed(serverLevel, pos) >= 0) {
                if (!state.is(Blocks.BEDROCK) || !state.is(BlockTags.WITHER_IMMUNE)) {
                    serverLevel.destroyBlock(pos, true, mob);
                    suppedCooldown += 5;
                }
            }
        }
        breakCooldown = suppedCooldown;
    }

    @Override
    public void stop() {
    }

    public void tickCooldown() {
        if (breakCooldown > 0) breakCooldown--;
    }
}
