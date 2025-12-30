package net.foxyas.changedaddon.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.foxyas.changedaddon.init.ChangedAddonParticleTypes;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record AgeableRibbonParticleOptions(Entity target, int color, int segments, float length, float sizeY,
                                           float rotationRad, int lifeTime) implements ParticleOptions {

    public static final Deserializer<AgeableRibbonParticleOptions> DESERIALIZER = new Deserializer() {
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
            return new AgeableRibbonParticleOptions(entity, color, segments, length, sizeY, reader.readFloat() * Mth.DEG_TO_RAD, lifeTime);//Input in DEG! for easier usage
        }

        @Override
        public ParticleOptions fromNetwork(@NotNull ParticleType pParticleType, @NotNull FriendlyByteBuf buf) {
            return new AgeableRibbonParticleOptions(UniversalDist.getLevel().getEntity(buf.readVarInt()), buf.readInt(), buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt());
        }
    };

    public static final Codec<AgeableRibbonParticleOptions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("target").xmap(i -> UniversalDist.getLevel().getEntity(i), Entity::getId).forGetter(AgeableRibbonParticleOptions::target),
                    Codec.INT.fieldOf("color").forGetter(AgeableRibbonParticleOptions::color),
                    Codec.INT.fieldOf("segments").forGetter(AgeableRibbonParticleOptions::segments),
                    Codec.FLOAT.fieldOf("length").forGetter(AgeableRibbonParticleOptions::length),
                    Codec.FLOAT.fieldOf("sizeY").forGetter(AgeableRibbonParticleOptions::sizeY),
                    Codec.FLOAT.fieldOf("rotationRad").forGetter(AgeableRibbonParticleOptions::rotationRad),
                    Codec.INT.fieldOf("lifeTime").forGetter(AgeableRibbonParticleOptions::lifeTime)
            ).apply(instance, AgeableRibbonParticleOptions::new));

    public static Codec<AgeableRibbonParticleOptions> codec(ParticleType<AgeableRibbonParticleOptions> type) {
        return CODEC;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ChangedAddonParticleTypes.AGEABLE_RIBBON.get();
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
        return ChangedAddonParticleTypes.AGEABLE_RIBBON.getId().toString();
    }
}