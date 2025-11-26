package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.ltxprogrammer.changed.item.SpecializedItemRendering;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.CompoundIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ElectricKatanaRedItem extends AbstractKatanaItem {

    private static final ModelResourceLocation GUI_MODEL =
            new ModelResourceLocation(ChangedAddonMod.resourceLoc("electric_katana_red_item_full"), "inventory");
    private static final ModelResourceLocation HANDLE_MODEL =
            new ModelResourceLocation(ChangedAddonMod.resourceLoc("electric_katana_red_3d"), "inventory");
    private static final ModelResourceLocation EMISSIVE_MODEL =
            new ModelResourceLocation(ChangedAddonMod.resourceLoc("electric_katana_red_laser"), "inventory");
    private static final ModelResourceLocation EMISSIVE_GUI_MODEL =
            new ModelResourceLocation(ChangedAddonMod.resourceLoc("electric_katana_red_glow"), "inventory");

    private static final ModelResourceLocation IN_HAND_MODEL =
            new ModelResourceLocation(ChangedAddonMod.resourceLoc("electric_katana_red_in_hand_model"), "inventory");

    public ElectricKatanaRedItem() {
        super(new Tier() {
            public int getUses() {
                return 1536;
            }

            public float getSpeed() {
                return 4f;
            }

            public float getAttackDamageBonus() {
                return 4f;
            }

            public int getLevel() {
                return 1;
            }

            public int getEnchantmentValue() {
                return 30;
            }

            public @NotNull Ingredient getRepairIngredient() {
                return CompoundIngredient.of(Ingredient.of(ItemTags.create(ResourceLocation.parse("changed_addon:tsc_katana_repair"))), Ingredient.of(new ItemStack(ChangedAddonItems.ELECTRIC_KATANA_RED.get())));
            }
        }, 3, -2.2f, new Item.Properties()
                //.tab(CreativeModeTab.TAB_COMBAT)
        );
    }

//    @Nullable
//    public ModelResourceLocation getEmissiveModelLocation(ItemStack itemStack, ItemTransforms.TransformType transformType) {
//        return transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.FIXED ? null
//                : transformType == ItemTransforms.TransformType.GROUND ? null : EMISSIVE_MODEL;
//    }

//    public ModelResourceLocation getModelLocation(ItemStack itemStack, ItemTransforms.TransformType transformType) {
//        return SpecializedItemRendering.isGUI(transformType) ? GUI_MODEL : HANDLE_MODEL;
//    }

    @Override
    public ResourceLocation getModelLocation(ItemStack itemStack, ItemDisplayContext type) {
        return SpecializedItemRendering.isGUI(type) ? null : IN_HAND_MODEL;
    }

    @Override
    public void loadSpecialModels(Consumer<ResourceLocation> consumer) {
        consumer.accept(IN_HAND_MODEL);
        //consumer.accept(HANDLE_MODEL);
        //consumer.accept(EMISSIVE_MODEL);
        //consumer.accept(GUI_MODEL);
        //consumer.accept(EMISSIVE_GUI_MODEL);
    }
}
