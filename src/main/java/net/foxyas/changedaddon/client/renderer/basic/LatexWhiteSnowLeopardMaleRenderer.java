package net.foxyas.changedaddon.client.renderer.basic;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.simple.LatexWhiteSnowLeopardMaleModel;
import net.foxyas.changedaddon.entity.simple.LatexWhiteSnowLeopardMale;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWhiteSnowLeopardMaleRenderer extends AdvancedHumanoidRenderer<LatexWhiteSnowLeopardMale, LatexWhiteSnowLeopardMaleModel, ArmorLatexMaleCatModel<LatexWhiteSnowLeopardMale>> {
    public LatexWhiteSnowLeopardMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWhiteSnowLeopardMaleModel(context.bakeLayer(LatexWhiteSnowLeopardMaleModel.LAYER_LOCATION)), ArmorLatexMaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWhiteSnowLeopardMale p_114482_) {
        return ChangedAddonMod.textureLoc("textures/latex_white_snow_leopard_male/latex_white_snow_leopard_male");
    }
}