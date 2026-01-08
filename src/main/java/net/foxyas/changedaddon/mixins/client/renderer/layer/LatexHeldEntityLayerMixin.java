package net.foxyas.changedaddon.mixins.client.renderer.layer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.ltxprogrammer.changed.client.LivingEntityRendererExtender;
import net.ltxprogrammer.changed.client.renderer.layers.LatexHeldEntityLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LatexHeldEntityLayer.class, remap = false)
public abstract class LatexHeldEntityLayerMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends RenderLayer<T, M> {
    public LatexHeldEntityLayerMixin(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/ltxprogrammer/changed/entity/ChangedEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/ltxprogrammer/changed/client/LivingEntityRendererExtender;directRender(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void scaleDownEntity(LivingEntityRendererExtender<T, M> instance, LivingEntity entity, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, int packedLight, Operation<Void> original) {
        pose.pushPose();
        if (entity instanceof IAlphaAbleEntity alpha && alpha.isAlpha()) pose.scale(0.5f, 0.5f, 0.5f);
        original.call(instance, entity, yaw, partialTicks, pose, bufferSource, packedLight);
        pose.popPose();
    }
}
