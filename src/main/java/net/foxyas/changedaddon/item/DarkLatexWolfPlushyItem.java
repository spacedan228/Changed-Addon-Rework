package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.foxyas.changedaddon.util.ComponentUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DarkLatexWolfPlushyItem extends BlockItem {
    public DarkLatexWolfPlushyItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    public DarkLatexWolfPlushyItem() {
        this(ChangedAddonBlocks.DARK_LATEX_WOLF_PLUSH.get(), new Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON));
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        if (armorType == EquipmentSlot.HEAD) {
            return true;
        }

        return super.canEquip(stack, armorType, entity);
    }
}
