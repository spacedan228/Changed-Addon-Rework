package net.foxyas.changedaddon.world.features;

import com.mojang.serialization.Codec;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

public record DynamicBiomeModifier() implements BiomeModifier {

    // ───────────────────────────────────────────────────────────
    // Codec sem campos → não exige nada no JSON
    // ───────────────────────────────────────────────────────────
    public static final Codec<DynamicBiomeModifier> CODEC = Codec.unit(new DynamicBiomeModifier());

    // ───────────────────────────────────────────────────────────
    // Aqui você coloca sua lógica global de biome modification
    // Ela rodará em *todos os biomas* durante Phase.ADD
    // ───────────────────────────────────────────────────────────
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        ChangedAddonMod.postEvent(new BiomeLoadingEvent(biome, phase, builder));
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }

    public static class BiomeLoadingEvent extends Event {
        private final ModifiableBiomeInfo.BiomeInfo.Builder builder;
        private final Holder<Biome> biomeHolder;
        private final BiomeModifier.Phase biomeModifierPhase;

        public BiomeLoadingEvent(Holder<Biome> biome, BiomeModifier.Phase biomeModifierPhase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            this.builder = builder;
            this.biomeModifierPhase = biomeModifierPhase;
            this.biomeHolder = biome;
        }

        public ModifiableBiomeInfo.BiomeInfo.Builder getBuilder() {
            return builder;
        }

        public Phase getBiomeModifierPhase() {
            return biomeModifierPhase;
        }

        public Holder<Biome> getBiomeHolder() {
            return biomeHolder;
        }

        public ResourceLocation getName() {
            return ForgeRegistries.BIOMES.getKey(getBiomeHolder().get());
        }
    }
}
