package net.foxyas.changedaddon.entity.goals.abilities;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.foxyas.changedaddon.network.packet.DynamicGrabEntityPacket;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class GrabTargetGoal extends MeleeAttackGoal {

    protected final IGrabberEntity grabber;

    public GrabTargetGoal(IGrabberEntity grabber, double speedModifier, boolean visualPersistence) {
        super(grabber.asMob(), speedModifier, visualPersistence);

        this.grabber = grabber;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity target, double distanceSq) {
        var ability = grabber.getGrabAbility();
        if (ability == null) {
            grabber.asMob().setTarget(null);
            return;
        }

        double reachSqr = this.getAttackReachSqr(target) * 0.9;

        // dentro do alcance e pronto para atacar
        if (distanceSq <= reachSqr && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();

            // Faz o GRAB normal (não suit)
            ability.grabEntity(target);

            // manda packet de GRAB (tipo ARMS)
            ChangedAddonMod.PACKET_HANDLER.send(
                    PacketDistributor.TRACKING_ENTITY.with(grabber::asMob),
                    new DynamicGrabEntityPacket(grabber.asMob(), target, DynamicGrabEntityPacket.GrabType.ARMS)
            );

            grabber.asMob().setTarget(null);

            // som (opcional, pode mudar)
            ChangedSounds.broadcastSound(
                    grabber.asMob(),
                    ChangedSounds.BLOW1,
                    1.0f,
                    1.0f
            );
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity target = grabber.getGrabTarget();
        if (target == null)
            return false;

        var ability = grabber.getGrabAbility();
        if (ability == null)
            return false;

        if (ability.grabbedEntity == target)
            return false;

        grabber.asMob().setTarget(target);
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = grabber.getGrabTarget();
        if (target == null)
            return false;

        var ability = grabber.getGrabAbility();
        if (ability == null)
            return false;

        if (ability.grabbedEntity == null)
            return false;

        grabber.asMob().setTarget(target);
        return super.canContinueToUse();
    }
}
