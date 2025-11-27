package net.foxyas.changedaddon.datagen.worldgen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.entity.simple.AbstractCheetahEntity;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class BiomeModifierProvider {

    public static final ResourceKey<BiomeModifier> ADD_LATEX_CHEETAHS_SPAWNS = ChangedAddonMod.resourceKey(ForgeRegistries.Keys.BIOME_MODIFIERS, "add_latex_cheetahs_spawns");

    public static void bootstrap(BootstapContext<BiomeModifier> context){
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        
        addCheetahSpawns(context, biomes);
    }

    private static void addCheetahSpawns(BootstapContext<BiomeModifier> context, HolderGetter<Biome> biomes) {
        List<Holder<Biome>> latexCheetahSpawnBiomes = new ArrayList<>();
        AbstractCheetahEntity.getSpawnBiomes().forEach((biomeResourceKey) -> {
            latexCheetahSpawnBiomes.add(biomes.getOrThrow(biomeResourceKey));
        });

        context.register(ADD_LATEX_CHEETAHS_SPAWNS, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(latexCheetahSpawnBiomes),
                List.of(
                        new MobSpawnSettings.SpawnerData(ChangedAddonEntities.LATEX_CHEETAH_MALE.get(), 20, 1, 4),
                        new MobSpawnSettings.SpawnerData(ChangedAddonEntities.LATEX_CHEETAH_FEMALE.get(), 20, 1, 4)
                ))
        );
    }
}