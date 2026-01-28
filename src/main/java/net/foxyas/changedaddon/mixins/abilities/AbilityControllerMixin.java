package net.foxyas.changedaddon.mixins.abilities;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AbstractAbility.Controller.class, remap = false)
public abstract class AbilityControllerMixin { /* implements AbilityControllerExtension {

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
    }*/
}
