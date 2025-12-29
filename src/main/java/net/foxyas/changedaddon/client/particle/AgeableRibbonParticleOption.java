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

public record AgeableRibbonParticleOption(Entity target, int color, int segments, float length, float sizeY,
                                          float rotationRad, int lifeTime) implements ParticleOptions {

    public static final Deserializer<AgeableRibbonParticleOption> DESERIALIZER = new Deserializer() {
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
            return new AgeableRibbonParticleOption(entity, color, segments, length, sizeY, reader.readFloat() * Mth.DEG_TO_RAD, lifeTime);//Input in DEG! for easier usage
        }

        @Override
        public ParticleOptions fromNetwork(@NotNull ParticleType pParticleType, @NotNull FriendlyByteBuf buf) {
            return new AgeableRibbonParticleOption(UniversalDist.getLevel().getEntity(buf.readVarInt()), buf.readInt(), buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt());
        }
    };

    public static final Codec<AgeableRibbonParticleOption> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("target").xmap(i -> UniversalDist.getLevel().getEntity(i), Entity::getId).forGetter(AgeableRibbonParticleOption::target),
                    Codec.INT.fieldOf("color").forGetter(AgeableRibbonParticleOption::color),
                    Codec.INT.fieldOf("segments").forGetter(AgeableRibbonParticleOption::segments),
                    Codec.FLOAT.fieldOf("length").forGetter(AgeableRibbonParticleOption::length),
                    Codec.FLOAT.fieldOf("sizeY").forGetter(AgeableRibbonParticleOption::sizeY),
                    Codec.FLOAT.fieldOf("rotationRad").forGetter(AgeableRibbonParticleOption::rotationRad),
                    Codec.INT.fieldOf("lifeTime").forGetter(AgeableRibbonParticleOption::lifeTime)
            ).apply(instance, AgeableRibbonParticleOption::new));

    public static Codec<AgeableRibbonParticleOption> codec(ParticleType<AgeableRibbonParticleOption> type) {
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