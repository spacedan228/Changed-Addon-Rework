package net.foxyas.changedaddon.mixins.client.renderer;

import net.foxyas.changedaddon.configuration.ChangedAddonClientConfiguration;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.NoSuchElementException;

@Mixin(value = LatexHumanoidArmorModel.class)
public class LatexHumanoidArmorModelMixin {

    @Inject(method = "prepareMobModel(Lnet/ltxprogrammer/changed/entity/ChangedEntity;FFF)V", at = @At("RETURN"), remap = false)
    private void TurnOffPlantoids(@NotNull ChangedEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, CallbackInfo ci) {
        var self = (AdvancedHumanoidModel<?>) (Object) this;
        var torso = self.getTorso();
        try {
            ModelPart plantoidsPart = torso.getChild("Plantoids");
            plantoidsPart.visible = !ChangedAddonClientConfiguration.PLANTOIDS_VISIBILITY.get();
        } catch (NoSuchElementException ignored) {
        }
    }
}
