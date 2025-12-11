package net.foxyas.changedaddon.entity.goals.abilities;

import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.foxyas.changedaddon.mixins.abilities.GrabEntityAbilityInstanceAccessor;
import net.foxyas.changedaddon.mixins.entity.CombatTrackerAccessor;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

public class MayGrabTargetGoal extends Goal {

    private final IGrabberEntity grabber;

    public MayGrabTargetGoal(IGrabberEntity grabber) {
        this.grabber = grabber;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean isInterruptable() {
        return super.isInterruptable();
    }

    @Override
    public boolean canUse() {
        GrabEntityAbilityInstance grabAbilityInstance = grabber.getGrabAbilityInstance();
        LivingEntity target = grabber.asMob().getTarget();
        if (target == null) return false;
        double reachSqr = grabber.asMob().getMeleeAttackRangeSqr(target) * 0.7f; //Closer than a normal punch
        if (grabAbilityInstance == null) return false;
        if (grabAbilityInstance instanceof GrabEntityAbilityInstanceAccessor grabEntityAbilityInstanceAccessor && grabEntityAbilityInstanceAccessor.getGrabCooldown() > 0) return false;

        return target.distanceToSqr(grabber.asMob()) <= reachSqr && grabAbilityInstance.grabbedEntity == null;
    }

    @Override
    public void tick() {
        super.tick();
        tryGrabNearbyTarget();
    }

    @Override
    public void start() {
        super.start();
        tryGrabNearbyTarget();
    }

    private void tryGrabNearbyTarget() {
        LivingEntity target = grabber.asMob().getTarget();
        if (!grabber.asMob().level().isClientSide()) {
            if (target != null && target.distanceTo(grabber.asMob()) <= 2.5f) {
                GrabEntityAbilityInstance grabAbilityInstance = grabber.getGrabAbilityInstance();
                if (grabAbilityInstance != null) {
                    LivingEntity grabbedEntity = grabAbilityInstance.grabbedEntity;
                    if (grabbedEntity == null) {
                        grabAbilityInstance.grabEntity(target);

                        Changed.PACKET_HANDLER.send(
                                PacketDistributor.TRACKING_ENTITY.with(grabber::asMob),
                                new GrabEntityPacket(grabber.asMob(), target, GrabEntityPacket.GrabType.ARMS)
                        );

                        ProcessTransfur.forceNearbyToRetarget(target.level(), target);

                        grabber.asMob().setTarget(null);

                        // som (opcional, pode mudar)
                        ChangedSounds.broadcastSound(
                                grabber.asMob(),
                                ChangedSounds.LATEX_GRAB_ENTITY,
                                1.0f,
                                1.0f
                        );
                    }
                }
            }
        }
    }
}
