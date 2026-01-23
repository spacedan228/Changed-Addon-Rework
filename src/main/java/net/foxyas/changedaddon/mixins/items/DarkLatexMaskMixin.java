package net.foxyas.changedaddon.mixins.items;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.item.DarkLatexMask;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(value = DarkLatexMask.class, remap = false)
public class DarkLatexMaskMixin {

    @Inject(method = "fillItemList", at = @At(value = "INVOKE", target = "Lnet/ltxprogrammer/changed/entity/variant/TransfurVariant;getPublicTransfurVariants()Ljava/util/stream/Stream;", shift = At.Shift.AFTER))
    private void fillItemListHook(Predicate<TransfurVariant<?>> predicate, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CallbackInfo ci) {
        DarkLatexMask self = (DarkLatexMask) (Object) this;
        TransfurVariant.getPublicTransfurVariants()
                .filter((transfurVariant -> transfurVariant.getFormId().getNamespace().equals(ChangedAddonMod.MODID) && transfurVariant.is(ChangedTags.TransfurVariants.MASKED)))
                .forEach((variant) -> output.accept(Syringe.setOwner(Syringe.setPureVariant(new ItemStack(self), variant.getFormId()), UniversalDist.getLocalPlayer())));
    }
}
