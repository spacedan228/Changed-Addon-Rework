package net.foxyas.changedaddon.datagen.worldgen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.entity.simple.AbstractCheetahEntity;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.init.ChangedAddonFeatures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ParametersAreNonnullByDefault
public class BiomeModifierProvider {

    public static final ResourceKey<BiomeModifier> ADD_LATEX_CHEETAHS_SPAWNS =
            ChangedAddonMod.resourceKey(ForgeRegistries.Keys.BIOME_MODIFIERS, "add_latex_cheetahs_spawns");

    public static final ResourceKey<BiomeModifier> ADD_IRIDIUM_GEN =
            create("add_iridium");

    public static final ResourceKey<BiomeModifier> ADD_PAINITE_GEN =
            create("add_painite");

    public static void bootstrap(BootstapContext<BiomeModifier> context){
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        context.register(
                ADD_IRIDIUM_GEN,
                new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(List.of(
                                placedFeatures.getOrThrow(ChangedAddonFeatures.Placements.IRIDIUM_ORE_SMALL),
                                placedFeatures.getOrThrow(ChangedAddonFeatures.Placements.IRIDIUM_ORE_LARGE),
                                placedFeatures.getOrThrow(ChangedAddonFeatures.Placements.IRIDIUM_ORE_BURIED)
                        )),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        context.register(
                ADD_PAINITE_GEN,
                new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(ChangedAddonFeatures.Placements.PAINITE_ORE_PLACED)),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );

        addCheetahSpawns(context, biomes);
    }

    private static void addCheetahSpawns(BootstapContext<BiomeModifier> context, HolderGetter<Biome> biomes) {
        List<Holder<Biome>> latexCheetahSpawnBiomes = new ArrayList<>();
        AbstractCheetahEntity.getSpawnBiomes().forEach((biomeResourceKey) -> {
            latexCheetahSpawnBiomes.add(biomes.getOrThrow(biomeResourceKey));
        });

        List<Holder<Biome>> shortedCheetahSpawnBiomes = latexCheetahSpawnBiomes.stream().sorted(Comparator.comparing(biomeHolder -> biomeHolder.unwrapKey().get().location().getPath())).toList();

        context.register(ADD_LATEX_CHEETAHS_SPAWNS, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(shortedCheetahSpawnBiomes),
                List.of(
                        new MobSpawnSettings.SpawnerData(ChangedAddonEntities.LATEX_CHEETAH_MALE.get(), 20, 1, 4),
                        new MobSpawnSettings.SpawnerData(ChangedAddonEntities.LATEX_CHEETAH_FEMALE.get(), 20, 1, 4)
                ))
        );

    }

    // Utils for Future Spawn Biome Modifiers
    private static List<Holder<Biome>> getShortedBiomeList(List<Holder<Biome>> originalList) {
        return originalList.stream().sorted(Comparator.comparing(holder -> holder.unwrapKey().get().location().getPath())).toList();
    }

    private static ResourceKey<BiomeModifier> create(String id) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                ResourceLocation.fromNamespaceAndPath(ChangedAddonMod.MODID, id));
    }
}