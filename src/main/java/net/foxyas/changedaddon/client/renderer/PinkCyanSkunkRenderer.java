package net.foxyas.changedaddon.client.renderer;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.PinkCyanSkunkModel;
import net.foxyas.changedaddon.entity.simple.PinkCyanSkunkEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PinkCyanSkunkRenderer extends AdvancedHumanoidRenderer<PinkCyanSkunkEntity, PinkCyanSkunkModel> {

    private static final ResourceLocation TEXTURE = ChangedAddonMod.textureLoc("textures/entities/pink_cyan_skunk/pink_cyan_skunk");

    public PinkCyanSkunkRenderer(EntityRendererProvider.Context context) {
        super(context, new PinkCyanSkunkModel(context.bakeLayer(PinkCyanSkunkModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5F);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PinkCyanSkunkEntity pEntity) {
        return TEXTURE;
    }
}
