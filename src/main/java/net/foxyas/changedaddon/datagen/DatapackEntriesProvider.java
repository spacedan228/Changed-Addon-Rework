package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.datagen.worldgen.BiomeModifierProvider;
import net.foxyas.changedaddon.datagen.worldgen.ConfiguredFeatureProvider;
import net.foxyas.changedaddon.datagen.worldgen.PlacedFeatureProvider;
import net.foxyas.changedaddon.init.ChangedAddonDamageSources;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
public class DatapackEntriesProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.BIOME, DatapackEntriesProvider::biome)
            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatureProvider::bootstrap)
            .add(Registries.PLACED_FEATURE, PlacedFeatureProvider::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifierProvider::bootstrap)
            .add(Registries.DAMAGE_TYPE, DatapackEntriesProvider::damageType);

    public DatapackEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ChangedAddonMod.MODID));
    }

    private static void damageType(BootstapContext<DamageType> context){
        context.register(ChangedAddonDamageSources.LATEX_SOLVENT.key(), new DamageType("latex_solvent", DamageScaling.NEVER, 0.1f));
        context.register(ChangedAddonDamageSources.CONSCIENCE_LOSE.key(), new DamageType("conscience_lose", DamageScaling.NEVER, 0));
    }

    private static void biome(BootstapContext<Biome> context){
    }
}