package net.foxyas.changedaddon.entity.goals.abilities;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.ability.api.GrabEntityAbilityExtensor;
import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.foxyas.changedaddon.mixins.abilities.GrabEntityAbilityInstanceAccessor;
import net.foxyas.changedaddon.network.packet.DynamicGrabEntityPacket;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.init.ChangedSounds;
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
        if (GrabEntityAbility.getGrabber(target) != null) return false;
        if (grabber.getGrabCooldown() > 0)
            return false;

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
        if (!grabber.asMob().getLevel().isClientSide()) {
            if (target != null && target.distanceTo(grabber.asMob()) <= 2.5f) {
                GrabEntityAbilityInstance grabAbilityInstance = grabber.getGrabAbilityInstance();
                if (grabAbilityInstance != null) {
                    LivingEntity grabbedEntity = grabAbilityInstance.grabbedEntity;
                    if (grabbedEntity == null && GrabEntityAbility.getGrabber(target) == null) {
                        grabAbilityInstance.grabEntity(target);

                        ChangedAddonMod.PACKET_HANDLER.send(
                                PacketDistributor.TRACKING_ENTITY.with(grabber::asMob),
                                new DynamicGrabEntityPacket(grabber.asMob(), target, DynamicGrabEntityPacket.GrabType.ARMS)
                        );

                        ProcessTransfur.forceNearbyToRetarget(target.getLevel(), target);

                        grabber.asMob().setTarget(null);

                        // som (opcional, pode mudar)
                        ChangedSounds.broadcastSound(
                                grabber.asMob(),
                                ChangedSounds.BLOW1,
                                1.0f,
                                1.0f
                        );

                        grabber.setGrabCooldown(120);
                    }
                }
            }
        }
    }
}