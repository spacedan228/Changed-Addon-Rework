package net.foxyas.changedaddon.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.DarkLatexYufengQueenModel;
import net.foxyas.changedaddon.client.model.armors.ArmorLatexFemaleDragonAltTailModel;
import net.foxyas.changedaddon.entity.simple.DarkLatexYufengQueenEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DarkLatexYufengQueenRenderer extends AdvancedHumanoidRenderer<DarkLatexYufengQueenEntity, DarkLatexYufengQueenModel, ArmorLatexFemaleDragonAltTailModel<DarkLatexYufengQueenEntity>> {

    private static final ResourceLocation TEXTURE = ChangedAddonMod.textureLoc("textures/entities/dark_latex_yufeng_queen");

    public DarkLatexYufengQueenRenderer(EntityRendererProvider.Context context) {
        super(context, new DarkLatexYufengQueenModel(context.bakeLayer(DarkLatexYufengQueenModel.LAYER_LOCATION)), ArmorLatexFemaleDragonAltTailModel.MODEL_SET, 0.5F);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x242424))
                .withIris(CustomEyesLayer.fixedIfNotDarkLatexOverrideLeft(Color3.WHITE),
                        CustomEyesLayer.fixedIfNotDarkLatexOverrideRight(Color3.WHITE))
                .build());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DarkLatexYufengQueenEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void scale(@NotNull DarkLatexYufengQueenEntity entity, @NotNull PoseStack pose, float partialTick) {
        float scaleFactor = 1.3F;
        pose.scale(scaleFactor, scaleFactor, scaleFactor);
    }
}
