package net.foxyas.changedaddon.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TransfurAspectEnchantment extends Enchantment {

    public TransfurAspectEnchantment(EquipmentSlot... pApplicableSlots) {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, pApplicableSlots);
    }
}
