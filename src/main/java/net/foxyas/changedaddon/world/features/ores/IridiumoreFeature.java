package net.foxyas.changedaddon.world.features.ores;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class IridiumoreFeature extends OreFeature {
    public static final Set<ResourceLocation> GENERATE_BIOMES = null;
    public static IridiumoreFeature FEATURE = null;
    public static Holder<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = null;
    public static Holder<PlacedFeature> PLACED_FEATURE = null;
    private final Set<ResourceKey<Level>> generate_dimensions = Set.of(Level.OVERWORLD);

    public IridiumoreFeature() {
        super(OreConfiguration.CODEC);
    }

    public static Feature<?> feature() {
        FEATURE = new IridiumoreFeature();
        // Usando os alvos vanilla (stone + deepslate)
        List<OreConfiguration.TargetBlockState> targets = List.of(OreConfiguration.target(IridiumoreFeatureRuleTest.INSTANCE, ChangedAddonBlocks.IRIDIUM_ORE.get().defaultBlockState()));
        CONFIGURED_FEATURE = Holder.direct(new ConfiguredFeature<>(Feature.ORE,
                new OreConfiguration(targets, 6) // vein size
        ));
        PLACED_FEATURE = Holder.direct(new PlacedFeature(CONFIGURED_FEATURE, List.of(CountPlacement.of(2), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(-60), VerticalAnchor.absolute(-45)), BiomeFilter.biome())));
        return FEATURE;
    }

    public static Holder<PlacedFeature> placedFeature() {
        return PLACED_FEATURE;
    }

    public boolean place(FeaturePlaceContext<OreConfiguration> context) {
        WorldGenLevel world = context.level();
        if (!generate_dimensions.contains(world.getLevel().dimension()))
            return false;
        return super.place(context);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class IridiumoreFeatureRuleTest extends RuleTest {
        static final IridiumoreFeatureRuleTest INSTANCE = new IridiumoreFeatureRuleTest();
        private static final com.mojang.serialization.Codec<IridiumoreFeatureRuleTest> CODEC = com.mojang.serialization.Codec.unit(() -> INSTANCE);
        public static final RuleTestType<IridiumoreFeatureRuleTest> CUSTOM_MATCH = () -> CODEC;

        @SubscribeEvent
        public static void init(FMLCommonSetupEvent event) {
            Registry.register(BuiltInRegistries.RULE_TEST, ResourceLocation.parse("changed_addon:iridium_ore_match"), CUSTOM_MATCH);
        }

        public boolean test(BlockState blockstate, @NotNull RandomSource random) {
            return Objects.equals(Blocks.DEEPSLATE, blockstate.getBlock());
        }

        protected @NotNull RuleTestType<?> getType() {
            return CUSTOM_MATCH;
        }
    }
}
