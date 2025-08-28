package net.foxyas.changedaddon.client.renderer;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.LatexWindCatMaleModel;
import net.foxyas.changedaddon.entity.simple.LatexWindCatMaleEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LatexWindCatMaleRenderer extends AdvancedHumanoidRenderer<LatexWindCatMaleEntity, LatexWindCatMaleModel, ArmorLatexMaleCatModel<LatexWindCatMaleEntity>> {

    private static final ResourceLocation TEXTURE = ChangedAddonMod.textureLoc("textures/entities/latex_wind_cat_male");

    public LatexWindCatMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWindCatMaleModel(context.bakeLayer(LatexWindCatMaleModel.LAYER_LOCATION)), ArmorLatexMaleCatModel.MODEL_SET, 0.5F);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LatexWindCatMaleEntity pEntity) {
        return TEXTURE;
    }
}
