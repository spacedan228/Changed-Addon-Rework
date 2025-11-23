package net.foxyas.changedaddon.item.api;

import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.registries.RegistryObject;

public interface ColorHolder {

    void registerCustomColors(RegisterColorHandlersEvent.Item itemColors, RegistryObject<Item> item);
}
