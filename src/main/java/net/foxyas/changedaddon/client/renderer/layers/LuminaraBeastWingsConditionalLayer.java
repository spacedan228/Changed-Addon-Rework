package net.foxyas.changedaddon.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.client.renderer.advanced.LuminaraFlowerBeastRenderer;
import net.foxyas.changedaddon.entity.advanced.LuminaraFlowerBeastEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class LuminaraBeastWingsConditionalLayer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends RenderLayer<T, M> {

    public LuminaraBeastWingsConditionalLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof LuminaraFlowerBeastEntity luminaraFlowerBeastEntity) {
            if (luminaraFlowerBeastEntity.isAwakened()) {
                this.getParentModel().prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
                this.getParentModel().renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(LuminaraFlowerBeastRenderer.WING_TEXTURE)), packedLight, overlay, 1, 1, 1, 1.0F);

                if (entity.isFlying() || entity.isFallFlying()) {
                    boolean hasElytra = luminaraFlowerBeastEntity.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
                    if (!hasElytra) {
                        this.getParentModel().renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.eyes(LuminaraFlowerBeastRenderer.WING_GLOW_TEXTURE)), packedLight, overlay, 1, 1, 1, 1.0F);
                    }
                }
            }
        }

    }
}
