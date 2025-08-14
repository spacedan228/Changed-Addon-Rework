package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonSounds;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;

public class MeaninglessStrafeMusicDiscItem extends RecordItem {
    public MeaninglessStrafeMusicDiscItem() {
        super(15, ChangedAddonSounds.EXP9_THEME, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).stacksTo(1).rarity(Rarity.RARE));
    }
}
