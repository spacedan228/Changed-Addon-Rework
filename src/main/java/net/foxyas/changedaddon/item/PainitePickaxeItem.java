package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PainitePickaxeItem extends PickaxeItem {

    public static Tier PAINITE = new Tier() {
        public int getUses() {
            return 3026;
        }

        public float getSpeed() {
            return 12f;
        }

        public float getAttackDamageBonus() {
            return 4.75f;
        }

        public int getLevel() {
            return 5;
        }

        public int getEnchantmentValue() {
            return 30;
        }

        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(new ItemStack(ChangedAddonItems.PAINITE.get()));
        }
    };

    static {
        ResourceLocation netherite = new ResourceLocation("netherite");
        TierSortingRegistry.registerTier(PAINITE, ChangedAddonMod.resourceLoc("painite"), List.of(netherite), List.of());
    }


    public PainitePickaxeItem() {
        super(PAINITE, 1, -2.8f, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON_COMBAT_OPTIONAL).fireResistant());
    }
}
