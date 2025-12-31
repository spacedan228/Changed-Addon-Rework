package net.foxyas.changedaddon.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TranslatorItem extends Item {

    private static final String TAG_ENABLED = "Enabled";

    public TranslatorItem() {
        super(new Properties()
                .stacksTo(64)
                .rarity(Rarity.COMMON));
    }

    /* ===== STATE ===== */

    public static boolean isEnabled(ItemStack stack) {
        return !stack.hasTag() || stack.getOrCreateTag().getBoolean(TAG_ENABLED);
    }

    public static void setEnabled(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(TAG_ENABLED, value);
    }

    public static void toggle(ItemStack stack) {
        setEnabled(stack, !isEnabled(stack));
    }
}

