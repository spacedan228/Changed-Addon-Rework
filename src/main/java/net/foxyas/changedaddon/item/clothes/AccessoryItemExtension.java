package net.foxyas.changedaddon.item.clothes;

import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.minecraft.world.item.ItemStack;

public interface AccessoryItemExtension {

    default boolean IsAffectedByMending(AccessorySlotType slotType, ItemStack itemStack) {
        return false;
    }
}
