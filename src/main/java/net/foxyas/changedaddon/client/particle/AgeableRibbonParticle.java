package net.foxyas.changedaddon.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AgeableRibbonParticle extends RibbonParticle {

    protected AgeableRibbonParticle(ClientLevel pLevel, Entity target, int color, int segments, float length, float scaleY, float rotationRad, int lifeTime) {
        super(pLevel, target, color, segments, length, scaleY, rotationRad);
        this.lifetime = lifeTime;
    }

    @Override
    public void tick() {
        age++;
        if (age >= lifetime) this.alpha -= 0.01f;
        super.tick();
        if (this.alpha <= 0) this.remove();
    }

    public static class Provider implements ParticleProvider<AgeableRibbonParticleOptions> {

        @Override
        public @Nullable Particle createParticle(@NotNull AgeableRibbonParticleOptions ageableRibbonParticleOptions, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            AgeableRibbonParticle ageableRibbonParticle = new AgeableRibbonParticle(pLevel, ageableRibbonParticleOptions.target(), ageableRibbonParticleOptions.color(), ageableRibbonParticleOptions.segments(), ageableRibbonParticleOptions.length(), ageableRibbonParticleOptions.sizeY(), ageableRibbonParticleOptions.rotationRad(), ageableRibbonParticleOptions.lifeTime());
            ageableRibbonParticle.offset = new Vec3(pXSpeed, pYSpeed, pZSpeed);
            return ageableRibbonParticle;
        }
    }
}
