package net.foxyas.changedaddon.entity.goals.abilities;

import net.foxyas.changedaddon.ability.api.GrabEntityAbilityExtensor;
import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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
        var ability = grabber.getGrabAbilityInstance();
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

    @Override
    public boolean canUse() {
        LivingEntity target = grabber.getGrabbedEntity();
        if (target == null)
            return false;

        GrabEntityAbilityInstance ability = grabber.getGrabAbilityInstance();
        if (ability == null)
            return false;

        if (ability.grabbedEntity == target) {
            grabber.asMob().setTarget(null);
            return false;
        }

        if (ability instanceof GrabEntityAbilityExtensor abilityExtensor && abilityExtensor.getGrabCooldown() > 0) {
            return false;
        }

        Optional<IAbstractChangedEntity> grabberSafe = GrabEntityAbility.getGrabberSafe(target);
        if (grabberSafe.isPresent()) {
            grabber.asMob().setTarget(null);
            return false;
        }

        grabber.asMob().setTarget(target);
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = grabber.getGrabbedEntity();
        if (target == null)
            return false;

        var ability = grabber.getGrabAbilityInstance();
        if (ability == null)
            return false;

        if (ability.grabbedEntity == null)
            return false;

        grabber.asMob().setTarget(target);
        return super.canContinueToUse();
    }
}