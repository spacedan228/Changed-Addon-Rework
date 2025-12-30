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

public record RibbonParticleOptions(Entity target, int color, int segments, float length, float sizeY,
                                    float rotationRad) implements ParticleOptions {

    public static final ParticleOptions.Deserializer<RibbonParticleOptions> DESERIALIZER = new Deserializer() {
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
            return new RibbonParticleOptions(entity, color, segments, length, sizeY, reader.readFloat() * Mth.DEG_TO_RAD);//Input in DEG! for easier usage
        }

        @Override
        public ParticleOptions fromNetwork(@NotNull ParticleType pParticleType, @NotNull FriendlyByteBuf buf) {
            return new RibbonParticleOptions(UniversalDist.getLevel().getEntity(buf.readVarInt()), buf.readInt(), buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
    };

    public static final Codec<RibbonParticleOptions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("target").xmap(i -> UniversalDist.getLevel().getEntity(i), Entity::getId).forGetter(RibbonParticleOptions::target),
                    Codec.INT.fieldOf("color").forGetter(RibbonParticleOptions::color),
                    Codec.INT.fieldOf("segments").forGetter(RibbonParticleOptions::segments),
                    Codec.FLOAT.fieldOf("length").forGetter(RibbonParticleOptions::length),
                    Codec.FLOAT.fieldOf("sizeY").forGetter(RibbonParticleOptions::sizeY),
                    Codec.FLOAT.fieldOf("rotationRad").forGetter(RibbonParticleOptions::rotationRad)
            ).apply(instance, RibbonParticleOptions::new));

    public static Codec<RibbonParticleOptions> codec(ParticleType<RibbonParticleOptions> type) {
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
    }

    @Override
    public @NotNull String writeToString() {
        return ChangedAddonParticleTypes.RIBBON.getId().toString();
    }
}