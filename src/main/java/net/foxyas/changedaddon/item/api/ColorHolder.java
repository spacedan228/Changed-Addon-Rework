package net.foxyas.changedaddon.item.api;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public interface ColorHolder {

    void registerCustomColors(ItemColors itemColors, RegistryObject<Item> item);
}
