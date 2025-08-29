package net.foxyas.changedaddon.mixins.client.renderer;

import net.foxyas.changedaddon.configuration.ChangedAddonClientConfiguration;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.NoSuchElementException;

@Mixin(value = AdvancedHumanoidModel.class)
public class AdvancedHumanoidModelMixin {

    @Inject(method = "shouldPartTransfur", at = @At("RETURN"), cancellable = true, remap = false)
    private void TurnOffPlantoids(ModelPart part, CallbackInfoReturnable<Boolean> cir) {
        var self = (AdvancedHumanoidModel<?>) (Object) this;
        var torso = self.getTorso();
        try {
            ModelPart plantoidsPart = torso.getChild("Plantoids");
            if (part == plantoidsPart) {
                cir.setReturnValue(!ChangedAddonClientConfiguration.PLANTOIDS_VISIBILITY.get());
            }
            //plantoidsPart.visible = !ChangedAddonClientConfiguration.PLANTOIDS_VARIABLE.get();
        } catch (NoSuchElementException ignored) {
        }
    }
}
