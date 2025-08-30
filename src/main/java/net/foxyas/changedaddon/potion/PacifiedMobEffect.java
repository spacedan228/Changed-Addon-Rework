package net.foxyas.changedaddon.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class PacifiedMobEffect extends MobEffect {

    public PacifiedMobEffect() {
        super(MobEffectCategory.NEUTRAL, -1);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "effect.changed_addon.pacified";
    }
}
