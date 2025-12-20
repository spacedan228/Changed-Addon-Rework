package net.foxyas.changedaddon.client.renderer;

import net.foxyas.changedaddon.client.model.LatexSnowFoxFemaleModel;
import net.foxyas.changedaddon.entity.simple.LatexSnowFoxFemaleEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LatexSnowFoxFemaleRenderer extends AdvancedHumanoidRenderer<LatexSnowFoxFemaleEntity, LatexSnowFoxFemaleModel, ArmorLatexFemaleWolfModel<LatexSnowFoxFemaleEntity>> {
    public LatexSnowFoxFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnowFoxFemaleModel(context.bakeLayer(LatexSnowFoxFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LatexSnowFoxFemaleEntity entity) {
        return ResourceLocation.parse("changed_addon:textures/entities/latex_snow_foxes/latex_snow_fox_female.png");
    }
}