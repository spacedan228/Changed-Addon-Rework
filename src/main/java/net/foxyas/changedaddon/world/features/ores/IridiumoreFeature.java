package net.foxyas.changedaddon.world.features.ores;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.Set;

public class IridiumoreFeature extends OreFeature {
    private final Set<ResourceKey<Level>> generate_dimensions = Set.of(Level.OVERWORLD);

    public IridiumoreFeature() {
        super(OreConfiguration.CODEC);
    }

    public boolean place(FeaturePlaceContext<OreConfiguration> context) {
        WorldGenLevel world = context.level();
        if (!generate_dimensions.contains(world.getLevel().dimension()))
            return false;
        return super.place(context);
    }

//    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
//    public static class IridiumoreFeatureRuleTest extends RuleTest {
//        public static final IridiumoreFeatureRuleTest INSTANCE = new IridiumoreFeatureRuleTest();
//        private static final com.mojang.serialization.Codec<IridiumoreFeatureRuleTest> CODEC = com.mojang.serialization.Codec.unit(() -> INSTANCE);
//        public static final RuleTestType<IridiumoreFeatureRuleTest> CUSTOM_MATCH = () -> CODEC;
//
//        @SubscribeEvent
//        public static void init(FMLCommonSetupEvent event) {
//            Registry.register(BuiltInRegistries.RULE_TEST, ResourceLocation.parse("changed_addon:iridium_ore_match"), CUSTOM_MATCH);
//        }
//
//        public boolean test(BlockState blockstate, @NotNull RandomSource random) {
//            return Objects.equals(Blocks.DEEPSLATE, blockstate.getBlock());
//        }
//
//        protected @NotNull RuleTestType<?> getType() {
//            return CUSTOM_MATCH;
//        }
//    }
}
