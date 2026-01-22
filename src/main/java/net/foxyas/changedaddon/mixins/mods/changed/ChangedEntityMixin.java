package net.foxyas.changedaddon.mixins.mods.changed;

import net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChangedEntity.class, remap = false)
public abstract class ChangedEntityMixin {
    @Shadow
    public abstract LatexType getLatexType();
    // holy guacamole
    @Inject(method = "targetSelectorTest", at = @At("HEAD"), cancellable = true)
    protected void targetSelectorTest(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        var entityVariant = ProcessTransfur.getEntityVariant(livingEntity);
        if (getLatexType() == LatexType.WHITE_LATEX
                && entityVariant.isPresent()
                && entityVariant.get() == ChangedAddonTransfurVariants.DARK_LATEX_YUFENG_QUEEN.get()) {
            cir.setReturnValue(false);
        }
    }
}
