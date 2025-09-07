package net.foxyas.changedaddon.entity.interfaces;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ChangedEntityExtension {

    default boolean isPacified() {
        return false;
    }

    default void setPacified(boolean value) {
    }

    static ChangedEntityExtension of(ChangedEntity entity){
        return (ChangedEntityExtension) entity;
    }

    static boolean isNeutralTo(ChangedEntity entity, LivingEntity target){
        return of(entity).c_additions$isNeutralTo(target);
    }

    boolean c_additions$isNeutralTo(LivingEntity target);
}
