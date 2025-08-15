package net.foxyas.changedaddon.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FriendlyGoeyIconItem extends Item {
    public FriendlyGoeyIconItem() {
        super(new Item.Properties().tab(null).stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemstack, Level world, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
        list.add(new TextComponent("\u00A74THIS IS ONE DEV ITEM YOU CAN'T DO ANYTHING WITH THIS"));
    }
}
