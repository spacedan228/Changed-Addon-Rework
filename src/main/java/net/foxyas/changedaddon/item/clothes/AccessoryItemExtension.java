package net.foxyas.changedaddon.item.clothes;

import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public interface AccessoryItemExtension {

    default boolean isAffectedByMending(AccessorySlotType slotType, ItemStack itemStack) {
        return false;
    }

    default boolean isConsideredByEnchantment(Enchantment enchantment, ItemStack itemStack, AccessorySlotType slotType, LivingEntity pEntity) {
        if (enchantment == Enchantments.MENDING) {
            return this.isAffectedByMending(slotType, itemStack);
        }


        return false;
    }
}
