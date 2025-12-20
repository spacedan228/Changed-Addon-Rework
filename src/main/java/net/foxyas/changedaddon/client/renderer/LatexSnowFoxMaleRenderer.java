package net.foxyas.changedaddon.client.renderer;

import net.foxyas.changedaddon.client.model.LatexSnowFoxMaleModel;
import net.foxyas.changedaddon.entity.simple.LatexSnowFoxMaleEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LatexSnowFoxMaleRenderer extends AdvancedHumanoidRenderer<LatexSnowFoxMaleEntity, LatexSnowFoxMaleModel, ArmorLatexMaleWolfModel<LatexSnowFoxMaleEntity>> {
    public LatexSnowFoxMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnowFoxMaleModel(context.bakeLayer(LatexSnowFoxMaleModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LatexSnowFoxMaleEntity entity) {
        return ResourceLocation.parse("changed_addon:textures/entities/latex_snow_foxes/latex_snow_fox_male.png");
    }
}