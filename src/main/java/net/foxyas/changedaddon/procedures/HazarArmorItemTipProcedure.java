package net.foxyas.changedaddon.procedures;

//@Mod.EventBusSubscriber
public class HazarArmorItemTipProcedure {
    /*@SubscribeEvent
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

                event.addModifier(ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.parse("changed:transfur_damage")), HazardDebuff3);
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
    }*/
}
