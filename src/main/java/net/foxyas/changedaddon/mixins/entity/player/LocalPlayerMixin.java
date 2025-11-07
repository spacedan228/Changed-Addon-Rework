package net.foxyas.changedaddon.mixins.entity.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.foxyas.changedaddon.entity.api.LivingEntityDataExtensor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements LivingEntityDataExtensor {


    @Inject(method = "isUnderWater" ,at = @At("RETURN"), cancellable = true)
    private void customIsUnderWater(CallbackInfoReturnable<Boolean> cir) {
        Boolean returnValue = cir.getReturnValue();
        if (returnValue != null) {
            if (!returnValue) {
                cir.setReturnValue(canOverrideSwim());
            }
        }
    }

    /*@WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUnderWater()Z", shift = At.Shift.BY))
    private boolean aiStepHook(LocalPlayer instance, Operation<Boolean> original) {
        if (!original.call(instance)) {
            if (instance.isEyeInFluid(FluidTags.LAVA) && instance.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                return true;
            }
        }

        return original.call(instance);
    }

    @WrapOperation(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isInWater()Z"
            )
    )
    private boolean wrapIsInWaterForSwim(LocalPlayer instance, Operation<Boolean> original) {
        // se a condição custom for verdadeira, finge que está na água
        if (!original.call(instance)) {
            if (this.canOverrideSwim()) {
                return true;
            }
        }

        // caso contrário, executa o comportamento normal
        return original.call(instance);
    }


    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUnderWater()Z", shift = At.Shift.BY))
    private boolean aiStepHook(LocalPlayer instance) {
        if (!instance.isUnderWater()) {
            if (this.canOverrideSwim()) {
                return true;
            }
        }

        return instance.isUnderWater();
    }*/
}
