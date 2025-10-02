package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.foxyas.changedaddon.init.ChangedAddonItemTiers.PAINITE;

public class PainitePickaxeItem extends PickaxeItem {
    public PainitePickaxeItem() {
        super(PAINITE, 1, -2.8f, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON_COMBAT_OPTIONAL).fireResistant());
    }
}
