package net.foxyas.changedaddon.mixins.entity;

import net.foxyas.changedaddon.ability.ToggleClimbAbilityInstance;
import net.foxyas.changedaddon.entity.api.ExtraConditions;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
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

}