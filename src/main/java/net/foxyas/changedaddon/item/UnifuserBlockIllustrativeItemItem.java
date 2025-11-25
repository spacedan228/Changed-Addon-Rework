package net.foxyas.changedaddon.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class UnifuserBlockIllustrativeItemItem extends Item {
    public UnifuserBlockIllustrativeItemItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }
}
