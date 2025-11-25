package net.foxyas.changedaddon.mixins.world;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(LevelStorageSource.class)
public abstract class LevelStorageSourceMixin {
    @Inject(
            method = "readLightweightData",
            at = @At("RETURN")
    )
    private static void onReadLightweightData(Path pFile, CallbackInfoReturnable<Tag> cir) {
        if (cir.getReturnValue() == null) return;

        Tag tag = cir.getReturnValue();
        if (!(tag instanceof CompoundTag rootTag)) return;
        if (UniversalDist.getLevel() == null) return;

        //CompoundTag dataTag = rootTag.getCompound("Data");
        //CompoundTag rules = dataTag.getCompound("GameRules");

        /*
        // --- Exemplo de fix manual ---
        if (rules.contains("changed_addon:oldRule")) {
            String value = rules.getString("changed_addon:oldRule");
            rules.putString("changed_addon:newRule", value);
            rules.remove("changed_addon:oldRule");
            ChangedAddonMod.LOGGER.info("Remapped GameRule oldRule → newRule");
        }*/

        // --- Opcional: usar teu sistema centralizado de DataFix ---
        if (ChangedAddonMod.dataFixer != null) {
            ChangedAddonMod.dataFixer.updateCompoundTag(DataFixTypes.LEVEL, rootTag);
        }
    }
}
