package net.foxyas.changedaddon.item.clothes;

import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public interface AccessoryItemExtension {

    default boolean shouldBeAffectedByMending(AccessorySlotType slotType, ItemStack itemStack) {
        return false;
    }

    default boolean shouldBeConsideredByEnchantment(Enchantment enchantment, ItemStack itemStack, AccessorySlotType slotType, LivingEntity pEntity) {
        if (enchantment == Enchantments.MENDING) {
            return this.shouldBeAffectedByMending(slotType, itemStack);
        }

        return false;
    }

    default boolean isConsideredInSlots(ItemStack itemStack, AccessorySlotType slotType, LivingEntity livingEntity) {
        return false;
    }

    default boolean shouldBeConsideredIntoPostDamageEffects(ItemStack itemStack, AccessorySlotType slotType, LivingEntity livingEntity) {
        return false;
    }

    default boolean shouldBeConsideredIntoPostHurtEffects(ItemStack itemStack, AccessorySlotType slotType, LivingEntity livingEntity) {
        return false;
    }
}
