package net.foxyas.changedaddon.mixins.entity;

import net.foxyas.changedaddon.abilities.ToggleClimbAbilityInstance;
import net.foxyas.changedaddon.entity.interfaces.ExtraConditions;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.foxyas.changedaddon.villagerTrades.ChangedAddonTrades;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    public void onClimbable(CallbackInfoReturnable<Boolean> callback) {
        LivingEntity self = (LivingEntity) (Object) this;
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(self), (variant) -> {
            AbstractAbilityInstance instance = variant.getAbilityInstance(ChangedAddonAbilities.TOGGLE_CLIMB.get());
            if (variant.getParent().canClimb && self.horizontalCollision) {
                if (instance instanceof ToggleClimbAbilityInstance abilityInstance) {
                    if (variant.getChangedEntity() instanceof ExtraConditions.Climb climb) {
                        if (climb.canClimb()) {
                            callback.setReturnValue(abilityInstance.isActivated());
                        } else {
                            if (callback.getReturnValue() != null && callback.getReturnValue() != true) {
                                callback.setReturnValue(false);
                            }
                        }
                    }
                }
            }
        });
    }

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    public void canStandOnFluid(FluidState state, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity self = (LivingEntity) (Object) this;
        var variant = TransfurVariant.getEntityVariant(self);
        if (variant == null) return;
        if (variant.is(ChangedAddonTransfurVariants.LATEX_WIND_CAT_MALE) || variant.is(ChangedAddonTransfurVariants.LATEX_WIND_CAT_FEMALE)) {
            if (!self.isShiftKeyDown()) {
                callback.setReturnValue(true);
            }
        }
    }

}