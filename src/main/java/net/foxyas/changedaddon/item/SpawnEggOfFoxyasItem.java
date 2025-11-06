package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class SpawnEggOfFoxyasItem extends SpecialSpawnEggItem {

    public SpawnEggOfFoxyasItem() {
        super(ChangedAddonEntities.LATEX_SNOW_FOX_FOXYAS, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).stacksTo(64).rarity(Rarity.COMMON));
    }
}
