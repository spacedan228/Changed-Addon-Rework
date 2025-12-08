package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.menu.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedAddonMenus {

    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, ChangedAddonMod.MODID);

    public static final RegistryObject<MenuType<FoxyasInventoryMenu>> FOXYAS_INVENTORY_MENU = register("foxyas_inventory_menu", FoxyasInventoryMenu::new);
    public static final RegistryObject<MenuType<GeneratorGuiMenu>> GENERATORGUI = register("generator_gui", GeneratorGuiMenu::new);
    public static final RegistryObject<MenuType<CatalyzerGuiMenu>> CATALYZER_GUI = register("catalyzer_gui", CatalyzerGuiMenu::new);
    public static final RegistryObject<MenuType<UnifuserGuiMenu>> UNIFUSER_GUI = register("unifuser_gui", UnifuserGuiMenu::new);
    public static final RegistryObject<MenuType<TransfurSoundsGuiMenu>> TRANSFUR_SOUNDS_GUI = register("transfur_sounds_gui", TransfurSoundsGuiMenu::new);
    public static final RegistryObject<MenuType<InformantGuiMenu>> INFORMANT_MENU = register("informant_gui", InformantGuiMenu::new);
    public static final RegistryObject<MenuType<PrototypeMenu>> PROTOTYPE_MENU = register("prototype_menu", PrototypeMenu::new);
    public static final RegistryObject<MenuType<CustomMerchantMenu>> MERCHANT_MENU = register("merchant_menu", CustomMerchantMenu::new);
    public static final RegistryObject<MenuType<TimedKeypadTimerMenu>> TIMED_KEYPAD_TIMER = register("timed_keypad_timer", TimedKeypadTimerMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String path, IContainerFactory<T> containerFactory) {
        return REGISTRY.register(path, () -> IForgeMenuType.create(containerFactory));
    }
}
