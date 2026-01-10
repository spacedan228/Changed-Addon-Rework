package net.foxyas.changedaddon.client.renderer.basic;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.simple.LatexCheetahFemaleModel;
import net.foxyas.changedaddon.entity.simple.LatexCheetahFemale;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LatexCheetahFemaleRenderer extends AdvancedHumanoidRenderer<LatexCheetahFemale, LatexCheetahFemaleModel> {
    public LatexCheetahFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexCheetahFemaleModel(context.bakeLayer(LatexCheetahFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LatexCheetahFemale LatexCheetahFemale) {
        return ChangedAddonMod.textureLoc("textures/entities/latex_cheetah_female/latex_cheetah_female");
    }
}