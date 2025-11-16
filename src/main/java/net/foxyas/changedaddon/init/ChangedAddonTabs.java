package net.foxyas.changedaddon.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChangedAddonTabs {

    public static final CreativeModeTab CHANGED_ADDON_MAIN_TAB;
    public static final CreativeModeTab CHANGED_ADDON_OPTIONAL_COMBAT_TAB;

    static {
        CHANGED_ADDON_MAIN_TAB = new CreativeModeTab("changed_addon_main_tab") {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(ChangedAddonItems.CHANGED_BOOK.get());
            }

            public boolean hasSearchBar() {
                return false;
            }
        };
        CHANGED_ADDON_OPTIONAL_COMBAT_TAB = new CreativeModeTab("changed_addon_optional_combat_tab") {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(ChangedAddonItems.ELECTRIC_KATANA.get());
            }

            public boolean hasSearchBar() {
                return false;
            }
        };
    }
}
