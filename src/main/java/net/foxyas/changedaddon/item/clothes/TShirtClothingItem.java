package net.foxyas.changedaddon.item.clothes;

import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TShirtClothingItem extends DyeableClothingItem implements DyeableLeatherItem {

    public TShirtClothingItem() {
        super();
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        this.setColor(stack, 0xffffff);
        return stack;
    }
}