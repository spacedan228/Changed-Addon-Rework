package net.foxyas.changedaddon.mixins.entity;

import net.foxyas.changedaddon.entity.api.LivingEntityDataExtensor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements LivingEntityDataExtensor {

    @Inject(method = "isInWater" ,at = @At("RETURN"), cancellable = true)
    private void customIsInWater(CallbackInfoReturnable<Boolean> cir) {
        Boolean returnValue = cir.getReturnValue();
        if (returnValue != null) {
            if (!returnValue) {
                cir.setReturnValue(canOverrideIsInWater());
            }
        }
    }

    @Inject(method = "isInLava" ,at = @At("RETURN"), cancellable = true)
    private void customIsInLava(CallbackInfoReturnable<Boolean> cir) {
        Boolean returnValue = cir.getReturnValue();
        if (returnValue != null) {
            if (returnValue) {
                cir.setReturnValue(canOverrideIsInLava());
            }
        }
    }
}
