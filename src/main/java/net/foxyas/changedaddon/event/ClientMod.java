package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.gui.*;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.foxyas.changedaddon.item.tooltip.ClientTransfurTotemTooltipComponent;
import net.foxyas.changedaddon.item.tooltip.TransfurTotemTooltipComponent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientMod {

    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ChangedAddonMenus.FOXYAS_GUI, FoxyasguiScreen::new);
            MenuScreens.register(ChangedAddonMenus.GENERATORGUI, GeneratorguiScreen::new);
            MenuScreens.register(ChangedAddonMenus.CATALYZER_GUI, CatalyzerGuiScreen::new);
            MenuScreens.register(ChangedAddonMenus.UNIFUSER_GUI, UnifuserguiScreen::new);
            MenuScreens.register(ChangedAddonMenus.FOXYAS_GUI_2, FoxyasGui2Screen::new);
            MenuScreens.register(ChangedAddonMenus.TRANSFUR_SOUNDS_GUI, TransfurSoundsGuiScreen::new);
            MenuScreens.register(ChangedAddonMenus.INFORMANT_GUI, InformantGuiScreen::new);
            MenuScreens.register(ChangedAddonMenus.PROTOTYPE_MENU, PrototypeMenuScreen::new);
            MenuScreens.register(ChangedAddonMenus.MERCHANT_MENU, CustomMerchantScreen::new);

            MenuScreens.register(ChangedAddonMenus.TEST_FOXYAS_MENU.get(), FoxyasMenuScreen::new);
        });
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerToolTips(FMLConstructModEvent event) {
        MinecraftForgeClient.registerTooltipComponentFactory(TransfurTotemTooltipComponent.class, ClientTransfurTotemTooltipComponent::new);
    }
}
