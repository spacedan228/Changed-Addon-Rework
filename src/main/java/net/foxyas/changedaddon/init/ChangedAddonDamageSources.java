package net.foxyas.changedaddon.init;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;

public class ChangedAddonDamageSources {

    public static final DamageSource LATEX_SOLVENT = new DamageSource("latex_solvent");

    public static EntityDamageSource mobLatesSolventAttack(LivingEntity mob) {
        return new EntityDamageSource("latex_solvent", mob);
    }
}
