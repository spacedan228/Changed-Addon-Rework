package net.foxyas.changedaddon.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChangedAddonTabs {

    public static final CreativeModeTab TAB_CHANGED_ADDON;
    public static final CreativeModeTab TAB_CHANGED_ADDON_COMBAT_OPTIONAL;

    static {
        TAB_CHANGED_ADDON = new CreativeModeTab("tab_changed_addon") {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(ChangedAddonItems.CHANGED_BOOK.get());
            }

            public boolean hasSearchBar() {
                return false;
            }
        };
        TAB_CHANGED_ADDON_COMBAT_OPTIONAL = new CreativeModeTab("tab_changed_addon_combat_optional") {
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
