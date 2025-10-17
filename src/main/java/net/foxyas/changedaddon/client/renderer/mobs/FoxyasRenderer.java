package net.foxyas.changedaddon.client.renderer.mobs;

import net.foxyas.changedaddon.client.model.LatexSnowFoxFoxyasModel;
import net.foxyas.changedaddon.entity.advanced.LatexSnowFoxFoxyasEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FoxyasRenderer extends AdvancedHumanoidRenderer<LatexSnowFoxFoxyasEntity, LatexSnowFoxFoxyasModel, ArmorLatexMaleWolfModel<LatexSnowFoxFoxyasEntity>> {
    public FoxyasRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnowFoxFoxyasModel(context.bakeLayer(LatexSnowFoxFoxyasModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LatexSnowFoxFoxyasEntity entity) {
        return ResourceLocation.parse("changed_addon:textures/entities/foxyas/foxyas_main.png");
    }
}
