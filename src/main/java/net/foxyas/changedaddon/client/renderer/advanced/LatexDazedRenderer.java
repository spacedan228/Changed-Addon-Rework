package net.foxyas.changedaddon.client.renderer.advanced;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.LatexDazedModel;
import net.foxyas.changedaddon.client.model.armors.ArmorLatexDazedModel;
import net.foxyas.changedaddon.entity.advanced.DazedLatexEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class LatexDazedRenderer extends AdvancedHumanoidRenderer<DazedLatexEntity, LatexDazedModel, ArmorLatexDazedModel<DazedLatexEntity>> {

    public LatexDazedRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexDazedModel(context.bakeLayer(LatexDazedModel.LAYER_LOCATION)),
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
    public void render(@NotNull DazedLatexEntity entity, float yRot, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, yRot, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DazedLatexEntity entity) {
        if (entity.isMorphed()) {
            return ChangedAddonMod.textureLoc("textures/entities/dazed_creature_puddle");
        }

        return ChangedAddonMod.textureLoc("textures/entities/dazed_creature");
    }


    @Override
    public boolean shouldRenderArmor(DazedLatexEntity entity) {
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
            if (entity instanceof DazedLatexEntity dazedLatexEntity
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
            if (entity instanceof DazedLatexEntity dazedLatexEntity
                    && !dazedLatexEntity.isMorphed()) {
                transfurCapeLayer.render(pose, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                gasMaskLayer.render(pose, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                latexParticlesLayer.render(pose, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }
    }
}