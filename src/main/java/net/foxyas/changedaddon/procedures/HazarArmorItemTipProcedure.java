package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonAttributes;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

@Mod.EventBusSubscriber
public class HazarArmorItemTipProcedure {
    @SubscribeEvent
    public static void addAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();

        AttributeModifier HazardArmor3 = new AttributeModifier(UUID.fromString("0-0-0-0-2"), "Hazard Armor Buff", 0.05, AttributeModifier.Operation.ADDITION);
        AttributeModifier HazardDebuff = new AttributeModifier(UUID.fromString("0-0-0-0-3"), "Hazard Armor Speed DeBuff", (-0.05), AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeModifier HazardDebuff2 = new AttributeModifier(UUID.fromString("0-0-0-0-4"), "Hazard Armor Attack Speed DeBuff", (-0.09), AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (event.getSlotType() == EquipmentSlot.HEAD) {
            if (stack.getItem() == ChangedAddonItems.HAZARD_SUIT_HELMET.get()) {
                event.addModifier(ChangedAddonAttributes.LATEX_RESISTANCE.get(), HazardArmor3);
                event.addModifier(Attributes.MOVEMENT_SPEED, HazardDebuff);
            }
        }

        if (event.getSlotType() == EquipmentSlot.CHEST) {
            if (stack.getItem() == ChangedAddonItems.HAZARD_SUIT_CHESTPLATE.get()) {
                AttributeModifier HazardArmor = new AttributeModifier(UUID.fromString("0-0-0-0-0"), "Hazard Armor Buff", 0.2, AttributeModifier.Operation.ADDITION);
                AttributeModifier HazardDebuff3 = new AttributeModifier(UUID.fromString("0-0-0-0-5"), "Hazard Armor Transfur Dmg DeBuff", (-1), AttributeModifier.Operation.MULTIPLY_TOTAL);

                event.addModifier(ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("changed:transfur_damage")), HazardDebuff3);
                event.addModifier(ChangedAddonAttributes.LATEX_RESISTANCE.get(), HazardArmor);
                event.addModifier(Attributes.MOVEMENT_SPEED, HazardDebuff);
                event.addModifier(Attributes.ATTACK_SPEED, HazardDebuff2);
            }
        }

        if (event.getSlotType() == EquipmentSlot.LEGS) {
            if (stack.getItem() == ChangedAddonItems.HAZARD_SUIT_LEGGINGS.get()) {
                AttributeModifier HazardArmor2 = new AttributeModifier(UUID.fromString("0-0-0-0-1"), "Hazard Armor Buff", 0.15, AttributeModifier.Operation.ADDITION);

                event.addModifier(ChangedAddonAttributes.LATEX_RESISTANCE.get(), HazardArmor2);
                event.addModifier(Attributes.MOVEMENT_SPEED, HazardDebuff);
                event.addModifier(Attributes.ATTACK_SPEED, HazardDebuff2);
            }
        }

        if (event.getSlotType() == EquipmentSlot.FEET) {
            if (stack.getItem() == ChangedAddonItems.HAZARD_SUIT_BOOTS.get()) {
                event.addModifier(ChangedAddonAttributes.LATEX_RESISTANCE.get(), HazardArmor3);
                event.addModifier(Attributes.MOVEMENT_SPEED, HazardDebuff);
            }
        }
    }
}
