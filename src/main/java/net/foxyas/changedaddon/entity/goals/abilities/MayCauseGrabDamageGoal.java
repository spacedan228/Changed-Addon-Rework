package net.foxyas.changedaddon.entity.goals.abilities;

import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MayCauseGrabDamageGoal extends Goal {

    private final PathfinderMob mob;
    private final IGrabberEntity grabber;

    private int cooldown;

    public MayCauseGrabDamageGoal(IGrabberEntity grabber) {
        this.grabber = grabber;
        this.mob = grabber.asMob();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown --;
            return false;
        }

        if (!mob.isAlive()) return false;

        if (grabber.getGrabTarget() == null) return false;

        return grabber.canCauseGrabDamage();
    }

    @Override
    public void start() {
        grabber.setCausingGrabDamage(true);
    }

    @Override
    public void tick() {
        // força o tick da habilidade de grab
        grabber.mayTickGrabAbility();
    }

    @Override
    public boolean canContinueToUse() {
        // só um tick é suficiente
        return false;
    }

    @Override
    public void stop() {
        grabber.setCausingGrabDamage(false);

        cooldown = 20 * 2; // 2 seconds
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    protected int adjustedTickDelay(int pAdjustment) {
        return super.adjustedTickDelay(pAdjustment);
    }
}
