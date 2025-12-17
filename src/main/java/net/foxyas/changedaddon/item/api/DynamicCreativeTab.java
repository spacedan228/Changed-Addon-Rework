package net.foxyas.changedaddon.item.api;

import net.foxyas.changedaddon.item.clothes.DyeableClothingItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface DynamicCreativeTab {

    default void fillItemCategory(@NotNull CreativeModeTab.Output tab) {};
}
