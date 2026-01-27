package net.foxyas.changedaddon.entity.api;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.world.entity.LivingEntity;

public interface IConditionalFuseEntity {

    default boolean canBeFusedBy(LivingEntity targetToFuse, IAbstractChangedEntity source, float amount) {
        return true;
    }
}
