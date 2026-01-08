package net.foxyas.changedaddon.mixins.entity.player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.foxyas.changedaddon.entity.api.LivingEntityDataExtensor;
import net.foxyas.changedaddon.variant.VariantExtraStats;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player implements LivingEntityDataExtensor {

    public LocalPlayerMixin(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(pLevel, pPos, pYRot, pGameProfile);
    }

    @ModifyExpressionValue(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z",
                    remap = false
            )
    )
    private boolean changedaddon$canElytraFlyRedirect(boolean original) {
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(this))
                .map(latexVariant -> {
                    if (latexVariant.getChangedEntity() instanceof VariantExtraStats extra) {
                        return extra.getFlyType().canGlide() || original;
                    }
                    return latexVariant.getParent().canGlide || original;
                })
                .orElse(original);
    }

    /*@Inject(method = "isUnderWater" ,at = @At("RETURN"), cancellable = true)
    private void customIsUnderWater(CallbackInfoReturnable<Boolean> cir) {
        Boolean returnValue = cir.getReturnValue();
        if (returnValue != null) {
            if (!returnValue) {
                cir.setReturnValue(canOverrideSwim());
            }
        }
    }*/

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
