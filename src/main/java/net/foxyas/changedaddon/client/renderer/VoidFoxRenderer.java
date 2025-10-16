package net.foxyas.changedaddon.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.client.model.VoidFoxModel;
import net.foxyas.changedaddon.client.renderer.layers.EntityOutlineLayer;
import net.foxyas.changedaddon.client.renderer.layers.ModelFlickerLayer;
import net.foxyas.changedaddon.client.renderer.layers.ParticlesTrailsLayer;
import net.foxyas.changedaddon.entity.bosses.VoidFoxEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoidFoxRenderer extends AdvancedHumanoidRenderer<VoidFoxEntity, VoidFoxModel, ArmorLatexMaleWolfModel<VoidFoxEntity>> {
    public VoidFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new VoidFoxModel(context.bakeLayer(VoidFoxModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer::scleraColor, CustomEyesLayer.fixedColorGlowing(Color3.WHITE), CustomEyesLayer.fixedColorGlowing(Color3.WHITE), CustomEyesLayer::noRender, CustomEyesLayer::noRender));
        this.addLayer(new ParticlesTrailsLayer<>(this, 0.025f, ParticleTypes.ASH));
        this.addLayer(new ParticlesTrailsLayer<>(this, 0.0025f, ParticleTypes.END_ROD));
        this.addLayer(new ModelFlickerLayer<>(this));
        this.addLayer(new EntityOutlineLayer<>(this));
    }

    @Override
    public void render(@NotNull VoidFoxEntity entity, float yRot, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, yRot, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void renderDebugText(List<Component> lines, PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, int packedLight) {
        Font font = Minecraft.getInstance().font;
        float scale = 0.025F;
        int lineHeight = 10;
        int totalHeight = lines.size() * lineHeight;
        double yOffset = entity.getBbHeight() + 0.5D + lines.size() * 0.1f;

        poseStack.pushPose();
        poseStack.translate(0.0D, yOffset, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation()); // vira para o jogador
        poseStack.scale(-scale, -scale, scale); // escala pequena

        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            float y = -totalHeight / 2.0F + i * lineHeight;
            font.drawInBatch(line,
                    -font.width("HP: " + entity.getHealth()) / 2.0F,
                    y,                          // Y da linha
                    0xFFFFFF,                   // Cor base (pode ser qualquer, será sobrescrita por .withStyle)
                    false,                      // Sem sombra
                    poseStack.last().pose(),    // Matrix do PoseStack
                    bufferSource,               // Buffer vindo do Method render
                    false,                      // seeThrough (normalmente false)
                    0,                          // background color (0 = transparente)
                    packedLight                 // luz do ambiente
            );
        }

        poseStack.popPose();
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull VoidFoxEntity entity) {
        return ResourceLocation.parse("changed_addon:textures/entities/void_fox_dark.png");
    }
}
