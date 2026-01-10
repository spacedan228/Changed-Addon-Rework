package net.foxyas.changedaddon.client.renderer.basic;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.BlueLizardModel;
import net.foxyas.changedaddon.entity.simple.BlueLizard;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlueLizardRenderer extends AdvancedHumanoidRenderer<BlueLizard, BlueLizardModel> {
    public BlueLizardRenderer(EntityRendererProvider.Context context) {
        super(context, new BlueLizardModel(context.bakeLayer(BlueLizardModel.LAYER_LOCATION)), ArmorLatexMaleDragonModel.MODEL_SET, 0.5F);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull BlueLizard p_114482_) {
        return ResourceLocation.fromNamespaceAndPath(ChangedAddonMod.MODID, "textures/entities/blue_lizard.png");
    }
}
