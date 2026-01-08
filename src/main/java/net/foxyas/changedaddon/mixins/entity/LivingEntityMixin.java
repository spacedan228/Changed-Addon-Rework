package net.foxyas.changedaddon.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.foxyas.changedaddon.ability.ToggleClimbAbilityInstance;
import net.foxyas.changedaddon.entity.api.ExtraConditions;
import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.foxyas.changedaddon.item.clothes.AccessoryItemExtension;
import net.foxyas.changedaddon.variant.VariantExtraStats;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

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

    @ModifyExpressionValue(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z",
                    remap = false
            )
    )
    private boolean changedaddon$canElytraFlyRedirect(boolean original) {
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull((LivingEntity)(Object)this))
                .map(latexVariant -> {
                    if (latexVariant.getChangedEntity() instanceof VariantExtraStats extra) {
                        return extra.getFlyType().canGlide();
                    }
                    return latexVariant.getParent().canGlide || original;
                })
                .orElse(original);
    }

    @ModifyExpressionValue(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;elytraFlightTick(Lnet/minecraft/world/entity/LivingEntity;I)Z",
                    remap = false
            )
    )
    private boolean changedaddon$elytraFlightTickRedirect(boolean original) {
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull((LivingEntity)(Object)this))
                .map(latexVariant -> {
                    if (latexVariant.getChangedEntity() instanceof VariantExtraStats extra) {
                        return extra.getFlyType().canGlide() || original;
                    }
                    return latexVariant.getParent().canGlide || original;
                })
                .orElse(original);
    }

    @WrapOperation(method = "getDamageAfterMagicAbsorb",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageProtection(Ljava/lang/Iterable;Lnet/minecraft/world/damagesource/DamageSource;)I"))
    public int andAccessorySlots(Iterable<ItemStack> armorSlots, DamageSource damageSource, Operation<Integer> original) {
        MutableInt total = new MutableInt();

        LivingEntity self = (LivingEntity) (Object) this;
        AccessorySlots.getForEntity(self).ifPresent((accessorySlots -> accessorySlots.forEachSlot((slot, itemStack) -> {
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof AccessoryItemExtension accessoryItem) {
                for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(itemStack).entrySet()) {
                    if (accessoryItem.isConsideredByEnchantment(new AccessorySlotContext<>(self, slot, itemStack), entry.getKey())) {
                        total.add(entry.getKey().getDamageProtection(entry.getValue(), damageSource));
                    }
                }
            }
        })));

        return total.intValue() + original.call(armorSlots, damageSource);
    }

    @Inject(method = "getScale", at = @At("RETURN"), cancellable = true)
    private void getScaleHook(CallbackInfoReturnable<Float> cir) {
        float originalValue = cir.getReturnValue();
        var self = (LivingEntity) (Object) this;
        if (self instanceof IAlphaAbleEntity iAlphaAbleEntity) {
            float alphaScale = iAlphaAbleEntity.alphaAdditionalScale();
            cir.setReturnValue(originalValue + alphaScale);
        }
    }
}