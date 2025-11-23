package net.foxyas.changedaddon.mixins.world;

import com.mojang.datafixers.DataFixer;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataFixTypes.class)
public abstract class DataFixTypesMixin {

    @Inject(method = "update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
    private void dataFixForChanged(DataFixer pFixer, CompoundTag pTag, int pVersion, int pNewVersion, CallbackInfoReturnable<CompoundTag> cir) {
        if (ChangedAddonMod.dataFixer != null)
            ChangedAddonMod.dataFixer.updateCompoundTag((DataFixTypes) (Object) this, pTag);
    }
}