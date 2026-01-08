package net.foxyas.changedaddon.mixins.client.renderer.layer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.foxyas.changedaddon.process.DEBUG;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LatexHeldEntityLayer.class, remap = false)
public abstract class LatexHeldEntityLayerMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends RenderLayer<T, M> {
    public LatexHeldEntityLayerMixin(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

//    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/ltxprogrammer/changed/entity/ChangedEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/ltxprogrammer/changed/client/LivingEntityRendererExtender;directRender(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
//    private void scaleDownEntity(LivingEntityRendererExtender<T, M> instance, LivingEntity entity, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, int packedLight, Operation<Void> original) {
//        if (entity instanceof IAlphaAbleEntity alpha && alpha.isAlpha()) {
//            pose.scale(DEBUG.HeadPosX, DEBUG.HeadPosY, DEBUG.HeadPosZ);
//        }
//        original.call(instance, entity, yaw, partialTicks, pose, bufferSource, packedLight);
//    }

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/ltxprogrammer/changed/entity/ChangedEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    shift = At.Shift.BY
            )
    )
    private void scaleAfterPush(
            PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci
    ) {
        if (entity instanceof IAlphaAbleEntity alpha && alpha.isAlpha()) {
            pose.scale(0.4286f, 0.4286f, 0.4286f); // to return a value from +75% we need to use this value
        }
    }
}
