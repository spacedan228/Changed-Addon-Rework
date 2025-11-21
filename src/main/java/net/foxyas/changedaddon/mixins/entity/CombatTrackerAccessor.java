package net.foxyas.changedaddon.mixins.entity;

import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CombatTracker.class)
public interface CombatTrackerAccessor {

    @Accessor("takingDamage")
    boolean isTakingDamage();
}
