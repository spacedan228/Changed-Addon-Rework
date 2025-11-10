package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public abstract class PainiteArmorItem extends ArmorItem {
    public PainiteArmorItem(EquipmentSlot slot, Item.Properties properties) {
        super(new ArmorMaterial() {

            final int[] durability = new int[]{13, 15, 16, 11};
            final int[] defence = new int[]{3, 6, 8, 3};

            @Override
            public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {
                return durability[slot.getIndex()] * 40;
            }

            @Override
            public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
                return defence[slot.getIndex()];
            }

            @Override
            public int getEnchantmentValue() {
                return 18;
            }

            @Override
            public @NotNull SoundEvent getEquipSound() {
                return SoundEvents.ARMOR_EQUIP_NETHERITE;
            }

            @Override
            public @NotNull Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(ChangedAddonItems.PAINITE.get()), new ItemStack(Items.NETHERITE_INGOT));
            }

            @Override
            public @NotNull String getName() {
                return "painite_armor";
            }

            @Override
            public float getToughness() {
                return 3f;
            }

            @Override
            public float getKnockbackResistance() {
                return 0.1f;
            }
        }, slot, properties);
    }

    public static class Helmet extends PainiteArmorItem {
        public Helmet() {
            super(EquipmentSlot.HEAD, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON_COMBAT_OPTIONAL).fireResistant());
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "changed_addon:textures/models/armor/netherite_with_painite_trim__layer_1.png";
        }
    }

    public static class Chestplate extends PainiteArmorItem {
        public Chestplate() {
            super(EquipmentSlot.CHEST, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON_COMBAT_OPTIONAL).fireResistant());
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "changed_addon:textures/models/armor/netherite_with_painite_trim__layer_1.png";
        }
    }

    public static class Leggings extends PainiteArmorItem {
        public Leggings() {
            super(EquipmentSlot.LEGS, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON_COMBAT_OPTIONAL).fireResistant());
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "changed_addon:textures/models/armor/netherite_with_painite_trim__layer_2.png";
        }
    }

    public static class Boots extends PainiteArmorItem {
        public Boots() {
            super(EquipmentSlot.FEET, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON_COMBAT_OPTIONAL).fireResistant());
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "changed_addon:textures/models/armor/netherite_with_painite_trim__layer_1.png";
        }
    }
}
