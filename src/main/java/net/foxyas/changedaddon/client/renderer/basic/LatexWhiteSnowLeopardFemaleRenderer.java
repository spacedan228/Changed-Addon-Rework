package net.foxyas.changedaddon.client.renderer.basic;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.simple.LatexWhiteSnowLeopardFemaleModel;
import net.foxyas.changedaddon.entity.simple.LatexWhiteSnowLeopardFemale;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LatexWhiteSnowLeopardFemaleRenderer extends AdvancedHumanoidRenderer<LatexWhiteSnowLeopardFemale, LatexWhiteSnowLeopardFemaleModel, ArmorLatexFemaleCatModel<LatexWhiteSnowLeopardFemale>> {
    public LatexWhiteSnowLeopardFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWhiteSnowLeopardFemaleModel(context.bakeLayer(LatexWhiteSnowLeopardFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LatexWhiteSnowLeopardFemale latexWhiteSnowLeopardFemale) {
        return ChangedAddonMod.textureLoc("textures/latex_white_snow_leopard_female/latex_white_snow_leopard_female");
    }
}