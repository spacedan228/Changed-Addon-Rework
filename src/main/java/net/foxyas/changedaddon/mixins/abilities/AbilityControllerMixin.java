package net.foxyas.changedaddon.mixins.abilities;

import net.foxyas.changedaddon.ability.api.AbilityControllerExtension;
import net.foxyas.changedaddon.ability.api.AbilityExtension;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractAbility.Controller.class, remap = false)
public class AbilityControllerMixin implements AbilityControllerExtension {

    @Shadow private int coolDownTicksRemaining;

    @Shadow @Final private AbstractAbilityInstance abilityInstance;

    @Override
    public void resetCooldown() {
        this.coolDownTicksRemaining = 0;
    }

    @Override
    public boolean shouldApplyCooldown() {
        if (abilityInstance instanceof AbilityExtension abilityExtension) {
            return abilityExtension.shouldApplyCooldown();
        }
        return AbilityControllerExtension.super.shouldApplyCooldown();
    }

    @Inject(method = "applyCoolDown", at = @At("TAIL"))
    public void injectApplyCooldown(CallbackInfo ci) {
        if (!this.shouldApplyCooldown()) {
            resetCooldown();
        }
    }
}
