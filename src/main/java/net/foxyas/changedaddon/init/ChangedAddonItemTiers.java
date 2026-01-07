package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChangedAddonItemTiers {

//    @Deprecated
//    public static final Tier PAINITE = new Tier() {
//        public int getUses() {
//            return 3026;
//        }
//
//        public float getSpeed() {
//            return 12f;
//        }
//
//        public float getAttackDamageBonus() {
//            return 4.75f;
//        }
//
//        public int getLevel() {
//            return 5;
//        }
//
//        public int getEnchantmentValue() {
//            return 30;
//        }
//
//        public @NotNull Ingredient getRepairIngredient() {
//            return Ingredient.of(new ItemStack(ChangedAddonItems.PAINITE.get()));
//        }
//    };

    public static void init() {
        ResourceLocation netherite = ResourceLocation.parse("netherite");
        //TierSortingRegistry.registerTier(PAINITE, ChangedAddonMod.resourceLoc("painite"), List.of(netherite), List.of());
    }

}
