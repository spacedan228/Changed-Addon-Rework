package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.item.armor.HazardBodySuit;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.ltxprogrammer.changed.item.ClothingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HazmatElementDisplayOverlayProcedure {
    public static boolean run(Entity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        if (entity == null) {
            return false;
        }
        if (!minecraft.options.getCameraType().isFirstPerson()) return false;
        assert minecraft.player != null;
        if (AccessorySlots.getForEntity(minecraft.player).isPresent()) {
            AccessorySlots accessorySlots = AccessorySlots.getForEntity(minecraft.player).get();
            Optional<ItemStack> item = accessorySlots.getItem(ChangedAccessorySlots.FULL_BODY.get());
            if (item.isPresent()) {
                ItemStack stack = item.get();
                if (stack.getItem() instanceof HazardBodySuit hazardBodySuit) {
                    return hazardBodySuit.getClothingState(stack).getValue(HazardBodySuit.HELMET);
                }
            }
        }


        return minecraft.player.getItemBySlot(EquipmentSlot.HEAD).is(ChangedAddonItems.HAZARD_SUIT_HELMET.get())
                || minecraft.player.getItemBySlot(EquipmentSlot.HEAD).is(ChangedAddonItems.HAZMAT_SUIT_HELMET.get());
    }
}
