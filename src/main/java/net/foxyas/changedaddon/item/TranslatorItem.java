package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TranslatorItem extends Item {

    private static final String TAG_ENABLED = "Enabled";

    public TranslatorItem() {
        super(new Properties()
                .tab(ChangedAddonTabs.CHANGED_ADDON_MAIN_TAB)
                .stacksTo(64)
                .rarity(Rarity.COMMON));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            ChangedSounds.broadcastSound(player, ChangedSounds.KEY, 1, 1);
            toggle(stack);
            player.displayClientMessage(
                    new TranslatableComponent(
                            isEnabled(stack)
                                    ? "item.changedaddon.translator.on"
                                    : "item.changedaddon.translator.off"
                    ),
                    true
            );
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
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

