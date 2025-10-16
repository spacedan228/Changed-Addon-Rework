package net.foxyas.changedaddon.client.renderer.advanced;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.advanced.BorealisMaleModel;
import net.foxyas.changedaddon.client.renderer.layers.ProtogenDisplay;
import net.foxyas.changedaddon.entity.advanced.BorealisMaleEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BorealisMaleRenderer extends AdvancedHumanoidRenderer<BorealisMaleEntity, BorealisMaleModel, ArmorLatexMaleWolfModel<BorealisMaleEntity>> {
    public BorealisMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new BorealisMaleModel(context.bakeLayer(BorealisMaleModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new ProtogenDisplay<>(this, getModel(),
                ResourceLocation.parse("changed_addon:textures/entities/borealis_male/protogen_display_eyes.png"),
                ResourceLocation.parse("changed_addon:textures/entities/borealis_male/protogen_display.png")) {
            @Override
            public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, @NotNull BorealisMaleEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
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
    public @NotNull ResourceLocation getTextureLocation(@NotNull BorealisMaleEntity entity) {
        return ChangedAddonMod.textureLoc("textures/entities/borealis_male/borealis_male");
    }
}
