package net.foxyas.changedaddon.client.renderer.advanced;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.LatexWindCatMaleModel;
import net.foxyas.changedaddon.client.model.advanced.LuminaraFlowerBeastModel;
import net.foxyas.changedaddon.client.renderer.layers.LuminaraBeastWingsConditionalLayer;
import net.foxyas.changedaddon.entity.advanced.LuminaraFlowerBeastEntity;
import net.foxyas.changedaddon.entity.simple.LatexWindCatMaleEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LuminaraFlowerBeastRenderer extends AdvancedHumanoidRenderer<LuminaraFlowerBeastEntity, LuminaraFlowerBeastModel, ArmorLatexMaleDragonModel<LuminaraFlowerBeastEntity>> {

    public static final ResourceLocation TEXTURE = ChangedAddonMod.textureLoc("textures/entities/luminara_beast/luminara_flower_beast_base");
    public static final ResourceLocation WING_TEXTURE = ChangedAddonMod.textureLoc("textures/entities/luminara_beast/luminara_flower_beast_wings_layer");
    public static final ResourceLocation WING_GLOW_TEXTURE = ChangedAddonMod.textureLoc("textures/entities/luminara_beast/luminara_flower_beast_wings_layer_glow");


    public LuminaraFlowerBeastRenderer(EntityRendererProvider.Context context) {
        super(context, new LuminaraFlowerBeastModel(context.bakeLayer(LuminaraFlowerBeastModel.LAYER_LOCATION)), ArmorLatexMaleDragonModel.MODEL_SET, 0.5F);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(new LuminaraBeastWingsConditionalLayer<>(this));
        //this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LuminaraFlowerBeastEntity pEntity) {
        return TEXTURE;
    }
}
