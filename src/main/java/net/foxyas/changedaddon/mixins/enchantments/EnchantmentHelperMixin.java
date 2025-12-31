package net.foxyas.changedaddon.mixins.enchantments;

import net.foxyas.changedaddon.item.clothes.AccessoryItemExtension;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Redirect(method = "doPostDamageEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAllSlots()Ljava/lang/Iterable;"))
    private static Iterable<ItemStack> accessoriesDoPostDamageEffects(LivingEntity livingEntity) {
        Iterable<ItemStack> original = livingEntity.getAllSlots();
        List<ItemStack> defaultStacks = new ArrayList<>();
        List<ItemStack> stacks = new ArrayList<>();
        original.forEach(defaultStacks::add);

        AccessorySlots.getForEntity(livingEntity).ifPresent((slots) ->
                slots.forEachSlot((slotType, itemStack) -> {
                    if (itemStack.isEmpty()) return;
                    if (!(itemStack.getItem() instanceof AccessoryItemExtension accessoryItemExtension)) return;
                    if (accessoryItemExtension.isConsideredIntoPostDamageEffects(itemStack, slotType, livingEntity)) {
                        stacks.add(itemStack);
                    } else {
                        defaultStacks.remove(itemStack);
                    }
                })
        );

        if (!stacks.isEmpty()) {
            stacks.addAll(defaultStacks);
            return stacks;
        }
        return defaultStacks;
    }

    @Redirect(method = "doPostHurtEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAllSlots()Ljava/lang/Iterable;"))
    private static Iterable<ItemStack> accessoriesDoPostHurtEffects(LivingEntity livingEntity) {
        Iterable<ItemStack> original = livingEntity.getAllSlots();
        List<ItemStack> defaultStacks = new ArrayList<>();
        List<ItemStack> stacks = new ArrayList<>();
        original.forEach(defaultStacks::add);

        AccessorySlots.getForEntity(livingEntity).ifPresent((slots) ->
                slots.forEachSlot((slotType, itemStack) -> {
                    if (itemStack.isEmpty()) return;
                    if (!(itemStack.getItem() instanceof AccessoryItemExtension accessoryItemExtension)) return;
                    if (accessoryItemExtension.isConsideredIntoPostHurtEffects(itemStack, slotType, livingEntity)) {
                        stacks.add(itemStack);
                    } else {
                        defaultStacks.remove(itemStack);
                    }
                })
        );

        if (!stacks.isEmpty()) {
            stacks.addAll(defaultStacks);
            return stacks;
        }

        return defaultStacks;
    }
}
