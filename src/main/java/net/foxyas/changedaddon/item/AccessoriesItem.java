package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.client.model.ModelAccessories;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public abstract class AccessoriesItem extends ArmorItem {
    public AccessoriesItem(EquipmentSlot slot, Item.Properties properties) {
        super(new ArmorMaterial() {
            @Override
            public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {
                return new int[]{13, 15, 16, 11}[slot.getIndex()] * 45;
            }

            @Override
            public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
                return new int[]{0, 0, 2, 0}[slot.getIndex()];
            }

            @Override
            public int getEnchantmentValue() {
                return 35;
            }

            @Override
            public @NotNull SoundEvent getEquipSound() {
                return ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.parse("changed_addon:armor_equip"));
            }

            @Override
            public @NotNull Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(ChangedAddonItems.PAINITE.get()));
            }

            @Override
            public @NotNull String getName() {
                return "accessories";
            }

            @Override
            public float getToughness() {
                return 0f;
            }

            @Override
            public float getKnockbackResistance() {
                return 0f;
            }
        }, slot, properties);
    }

    public static class Chestplate extends AccessoriesItem {
        public Chestplate() {
            super(EquipmentSlot.CHEST, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).fireResistant());
        }

        public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
            consumer.accept(new IItemRenderProperties() {
                @Override
                @OnlyIn(Dist.CLIENT)
                public HumanoidModel getArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                    HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(), Map.of("body", new ModelAccessories(Minecraft.getInstance().getEntityModels().bakeLayer(ModelAccessories.LAYER_LOCATION)).Colar, "left_arm",
                            new ModelAccessories(Minecraft.getInstance().getEntityModels().bakeLayer(ModelAccessories.LAYER_LOCATION)).LeftArmBracelet, "right_arm",
                            new ModelAccessories(Minecraft.getInstance().getEntityModels().bakeLayer(ModelAccessories.LAYER_LOCATION)).RightArmBracelet, "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "hat",
                            new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
                    armorModel.crouching = living.isShiftKeyDown();
                    armorModel.riding = defaultModel.riding;
                    armorModel.young = living.isBaby();
                    return armorModel;
                }
            });
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "changed_addon:textures/entities/painite.png";
        }
    }
}
