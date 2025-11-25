package net.foxyas.changedaddon.mixins.fluids;

import net.foxyas.changedaddon.item.armor.HazardBodySuit;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.fluid.TransfurGas;
import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = TransfurGas.class, remap = false)
public class TransfurGasMixin {

    @Inject(method = "validEntityInGas", at = @At("HEAD"), remap = false, cancellable = true)
    private static void cancelTransfurGas(LivingEntity livingEntity, CallbackInfoReturnable<Optional<TransfurGas>> cir) {
        TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(livingEntity));
        if (variant == null) {
            Optional<AccessorySlots> slots = AccessorySlots.getForEntity(livingEntity);
            slots.ifPresent((accessorySlots) -> {
                Optional<ItemStack> item = accessorySlots.getItem(ChangedAccessorySlots.FULL_BODY.get());
                if (item.isPresent()) {
                    ItemStack itemStack = item.get();
                    if (itemStack.getItem() instanceof HazardBodySuit hazardBodySuit) {
                        if (hazardBodySuit.getClothingState(itemStack).getValue(HazardBodySuit.HELMET)) {
                            cir.setReturnValue(Optional.empty());
                        }
                    }
                }
            });
        }
    }
}
