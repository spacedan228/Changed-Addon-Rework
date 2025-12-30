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

import java.util.Arrays;

public record MultiColorRibbonParticleOptions(
        Entity target,
        int[] colors,
        int segments,
        float length,
        float sizeY,
        float rotationRad
) implements ParticleOptions {

    /* ---------- Codec ---------- */

    public static final Codec<MultiColorRibbonParticleOptions> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("target")
                            .xmap(i -> UniversalDist.getLevel().getEntity(i), Entity::getId)
                            .forGetter(MultiColorRibbonParticleOptions::target),

                    Codec.INT.listOf()
                            .xmap(
                                    list -> list.stream().mapToInt(Integer::intValue).toArray(),
                                    arr -> Arrays.stream(arr).boxed().toList()
                            )
                            .fieldOf("colors")
                            .forGetter(MultiColorRibbonParticleOptions::colors),

                    Codec.INT.fieldOf("segments").forGetter(MultiColorRibbonParticleOptions::segments),
                    Codec.FLOAT.fieldOf("length").forGetter(MultiColorRibbonParticleOptions::length),
                    Codec.FLOAT.fieldOf("sizeY").forGetter(MultiColorRibbonParticleOptions::sizeY),
                    Codec.FLOAT.fieldOf("rotationRad").forGetter(MultiColorRibbonParticleOptions::rotationRad)
            ).apply(instance, MultiColorRibbonParticleOptions::new));

    public static Codec<MultiColorRibbonParticleOptions> codec(ParticleType<MultiColorRibbonParticleOptions> type) {
        return CODEC;
    }

    /* ---------- Deserializer ---------- */

    public static final ParticleOptions.Deserializer<MultiColorRibbonParticleOptions> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {

                @Override
                public MultiColorRibbonParticleOptions fromCommand(
                        @NotNull ParticleType type,
                        @NotNull StringReader reader
                ) throws CommandSyntaxException {

                    reader.expect(' ');
                    Entity target = UniversalDist.getLevel().getEntity(reader.readInt());

                    reader.expect(' ');
                    int color = reader.readInt(); // comando usa 1 cor

                    reader.expect(' ');
                    int segments = reader.readInt();

                    reader.expect(' ');
                    float length = reader.readFloat();

                    reader.expect(' ');
                    float sizeY = reader.readFloat();

                    reader.expect(' ');
                    float rot = reader.readFloat() * Mth.DEG_TO_RAD;

                    return new MultiColorRibbonParticleOptions(
                            target,
                            new int[]{color},
                            segments,
                            length,
                            sizeY,
                            rot
                    );
                }

                @Override
                public MultiColorRibbonParticleOptions fromNetwork(
                        @NotNull ParticleType type,
                        @NotNull FriendlyByteBuf buf
                ) {
                    Entity target = UniversalDist.getLevel().getEntity(buf.readVarInt());

                    int count = buf.readVarInt();
                    int[] colors = new int[count];
                    for (int i = 0; i < count; i++) {
                        colors[i] = buf.readInt();
                    }

                    return new MultiColorRibbonParticleOptions(
                            target,
                            colors,
                            buf.readVarInt(),
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat()
                    );
                }
            };

    @Override
    public ParticleType<?> getType() {
        return ChangedAddonParticleTypes.MULTICOLOR_RIBBON.get();
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buf) {
        buf.writeVarInt(target.getId());

        buf.writeVarInt(colors.length);
        for (int c : colors) {
            buf.writeInt(c);
        }

        buf.writeVarInt(segments);
        buf.writeFloat(length);
        buf.writeFloat(sizeY);
        buf.writeFloat(rotationRad);
    }

    @Override
    public @NotNull String writeToString() {
        return ChangedAddonParticleTypes.MULTICOLOR_RIBBON.getId().toString();
    }
}