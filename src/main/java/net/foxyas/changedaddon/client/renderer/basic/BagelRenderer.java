package net.foxyas.changedaddon.client.renderer.basic;

import net.foxyas.changedaddon.client.model.BagelModel;
import net.foxyas.changedaddon.entity.simple.BagelEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BagelRenderer extends AdvancedHumanoidRenderer<BagelEntity, BagelModel> {
    public BagelRenderer(EntityRendererProvider.Context context) {
        super(context, new BagelModel(context.bakeLayer(BagelModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new GasMaskLayer<>(this, context.getModelSet()));
        this.addLayer(new EmissiveBodyLayer<>(this, ResourceLocation.parse("changed_addon:textures/entities/bagel_transfur_glow.png")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BagelEntity entity) {
        return ResourceLocation.parse("changed_addon:textures/entities/bagel_transfur.png");
    }
}