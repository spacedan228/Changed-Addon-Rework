package net.foxyas.changedaddon.effect.particles;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;

public interface ParticleProviderHolder {
    
    <T extends ParticleOptions> ParticleProvider<T> getProvider(SpriteSet spriteSet);
}
