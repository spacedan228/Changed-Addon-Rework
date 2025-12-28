package net.foxyas.changedaddon.client.particle;

import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.foxyas.changedaddon.init.ChangedAddonParticleTypes;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
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

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        super.render(pBuffer, pRenderInfo, pPartialTicks);
    }

    public record Options(Entity target, int color, int segments, float length, float sizeY,
                          float rotationRad, int lifeTime) implements ParticleOptions {

        public static final Deserializer<Options> DESERIALIZER = new Deserializer() {
            @Override
            public ParticleOptions fromCommand(@NotNull ParticleType pParticleType, @NotNull StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                Entity entity = UniversalDist.getLevel().getEntity(reader.readInt());
                reader.expect(' ');
                int color = reader.readInt();
                reader.expect(' ');
                int segments = reader.readInt();
                reader.expect(' ');
                float length = reader.readFloat();
                reader.expect(' ');
                float sizeY = reader.readFloat();
                reader.expect(' ');
                int lifeTime = reader.readInt();
                reader.expect(' ');
                return new Options(entity, color, segments, length, sizeY, reader.readFloat() * Mth.DEG_TO_RAD, lifeTime);//Input in DEG! for easier usage
            }

            @Override
            public ParticleOptions fromNetwork(@NotNull ParticleType pParticleType, @NotNull FriendlyByteBuf buf) {
                return new Options(UniversalDist.getLevel().getEntity(buf.readVarInt()), buf.readInt(), buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt());
            }
        };

        public static final Codec<Options> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("target").xmap(i -> UniversalDist.getLevel().getEntity(i), Entity::getId).forGetter(Options::target),
                        Codec.INT.fieldOf("color").forGetter(Options::color),
                        Codec.INT.fieldOf("segments").forGetter(Options::segments),
                        Codec.FLOAT.fieldOf("length").forGetter(Options::length),
                        Codec.FLOAT.fieldOf("sizeY").forGetter(Options::sizeY),
                        Codec.FLOAT.fieldOf("rotationRad").forGetter(Options::rotationRad),
                        Codec.INT.fieldOf("lifeTime").forGetter(Options::lifeTime)
                ).apply(instance, Options::new));

        public static Codec<Options> codec(ParticleType<Options> type) {
            return CODEC;
        }

        @Override
        public @NotNull ParticleType<?> getType() {
            return ChangedAddonParticleTypes.RIBBON.get();
        }

        @Override
        public void writeToNetwork(@NotNull FriendlyByteBuf buf) {
            buf.writeVarInt(target.getId());
            buf.writeInt(color);
            buf.writeVarInt(segments);
            buf.writeFloat(length);
            buf.writeFloat(sizeY);
            buf.writeFloat(rotationRad);
            buf.writeInt(lifeTime);
        }

        @Override
        public @NotNull String writeToString() {
            return ChangedAddonParticleTypes.RIBBON.getId().toString();
        }
    }

    public static class Provider implements ParticleProvider<Options> {

        @Override
        public @Nullable Particle createParticle(@NotNull Options options, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new AgeableRibbonParticle(pLevel, options.target, options.color, options.segments, options.length, options.sizeY, options.rotationRad, options.lifeTime);
        }
    }
}
