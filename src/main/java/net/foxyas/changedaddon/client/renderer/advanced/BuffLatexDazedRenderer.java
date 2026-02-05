package net.foxyas.changedaddon.client.renderer.advanced;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.BuffLatexDazedModel;
import net.foxyas.changedaddon.entity.advanced.AbstractDazedEntity;
import net.foxyas.changedaddon.entity.advanced.BuffDazedLatexEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BuffLatexDazedRenderer extends AdvancedHumanoidRenderer<BuffDazedLatexEntity, BuffLatexDazedModel> {

    public BuffLatexDazedRenderer(EntityRendererProvider.Context context) {
        super(context, new BuffLatexDazedModel(context.bakeLayer(BuffLatexDazedModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel(), model::isPartNotPuddle));
        this.addLayer(new ConditionalCustomEyesLayer<>(this,
                new CustomEyesLayer<>(this, context.getModelSet(),
                        CustomEyesLayer.fixedColor(Color3.DARK),
                        CustomEyesLayer::glowingIrisColorLeft,
                        CustomEyesLayer::glowingIrisColorRight,
                        CustomEyesLayer::noRender,
                        CustomEyesLayer::noRender)));
        this.addLayer(new ConditionalCustomLayers<>(this,
                TransfurCapeLayer.normalCape(this, context.getModelSet()),
                GasMaskLayer.forSnouted(this, context.getModelSet()),
                new LatexParticlesLayer<>(this, getModel(), model::isPartNotPuddle))
        );
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BuffDazedLatexEntity entity) {
        return ChangedAddonMod.textureLoc("textures/entities/buff_dazed_creature");
    }

    @Override
    protected void scale(@NotNull BuffDazedLatexEntity pLivingEntity, @NotNull PoseStack poseStack, float pPartialTickTime) {
        super.scale(pLivingEntity, poseStack, pPartialTickTime);
        poseStack.scale(1.08f, 1.08f, 1.08f);
    }

    @Override
    public boolean shouldRenderArmor(BuffDazedLatexEntity entity) {
        return !entity.isMorphed();
    }

    public static class ConditionalCustomEyesLayer<M extends AdvancedHumanoidModel<T>, T extends ChangedEntity> extends RenderLayer<T, M> {

        private final CustomEyesLayer<M, T> customEyesLayer;

        public ConditionalCustomEyesLayer(RenderLayerParent<T, M> parent, CustomEyesLayer<M, T> customEyesLayer) {
            super(parent);
            this.customEyesLayer = customEyesLayer;
        }

        @Override
        public void render(@NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity instanceof AbstractDazedEntity dazedLatexEntity
                    && !dazedLatexEntity.isMorphed()) {
                customEyesLayer.render(pose, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }
    }

    public static class ConditionalCustomLayers<M extends AdvancedHumanoidModel<T>, T extends ChangedEntity> extends RenderLayer<T, M> {

        private final TransfurCapeLayer<T, M> transfurCapeLayer;
        private final GasMaskLayer<T, M> gasMaskLayer;
        private final LatexParticlesLayer<T, M> latexParticlesLayer;

        public ConditionalCustomLayers(
                RenderLayerParent<T, M> parent,
                TransfurCapeLayer<T, M> transfurCapeLayer,
                GasMaskLayer<T, M> gasMaskLayer,
                LatexParticlesLayer<T, M> latexParticlesLayer) {

            super(parent);
            this.transfurCapeLayer = transfurCapeLayer;
            this.gasMaskLayer = gasMaskLayer;
            this.latexParticlesLayer = latexParticlesLayer;
        }

        @Override
        public void render(@NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity instanceof AbstractDazedEntity dazedLatexEntity
                    && !dazedLatexEntity.isMorphed()) {
                transfurCapeLayer.render(pose, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                gasMaskLayer.render(pose, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                latexParticlesLayer.render(pose, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }
    }
}