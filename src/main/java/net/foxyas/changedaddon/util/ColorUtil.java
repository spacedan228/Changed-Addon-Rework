package net.foxyas.changedaddon.util;

import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
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
}
