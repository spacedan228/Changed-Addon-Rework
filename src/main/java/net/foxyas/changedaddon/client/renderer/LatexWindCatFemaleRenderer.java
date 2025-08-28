package net.foxyas.changedaddon.client.renderer;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.LatexWindCatFemaleModel;
import net.foxyas.changedaddon.client.model.LatexWindCatMaleModel;
import net.foxyas.changedaddon.entity.simple.LatexWindCatFemaleEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LatexWindCatFemaleRenderer extends AdvancedHumanoidRenderer<LatexWindCatFemaleEntity, LatexWindCatFemaleModel, ArmorLatexFemaleCatModel<LatexWindCatFemaleEntity>> {

    private static final ResourceLocation TEXTURE = ChangedAddonMod.textureLoc("textures/entities/latex_wind_cat_female/latex_wind_cat_female");

    public LatexWindCatFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWindCatFemaleModel(context.bakeLayer(LatexWindCatFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleCatModel.MODEL_SET, 0.5F);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LatexWindCatFemaleEntity pEntity) {
        return TEXTURE;
    }
}
