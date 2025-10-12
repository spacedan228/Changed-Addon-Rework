package net.foxyas.changedaddon.util;

import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ColorUtil {

    public static Color3 lerpTFColor(@NotNull Color3 start, @NotNull Color3 end, @Nullable Player player){
        if(player == null) return start;

        TransfurVariantInstance<?> transfurVariantInstance = ProcessTransfur.getPlayerTransfurVariant(player);
        if(transfurVariantInstance == null) return start;

        return start.lerp(transfurVariantInstance.getTransfurProgression(1), end);
    }

    public static Color3 lerpTFColor(@NotNull Color3 start, @NotNull Color3 end, float partialTicks){
        return start.lerp(partialTicks, end);
    }

    public static float getPlayerTransfurProgressSafe(@Nullable Player player, float partialTick){
        if(player == null) return 0;

        TransfurVariantInstance<?> transfurVariantInstance = ProcessTransfur.getPlayerTransfurVariant(player);
        if(transfurVariantInstance == null) return 0;

        return transfurVariantInstance.getTransfurProgression(partialTick);
    }


    public static Color3 lerpTFColors(LivingEntity livingEntity, float partialTicks, Color3... colors) {
        if (colors == null || colors.length == 0)
            return new Color3(1.0f, 1.0f, 1.0f); // fallback branco

        if (colors.length == 1)
            return colors[0]; // só uma cor, nada pra interpolar

        int amountOfColors = colors.length;
        float progress = 0.0f;

        if (livingEntity instanceof Player player) {
            progress = getPlayerTransfurProgressSafe(player, partialTicks);
        }

        // Garante que o valor fique entre 0 e 1
        progress = Mth.clamp(progress, 0.0f, 1.0f);

        // Divide o progresso igualmente entre as cores
        float segment = 1.0f / (amountOfColors - 1);

        // Identifica entre quais cores o progresso atual está
        int index = (int) Math.floor(progress / segment);
        if (index < 0) index = 0;
        if (index >= amountOfColors - 1) index = amountOfColors - 2;

        float localProgress = (progress - (index * segment)) / segment;

        // Faz o lerp entre as duas cores do segmento atual
        Color3 start = colors[index];
        Color3 end = colors[index + 1];
        return new Color3(
                Mth.lerp(localProgress, start.red(), end.red()),
                Mth.lerp(localProgress, start.green(), end.green()),
                Mth.lerp(localProgress, start.blue(), end.blue())
        );
    }
}
