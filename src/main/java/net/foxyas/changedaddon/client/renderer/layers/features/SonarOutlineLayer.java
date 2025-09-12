package net.foxyas.changedaddon.client.renderer.layers.features;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.client.renderer.renderTypes.ChangedAddonRenderTypes;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
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
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Outline layer with customizable behavior per-entity and fade-in/out logic.
 */
public class SonarOutlineLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private final RenderLayerParent<T, M> parent;

    public SonarOutlineLayer(RenderLayerParent<T, M> parent) {
        super(parent);
        this.parent = parent;
    }

    public interface CustomSonarRenderable {
        /**
         * Called when rendering with sonar outline.
         * Implementors can override rendering (pose, color, etc.).
         * Return true if you fully handled rendering yourself (skips default).
         */
        boolean handleSonarRender(@NotNull SonarOutlineLayer<?, ?> sonarOutlineLayer, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
                                  float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float alpha);

        /**
         * Same as handleSonarRender but specifically for the camera entity.
         * Return true if you fully handled rendering yourself (skips default).
         */
        boolean handleSonarRenderForCamera(@NotNull SonarOutlineLayer<?, ?> sonarOutlineLayer, @NotNull LivingEntity livingEntity, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
                                           float limbSwing, float limbSwingAmount, float partialTicks,
                                           float ageInTicks, float netHeadYaw, float headPitch, float alpha);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
                       @NotNull T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (SonarClientState.ticksToRenderEntities <= 0) return;

        Minecraft minecraft = Minecraft.getInstance();
        Entity camera = minecraft.cameraEntity;
        if (camera == null || camera.distanceToSqr(livingEntity) > SonarClientState.maxDistSqr) return;

        Player player = minecraft.player;
        if (player == null) return;
        TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
        if (instance == null) return;
        if (!instance.hasAbility(ChangedAddonAbilities.SONAR.get())) return;

        float alpha = SonarClientState.getAlpha(partialTicks);
        if (alpha <= 0.01f) return;

        // If the entity itself provides custom sonar rendering
        if (livingEntity instanceof CustomSonarRenderable custom) {
            if (custom.handleSonarRender(this, poseStack, buffer, packedLight,
                    limbSwing, limbSwingAmount, partialTicks,
                    ageInTicks, netHeadYaw, headPitch, alpha)) {
                return; // custom handled → skip default
            }
        }

        // If the transformed entity (variant) provides custom sonar rendering
        if (instance.getChangedEntity() instanceof CustomSonarRenderable custom) {
            if (custom.handleSonarRenderForCamera(this, livingEntity, poseStack, buffer, packedLight,
                    limbSwing, limbSwingAmount, partialTicks,
                    ageInTicks, netHeadYaw, headPitch, alpha)) {
                return; // custom handled → skip default
            }
        }

        // Default colors: white (Sonar) or yellowish (Echo)
        float r = 1.0f, g = 1.0f, b = 1.0f;
        if (SonarClientState.renderMode == RenderMode.ECHO_LOCATION) {
            r = 1.0f;
            g = 1.0f;
            b = 0.2f;
        }

        RenderType outline = ChangedAddonRenderTypes.outlineWithTranslucencyCull(getTextureLocation(livingEntity));
        getParentModel().renderToBuffer(
                poseStack,
                buffer.getBuffer(outline),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                r, g, b, alpha
        );
    }

    // estado global do cliente
    public static class SonarClientState {

        private static int ticksToRenderEntities = 0;
        private static int fadeInDuration = 10;   // duração do fade in
        private static int fadeOutDuration = 10;  // duração do fade out
        private static float maxDistSqr;
        private static RenderMode renderMode = RenderMode.SONAR;
        private static int lastTicks = 0;

        public static void setTicksToRenderEntities(int ticks, int iLastTicks, int fadeIn, int fadeOut, float maxDist, RenderMode mode) {
            ticksToRenderEntities = ticks;
            fadeInDuration = fadeIn;
            fadeOutDuration = fadeOut;
            maxDistSqr = maxDist * maxDist;
            renderMode = mode;
            lastTicks = iLastTicks;
        }

        public static void tick() {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player == null) return;
            TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
            if (instance == null) {
                ticksToRenderEntities = 0;
                return;
            } else if (!instance.hasAbility(ChangedAddonAbilities.SONAR.get())) {
                ticksToRenderEntities = 0;
                return;
            }


            if (ticksToRenderEntities > 0) ticksToRenderEntities--;
        }

        /**
         * Calcula o alpha (0f-1f) baseado no tempo restante.
         */
        public static float getAlpha(float partialTicks) {
            if (lastTicks <= 0) return 0f;
            int ticks = ticksToRenderEntities;

            // --- FADE IN ---
            if (ticks >= lastTicks - fadeInDuration) {
                float elapsed = (lastTicks - ticks) + partialTicks;
                return Mth.clamp(elapsed / fadeInDuration, 0f, 1f);
            }

            // --- FADE OUT ---
            if (ticks <= fadeOutDuration) {
                float remaining = (ticks - partialTicks);
                return Mth.clamp(remaining / fadeOutDuration, 0f, 1f);
            }

            // --- VISIBILIDADE TOTAL ---
            return 1f;
        }

        public static boolean isActive() {
            return ticksToRenderEntities > 0;
        }

        public static RenderMode getRenderMode() {
            return renderMode;
        }

        public static float getMaxDistSqr() {
            return maxDistSqr;
        }

        public static void resetData() {
            ticksToRenderEntities = 0;
            fadeInDuration = 0;
            fadeOutDuration = 0;
            maxDistSqr = 0;
            renderMode = RenderMode.SONAR;
            lastTicks = 0;
        }
    }

}
