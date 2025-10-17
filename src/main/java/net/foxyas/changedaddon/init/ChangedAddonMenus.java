package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.menu.CustomMerchantMenu;
import net.foxyas.changedaddon.menu.FoxyasMenu;
import net.foxyas.changedaddon.menu.PrototypeMenu;
import net.foxyas.changedaddon.world.inventory.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedAddonMenus {//TODO use actual registry?
    private static final List<MenuType<?>> LIST = new ArrayList<>();

    private static <T extends AbstractContainerMenu> MenuType<T> register(String registryname, IContainerFactory<T> containerFactory) {
        MenuType<T> menuType = new MenuType<>(containerFactory);
        menuType.setRegistryName(registryname);
        LIST.add(menuType);
        return menuType;
    }

    public static final MenuType<FoxyasGuiMenu> FOXYAS_GUI = register("foxyas_gui", FoxyasGuiMenu::new);

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().registerAll(LIST.toArray(new MenuType[0]));
    }

    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, ChangedAddonMod.MODID);

    public static final RegistryObject<MenuType<FoxyasMenu>> TEST_FOXYAS_MENU = REGISTRY.register("test_foxyas_menu", () -> IForgeMenuType.create(FoxyasMenu::new));


    public static final MenuType<GeneratorGuiMenu> GENERATORGUI = register("generator_gui", GeneratorGuiMenu::new);
    public static final MenuType<CatalyzerGuiMenu> CATALYZER_GUI = register("catalyzer_gui", CatalyzerGuiMenu::new);
    public static final MenuType<UnifuserGuiMenu> UNIFUSER_GUI = register("unifuser_gui", UnifuserGuiMenu::new);
    public static final MenuType<FoxyasGui2Menu> FOXYAS_GUI_2 = register("foxyas_gui_2", FoxyasGui2Menu::new);
    public static final MenuType<TransfurSoundsGuiMenu> TRANSFUR_SOUNDS_GUI = register("transfur_sounds_gui", TransfurSoundsGuiMenu::new);
    public static final MenuType<InformantGuiMenu> INFORMANT_GUI = register("informant_gui", InformantGuiMenu::new);

    public static final MenuType<PrototypeMenu> PROTOTYPE_MENU = register("prototype_menu", PrototypeMenu::new);

    public static final MenuType<CustomMerchantMenu> MERCHANT_MENU = register("merchant_menu", CustomMerchantMenu::new);
}
