package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.foxyas.changedaddon.init.ChangedAddonItems.*;

public class ChangedAddonTabs {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ChangedAddonMod.MODID);

    public static final RegistryObject<CreativeModeTab> CHANGED_ADDON_MAIN_TAB = TABS.register("main_tab", () -> CreativeModeTab.builder()
            .icon(()-> ChangedAddonItems.CHANGED_BOOK.get().getDefaultInstance())
            .displayItems((params, items) -> {
                //TODO add items
            }).build());//CHANGED_BOOK can be replaced with .withTabsImage(ResourceLocation tabsImage)

    public static final RegistryObject<CreativeModeTab> CHANGED_ADDON_OPTIONAL_COMBAT_TAB = TABS.register("optional_combat_tab", () -> CreativeModeTab.builder()
            .icon(()-> ChangedAddonItems.ELECTRIC_KATANA.get().getDefaultInstance())
            .displayItems((params, items) -> {
                items.accept(PAINITE_SWORD.get());
                items.accept(PAINITE_PICKAXE.get());
                items.accept(PAINITE_AXE.get());
                items.accept(PAINITE_SHOVEL.get());
                items.accept(PAINITE_HOE.get());
                items.accept(PAINITE_ARMOR_HELMET.get());
                items.accept(PAINITE_ARMOR_CHESTPLATE.get());
                items.accept(PAINITE_ARMOR_LEGGINGS.get());
                items.accept(PAINITE_ARMOR_BOOTS.get());

                items.accept(THE_DECIMATOR.get());
                //TODO add items
            }).build());
}
