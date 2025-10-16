package net.foxyas.changedaddon.item.armor;

import net.foxyas.changedaddon.client.model.ModelHazardArmorCustomArms;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public abstract class HazardSuitItem extends ArmorItem {
    public HazardSuitItem(EquipmentSlot slot, Item.Properties properties) {
        super(new ArmorMaterial() {
            @Override
            public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {
                return new int[]{13, 15, 16, 11}[slot.getIndex()] * 25;
            }

            @Override
            public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
                return new int[]{1, 4, 5, 1}[slot.getIndex()];
            }

            @Override
            public int getEnchantmentValue() {
                return 9;
            }

            @Override
            public @NotNull SoundEvent getEquipSound() {
                return ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.parse("changed_addon:armor_equip"));
            }

            @Override
            public @NotNull Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(Items.LEATHER), new ItemStack(Items.IRON_INGOT));
            }

            @Override
            public @NotNull String getName() {
                return "hazard_suit";
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

    public static class Helmet extends HazardSuitItem {
        public Helmet() {
            super(EquipmentSlot.HEAD, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON));
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "changed_addon:textures/models/armor/hazard_armor__layer_1.png";
        }
    }

    public static class Chestplate extends HazardSuitItem {
        public Chestplate() {
            super(EquipmentSlot.CHEST, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON));
        }

        public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
            consumer.accept(new IItemRenderProperties() {
                @Override
                @OnlyIn(Dist.CLIENT)
                public HumanoidModel getArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                    HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
                            Map.of("body", new ModelHazardArmorCustomArms(Minecraft.getInstance().getEntityModels().bakeLayer(ModelHazardArmorCustomArms.LAYER_LOCATION)).body, "left_arm",
                                    new ModelHazardArmorCustomArms(Minecraft.getInstance().getEntityModels().bakeLayer(ModelHazardArmorCustomArms.LAYER_LOCATION)).left_arm, "right_arm",
                                    new ModelHazardArmorCustomArms(Minecraft.getInstance().getEntityModels().bakeLayer(ModelHazardArmorCustomArms.LAYER_LOCATION)).right_arm, "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                    "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg",
                                    new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
                    armorModel.crouching = living.isShiftKeyDown();
                    armorModel.riding = defaultModel.riding;
                    armorModel.young = living.isBaby();
                    return armorModel;
                }
            });
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "changed_addon:textures/models/armor/hazard_armor__layer_1.png";
        }
    }

    public static class Leggings extends HazardSuitItem {
        public Leggings() {
            super(EquipmentSlot.LEGS, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON));
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "changed_addon:textures/entities/hazardarmor_legs.png";
        }
    }

    public static class Boots extends HazardSuitItem {
        public Boots() {
            super(EquipmentSlot.FEET, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON));
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "changed_addon:textures/models/armor/hazard_armor__layer_1.png";
        }
    }
}
