package net.foxyas.changedaddon.entity.api;

import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

public interface IGrabberEntity {
    PathfinderMob asMob();

    LivingEntity getGrabTarget();

    GrabEntityAbilityInstance getGrabAbilityInstance();

    default GrabEntityAbilityInstance createGrabAbility() {
        if (this instanceof ChangedEntity changedEntity) {
            return new GrabEntityAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), IAbstractChangedEntity.forEntity(changedEntity));
        } else return null;
    }

    default void mayTickGrabAbility() {
        GrabEntityAbilityInstance grabAbilityInstance = this.getGrabAbilityInstance();
        if (grabAbilityInstance != null) {
            if (grabAbilityInstance.getController().getHoldTicks() > 0 && (grabAbilityInstance.canUse() && grabAbilityInstance.canKeepUsing())) {
                grabAbilityInstance.tick();
            }

            grabAbilityInstance.tickIdle();
        }
    }
}
