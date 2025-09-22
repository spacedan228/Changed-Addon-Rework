package net.foxyas.changedaddon.abilities.interfaces;

import net.foxyas.changedaddon.entity.interfaces.ChangedEntityExtension;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.ChangedEntity;

public interface AbilityControllerExtension {

    static AbilityControllerExtension of(AbstractAbility.Controller controller){
        return (AbilityControllerExtension) controller;
    }

    void resetCooldown();

    default boolean shouldReallyApplyCooldown() {
        return true;
    }
}
