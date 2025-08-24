package net.foxyas.changedaddon.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.jetbrains.annotations.NotNull;

public class FadigueMobEffect extends MobEffect {

    public FadigueMobEffect() {
        super(MobEffectCategory.NEUTRAL, -1);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "effect.changed_addon.fadigue";
    }
}
