package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedAddonSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registry.SOUND_EVENT_REGISTRY, ChangedAddonMod.MODID);

    public static final RegistryObject<SoundEvent> ARMOR_EQUIP = registerSimple("armor_equip");
    public static final RegistryObject<SoundEvent> GECKO_BEEP = registerSimple("gecko_sound");
    public static final RegistryObject<SoundEvent> PLUSHY_SOUND = registerSimple("block.plushes.sfx");
    public static final RegistryObject<SoundEvent> SPRAY_SOUND = registerSimple("spray.sound");
    public static final RegistryObject<SoundEvent> UNTRANSFUR = registerSimple("untransfur.sound");
    public static final RegistryObject<SoundEvent> WARN = registerSimple("warn");

    public static final RegistryObject<SoundEvent> EXP10_THEME = registerSimple("experiment10_theme");
    public static final RegistryObject<SoundEvent> EXP9_THEME = registerSimple("music.boss.exp9");
    public static final RegistryObject<SoundEvent> LUMINARCTIC_LEOPARD = registerSimple("music.boss.luminarctic_leopard");
    public static final RegistryObject<SoundEvent> HAMMER_SWING = registerSimple("hammer_swing");
    public static final RegistryObject<SoundEvent> HAMMER_GUN_SHOT = registerSimple("hammer_gun_shot");

    public static final RegistryObject<SoundEvent> PROTOTYPE_IDEA = registerSimple("entity.prototype.idea_sfx");

    private static RegistryObject<SoundEvent> registerSimple(String path) {
        return SOUNDS.register(path, () -> new SoundEvent(ChangedAddonMod.resourceLoc(path)));
    }
}
