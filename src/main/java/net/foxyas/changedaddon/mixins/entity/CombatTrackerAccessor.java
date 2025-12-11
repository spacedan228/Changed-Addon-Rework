package net.foxyas.changedaddon.mixins.entity;

import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CombatTracker.class)
public interface CombatTrackerAccessor {

    @Accessor("takingDamage")
    boolean isTakingDamage();

    @Accessor("inCombat")
    boolean isInCombat();

    @Accessor("entries")
    List<CombatEntry> getEntries();
}
