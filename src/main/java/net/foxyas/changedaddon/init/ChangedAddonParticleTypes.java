package net.foxyas.changedaddon.init;

import com.mojang.serialization.Codec;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.particle.SolventParticleParticle;
import net.foxyas.changedaddon.effect.particles.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Function;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedAddonParticleTypes {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ChangedAddonMod.MODID);
    public static final RegistryObject<ParticleType<SimpleParticleType>> SOLVENT_PARTICLE = REGISTRY.register("solvent_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<ParticleType<ThunderSparkOption>> THUNDER_SPARK = register("thunder_spark", ThunderSparkOption.DESERIALIZER, ThunderSparkOption::codec);
    public static final RegistryObject<ParticleType<LaserPointParticle.Option>> LASER_POINT = register("laser_point", LaserPointParticle.Option.DESERIALIZER, LaserPointParticle.Option::codec);

    public static ThunderSparkOption thunderSpark(int lifeSpam) {
        return new ThunderSparkOption(THUNDER_SPARK.get(), lifeSpam);
    }

    public static LaserPointParticle.Option laserPoint(Entity entity, Color3 color, float alpha) {
        return new LaserPointParticle.Option(entity, color.toInt(), alpha);
    }

    public static LaserPointParticle.Option laserPoint(Entity entity, Color color) {
        return new LaserPointParticle.Option(entity, color.getRGB(), color.getAlpha() / 255f);
    }

    private static <T extends ParticleOptions> RegistryObject<ParticleType<T>> register(String name, ParticleOptions.Deserializer<T> dec, final Function<ParticleType<T>, Codec<T>> fn) {
        return REGISTRY.register(name, () -> new ParticleType<>(false, dec) {
            public @NotNull Codec<T> codec() {
                return fn.apply(this);
            }
        });
    }

    @SubscribeEvent
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        var engine = Minecraft.getInstance().particleEngine;

        for (RegistryObject<ParticleType<?>> particleTypeRegistryObject : REGISTRY.getEntries()) {
            if (particleTypeRegistryObject.get() instanceof ParticleProviderHolder particleProviderHolder) {
                engine.register(particleTypeRegistryObject.get(), particleProviderHolder::getProvider);
            }
        }

        engine.register(THUNDER_SPARK.get(), ThunderSparkParticle.Provider::new);
        engine.register(LASER_POINT.get(), LaserPointParticle.Provider::new);
        engine.register(ChangedAddonParticleTypes.SOLVENT_PARTICLE.get(), SolventParticleParticle::provider);
    }

}

