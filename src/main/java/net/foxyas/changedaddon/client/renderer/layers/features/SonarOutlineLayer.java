package net.foxyas.changedaddon.client.renderer.layers.features;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Outline layer with customizable behavior per-entity and fade-in/out logic.
 */
public class SonarOutlineLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public SonarOutlineLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    public interface CustomSonarRenderable {
        /**
         * Called when rendering with sonar outline.
         * Implementors can modify poseStack, choose color, etc.
         * Return true if you handled rendering yourself (skip default).
         */
        boolean handleSonarRender(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                  float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float alpha);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       @NotNull T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (ClientState.ticksToRenderEntities <= 0) return;

        Entity camera = Minecraft.getInstance().cameraEntity;
        if(camera == null || camera.distanceToSqr(livingEntity) > ClientState.maxDistSqr) return;

        float alpha = ClientState.getAlpha(partialTicks);
        if (alpha <= 0.01f) return;

        // se a entity tiver um comportamento customizado
        if (livingEntity instanceof CustomSonarRenderable custom) {
            if (custom.handleSonarRender(poseStack, buffer, packedLight, limbSwing, limbSwingAmount,
                    partialTicks, ageInTicks, netHeadYaw, headPitch, alpha)) {
                return; // custom handle → skip default
            }
        }

        // cor padrão: branco (Sonar) ou amarelo (Echo)
        float r = 1.0f, g = 1.0f, b = 1.0f;
        if (ClientState.renderMode == RenderMode.ECHO_LOCATION) {
            r = 1.0f; g = 1.0f; b = 0.2f;
        }

        RenderType outline = RenderType.outline(getTextureLocation(livingEntity));
        getParentModel().renderToBuffer(
                poseStack,
                buffer.getBuffer(outline),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                r, g, b, alpha
        );
    }

    // estado global do cliente
    public static class ClientState {

        private static int ticksToRenderEntities = 0;
        private static int fadeDuration = 10; // ticks de fade in/out
        private static float maxDistSqr;
        private static RenderMode renderMode = RenderMode.SONAR;

        private static int lastTicks = 0;

        public static void setTicksToRenderEntities(int ticks, int fadeInTicks, float maxDist, RenderMode mode) {
            ticksToRenderEntities = ticks;
            fadeDuration = fadeInTicks;
            maxDistSqr = maxDist * maxDist;
            renderMode = mode;
            lastTicks = ticks;
        }

        public static void tick() {
            if (ticksToRenderEntities > 0) {
                ticksToRenderEntities--;
            }
        }

        public static float getAlpha(float partialTicks) {
            if (lastTicks <= 0) return 0f;
            int ticks = ticksToRenderEntities;

            if (ticks > lastTicks - fadeDuration) {
                // fade in
                float progress = (lastTicks - ticks + partialTicks) / (float) fadeDuration;
                return Mth.clamp(progress, 0f, 1f);
            } else if (ticks < fadeDuration) {
                // fade out
                float progress = (ticks - partialTicks) / (float) fadeDuration;
                return Mth.clamp(progress, 0f, 1f);
            }

            return 1f; // estado normal (full visible)
        }
    }
}
