package net.foxyas.changedaddon.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.foxyas.changedaddon.ability.ToggleClimbAbilityInstance;
import net.foxyas.changedaddon.entity.api.ExtraConditions;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.foxyas.changedaddon.item.clothes.AccessoryItemExtension;
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

}