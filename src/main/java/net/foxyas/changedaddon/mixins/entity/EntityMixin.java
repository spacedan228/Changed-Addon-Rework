package net.foxyas.changedaddon.mixins.entity;

import net.foxyas.changedaddon.entity.api.LivingEntityDataExtensor;
import net.foxyas.changedaddon.item.clothes.AccessoryItemExtension;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public class EntityMixin implements LivingEntityDataExtensor {

    @Inject(method = "isInWater", at = @At("RETURN"), cancellable = true)
    private void customIsInWater(CallbackInfoReturnable<Boolean> cir) {
        Boolean returnValue = cir.getReturnValue();
        if (returnValue != null) {
            if (!returnValue) {
                cir.setReturnValue(overrideIsInWater());
            }
        }
    }

    @Inject(method = "getAllSlots", at = @At("RETURN"), cancellable = true)
    private void hookAccessoriesSlots(CallbackInfoReturnable<Iterable<ItemStack>> cir) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof LivingEntity livingEntity)) return;
        List<ItemStack> defaultStacks = new ArrayList<>();
        List<ItemStack> stacks = new ArrayList<>();

        AccessorySlots.getForEntity(livingEntity).ifPresent((slots) ->
                slots.forEachSlot((slotType, itemStack) -> {
                    if (itemStack.isEmpty()) return;
                    if (!(itemStack.getItem() instanceof AccessoryItemExtension accessoryItemExtension)) return;
                    if (accessoryItemExtension.isConsideredInSlots(itemStack, slotType, livingEntity)) {
                        stacks.add(itemStack);
                    }
                })
        );

        if (!stacks.isEmpty()) {
            Iterable<ItemStack> returnValue = cir.getReturnValue();
            if (returnValue != null) {
                returnValue.forEach(defaultStacks::add);
                stacks.addAll(defaultStacks);
                cir.setReturnValue(stacks);
            }
        }
    }
}
