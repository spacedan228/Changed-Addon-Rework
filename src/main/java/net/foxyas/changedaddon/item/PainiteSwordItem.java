package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class PainiteSwordItem extends SwordItem {
    public PainiteSwordItem() {
        super(new Tier() {
            public int getUses() {
                return 3026;
            }

            public float getSpeed() {
                return 12f;
            }

            public float getAttackDamageBonus() {
                return 4.5f;
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
        }, 3, -2.4f, new Item.Properties().tab(ChangedAddonTabs.CHANGED_ADDON_OPTIONAL_COMBAT_TAB).fireResistant());
    }
}
