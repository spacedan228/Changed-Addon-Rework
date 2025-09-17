package net.foxyas.changedaddon.enchantment;

import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SolventEnchantment extends Enchantment {
    public SolventEnchantment(EquipmentSlot... slots) {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, slots);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment ench) {
        return this != ench && !Objects.equals(Enchantments.SHARPNESS, ench);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack itemstack) {
        return itemstack.is(ChangedAddonTags.Items.LATEX_SOLVENT_APPLICABLE)
                || itemstack.getItem() instanceof SwordItem
                || itemstack.getItem() instanceof AxeItem;
    }
}
