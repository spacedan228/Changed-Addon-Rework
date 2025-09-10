package net.foxyas.changedaddon.mixins.client.renderer;

import net.foxyas.changedaddon.client.renderer.layers.features.SonarOutlineLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow public abstract boolean addLayer(RenderLayer<T, M> pLayer);

    @Inject(method = "<init>" , at = @At("TAIL"), cancellable = false)
    private void addExtraLayers(EntityRendererProvider.Context pContext, M pModel, float pShadowRadius, CallbackInfo ci){
        LivingEntityRenderer<T, M> self = (LivingEntityRenderer<T,M>) (Object) this;
        this.addLayer(new SonarOutlineLayer<>(self));
    }
}
