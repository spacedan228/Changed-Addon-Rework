package net.foxyas.changedaddon.effect.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SolventParticleParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    protected SolventParticleParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.3f, 0.3f);

        this.lifetime = Math.max(1, 10 + (this.random.nextInt(20) - 10));
        this.gravity = 0.1f;
        this.hasPhysics = true;
        this.xd = vx * 0.05;
        this.yd = vy * 0.05;
        this.zd = vz * 0.05;
        this.pickSprite(spriteSet);
    }

    public static SolventParticleParticleProvider provider(SpriteSet spriteSet) {
        return new SolventParticleParticleProvider(spriteSet);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static class SolventParticleParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public SolventParticleParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SolventParticleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
