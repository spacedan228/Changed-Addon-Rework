package net.foxyas.changedaddon.entity.goals.abilities;

import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.foxyas.changedaddon.mixins.entity.CombatTrackerAccessor;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

public class MayDropGrabbedEntityGoal extends Goal {

    private final IGrabberEntity grabber;

    public MayDropGrabbedEntityGoal(IGrabberEntity grabber) {
        this.grabber = grabber;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
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
    public boolean canUse() {
        GrabEntityAbilityInstance grabAbilityInstance = grabber.getGrabAbilityInstance();
        return ((CombatTrackerAccessor) grabber.asMob().getCombatTracker()).isTakingDamage() && grabAbilityInstance != null && grabAbilityInstance.grabbedEntity != null;
    }

    @Override
    public void start() {
        super.start();
        if (!grabber.asMob().level().isClientSide()) {
            GrabEntityAbilityInstance grabAbilityInstance = grabber.getGrabAbilityInstance();
            if (grabAbilityInstance != null) {
                LivingEntity grabbedEntity = grabAbilityInstance.grabbedEntity;
                if (grabbedEntity != null) {
                    grabAbilityInstance.releaseEntity();
                    // manda packet de GRAB (tipo ARMS)
                    Changed.PACKET_HANDLER.send(
                            PacketDistributor.TRACKING_ENTITY.with(grabber::asMob),
                            new GrabEntityPacket(grabber.asMob(), grabbedEntity, GrabEntityPacket.GrabType.RELEASE)
                    );
                }
            }
        }
    }
}
