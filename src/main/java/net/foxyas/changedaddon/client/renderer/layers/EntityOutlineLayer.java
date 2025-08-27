package net.foxyas.changedaddon.client.renderer.layers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxyas.changedaddon.client.renderer.renderTypes.ChangedAddonRenderTypes;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class EntityOutlineLayer<M extends AdvancedHumanoidModel<T>, T extends ChangedEntity> extends RenderLayer<T, M> {

    public EntityOutlineLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        poseStack.pushPose();
        poseStack.scale(1.01f, 1.01f, 1.01f);
        poseStack.translate(0, -0.005f, 0); // Small tweak in the outline value

        float r = 1f, g = 1f, b = 1f, a = 1f;
        VertexConsumer outlineBuffer = bufferSource.getBuffer(ChangedAddonRenderTypes.outlineWithDepth(this.getTextureLocation(entity)));
        this.getParentModel().renderToBuffer(
                poseStack,
                outlineBuffer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                r, g, b, a
        );

        poseStack.popPose();
    }
}
