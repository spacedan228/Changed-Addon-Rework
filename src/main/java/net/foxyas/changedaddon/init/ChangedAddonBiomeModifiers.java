package net.foxyas.changedaddon.init;

import com.mojang.serialization.Codec;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.world.features.DynamicBiomeModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedAddonBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ChangedAddonMod.MODID);

    public static final RegistryObject<Codec<? extends BiomeModifier>> DYNAMIC_BIOME_MODIFIER =
            BIOME_MODIFIERS.register("dynamic_biome_modifier", () -> DynamicBiomeModifier.CODEC);
}