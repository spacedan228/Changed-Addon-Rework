package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class PainiteHoeItem extends HoeItem {
    public PainiteHoeItem() {
        super(new Tier() {
            public int getUses() {
                return 3046;
            }

            public float getSpeed() {
                return 15f;
            }

            public float getAttackDamageBonus() {
                return 1f;
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
        }, 0, -2f, new Item.Properties().fireResistant());
    }
}
