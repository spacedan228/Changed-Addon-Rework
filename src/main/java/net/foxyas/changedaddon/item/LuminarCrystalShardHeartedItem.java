package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class LuminarCrystalShardHeartedItem extends Item {
    public LuminarCrystalShardHeartedItem() {
        super(new Item.Properties().tab(ChangedAddonTabs.CHANGED_ADDON_MAIN_TAB).stacksTo(64).rarity(Rarity.RARE));
    }
}
