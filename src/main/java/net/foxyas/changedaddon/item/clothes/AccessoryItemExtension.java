package net.foxyas.changedaddon.item.clothes;

import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public interface AccessoryItemExtension {

    default boolean isConsideredByEnchantment(AccessorySlotContext<?> slotContext, Enchantment enchantment) {
        return false;
    }
}
