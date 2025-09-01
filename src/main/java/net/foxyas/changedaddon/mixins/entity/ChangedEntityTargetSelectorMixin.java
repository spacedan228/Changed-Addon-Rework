package net.foxyas.changedaddon.mixins.entity;

import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.entity.advanced.LuminaraFlowerBeastEntity;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.item.armor.DarkLatexCoatItem;
import net.foxyas.changedaddon.util.FoxyasUtils;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexWolf;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ChangedEntity.class, remap = false)
public class ChangedEntityTargetSelectorMixin {

    @Unique
    private static boolean isDarkLatexCoat(ItemStack itemStack) {
        return itemStack != null
                && !itemStack.isEmpty()
                && itemStack.getItem() instanceof DarkLatexCoatItem;
    }

    @Inject(method = "targetSelectorTest", at = @At("HEAD"), cancellable = true)
    private void CancelTarget(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        ItemStack Head = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack Chest = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if (ChangedAddonServerConfiguration.DL_COAT_AFFECT_ALL.get()) {
            if (isDarkLatexCoat(Head) && isDarkLatexCoat(Chest)) {
                //ChangedAddonMod.LOGGER.info("Evento cancelado: capacete e peitoral detectados");
                cir.setReturnValue(false);
            } else if (isDarkLatexCoat(Head) ^ isDarkLatexCoat(Chest)) {
                if (livingEntity.distanceTo((ChangedEntity) (Object) this) >= 4) {
                    //ChangedAddonMod.LOGGER.info("Evento cancelado: item parcial detectado, distância > 4");
                    cir.setReturnValue(false);
                }
            }
        } else {
            if ((ChangedEntity) (Object) this instanceof AbstractDarkLatexWolf) {
                if (isDarkLatexCoat(Head) && isDarkLatexCoat(Chest)) {
                    //ChangedAddonMod.LOGGER.info("Evento cancelado: capacete e peitoral detectados");
                    cir.setReturnValue(false);
                } else if (isDarkLatexCoat(Head) ^ isDarkLatexCoat(Chest)) {
                    if (livingEntity.distanceTo((ChangedEntity) (Object) this) >= 4) {
                        //ChangedAddonMod.LOGGER.info("Evento cancelado: item parcial detectado, distância > 4");
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

    @Inject(method = "targetSelectorTest", at = @At("HEAD"), cancellable = true)
    private void injectTargetSelector(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (self().hasEffect(ChangedAddonMobEffects.PACIFIED.get())) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private ChangedEntity self() {
        return (ChangedEntity) (Object) this;
    }
}
