package net.foxyas.changedaddon.client.renderer.advanced;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.advanced.BorealisFemaleModel;
import net.foxyas.changedaddon.client.renderer.layers.ProtogenDisplay;
import net.foxyas.changedaddon.entity.advanced.BorealisFemaleEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BorealisFemaleRenderer extends AdvancedHumanoidRenderer<BorealisFemaleEntity, BorealisFemaleModel, ArmorLatexFemaleWolfModel<BorealisFemaleEntity>> {
    public BorealisFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new BorealisFemaleModel(context.bakeLayer(BorealisFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new ProtogenDisplay<>(this, getModel(),
                ResourceLocation.parse("changed_addon:textures/entities/borealis_female/protogen_display_eyes.png"),
                ResourceLocation.parse("changed_addon:textures/entities/borealis_female/protogen_display.png")) {
            @Override
            public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, @NotNull BorealisFemaleEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (!entity.isInvisible()) {
                    BasicPlayerInfo info = entity.getBasicPlayerInfo();
                    Color3 displayColor = Color3.WHITE;  // Cor do display
                    Color3 eyeColor = Color3.WHITE;    // Cor dos olhos
                    int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

                    this.getParentModel().renderToBuffer(poseStack, bufferSource.getBuffer(getNormalDisplayRender()), packedLight, overlay, displayColor.red(), displayColor.green(), displayColor.blue(), 1.0F);
                    this.getParentModel().renderToBuffer(poseStack, bufferSource.getBuffer(getGlowEyeRender()), packedLight, overlay, eyeColor.red(), eyeColor.green(), eyeColor.blue(), 1.0F);
                }
            }
        });
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BorealisFemaleEntity entity) {
        return ChangedAddonMod.textureLoc("textures/entities/borealis_female/borealis_female");
    }
}
