package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class LaethinItem extends Item {
    public LaethinItem() {
        super(new Item.Properties()//.tab(ChangedAddonTabs.CHANGED_ADDON_MAIN_TAB)
                .stacksTo(64).rarity(Rarity.RARE));
    }
}
