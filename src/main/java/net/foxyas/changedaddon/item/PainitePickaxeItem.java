package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;

import static net.foxyas.changedaddon.init.ChangedAddonItemTiers.PAINITE;

public class PainitePickaxeItem extends PickaxeItem {
    public PainitePickaxeItem() {
        super(PAINITE, 1, -2.8f, new Item.Properties().tab(ChangedAddonTabs.CHANGED_ADDON_OPTIONAL_COMBAT_TAB).fireResistant());
    }
}
