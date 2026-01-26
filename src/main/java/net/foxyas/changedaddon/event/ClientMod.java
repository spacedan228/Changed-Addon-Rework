package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.gui.*;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.foxyas.changedaddon.item.tooltip.ClientTransfurTotemTooltipComponent;
import net.foxyas.changedaddon.item.tooltip.TransfurTotemTooltipComponent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientMod {

    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ChangedAddonMenus.GENERATORGUI.get(), GeneratorguiScreen::new);
            MenuScreens.register(ChangedAddonMenus.CATALYZER_GUI.get(), CatalyzerGuiScreen::new);
            MenuScreens.register(ChangedAddonMenus.UNIFUSER_GUI.get(), UnifuserGuiScreen::new);
            MenuScreens.register(ChangedAddonMenus.INFORMANT_MENU.get(), InformantGuiScreen::new);
            MenuScreens.register(ChangedAddonMenus.PROTOTYPE_MENU.get(), PrototypeMenuScreen::new);
            MenuScreens.register(ChangedAddonMenus.MERCHANT_MENU.get(), CustomMerchantScreen::new);
            MenuScreens.register(ChangedAddonMenus.FOXYAS_INVENTORY_MENU.get(), FoxyasInventoryMenuScreen::new);
            MenuScreens.register(ChangedAddonMenus.TIMED_KEYPAD_TIMER.get(), TimedKeypadTimerScreen::new);
        });
    }

    public static boolean changedAdditionsLoaded = false;
    public static boolean changedAdditionsWarningScreenShowed = false;

    @SubscribeEvent
    public static void showWarningScreen(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("changed_additions")) {
            changedAdditionsLoaded = true;
            changedAdditionsWarningScreenShowed = false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerToolTips(FMLConstructModEvent event) {
        MinecraftForgeClient.registerTooltipComponentFactory(TransfurTotemTooltipComponent.class, ClientTransfurTotemTooltipComponent::new);
    }
}
