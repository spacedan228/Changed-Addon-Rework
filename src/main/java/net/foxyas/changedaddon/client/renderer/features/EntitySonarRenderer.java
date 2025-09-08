package net.foxyas.changedaddon.client.renderer.features;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class EntitySonarRenderer {

    public static class ClientState {
        private static int ticksToRenderEntities = 60;

        public static int getTicksToRenderEntities() {
            return ticksToRenderEntities;
        }

        public static void setTicksToRenderEntities(int ticksToRenderEntities) {
            ClientState.ticksToRenderEntities = ticksToRenderEntities;
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (EntitySonarRenderer.ClientState.ticksToRenderEntities > 0) {
                EntitySonarRenderer.ClientState.ticksToRenderEntities--;
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
        if (ClientState.ticksToRenderEntities > 0) {
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource buffer = event.getMultiBufferSource();

            LivingEntity livingEntity = event.getEntity();
            LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> renderer = event.getRenderer();
            EntityModel<LivingEntity> model = renderer.getModel();

            // pega o renderType de outline com a textura normal da entidade
            RenderType outline = RenderType.outline(renderer.getTextureLocation(livingEntity));

            // animação/pose já é tratada aqui
            float limbSwing = livingEntity.animationPosition;
            float limbSwingAmount = livingEntity.animationSpeed;
            float ageInTicks = livingEntity.tickCount + event.getPartialTick();
            float netHeadYaw = Mth.lerp(event.getPartialTick(), livingEntity.yHeadRotO, livingEntity.getYHeadRot())
                    - Mth.lerp(event.getPartialTick(), livingEntity.yBodyRotO, livingEntity.yBodyRot);
            float headPitch = Mth.lerp(event.getPartialTick(), livingEntity.xRotO, livingEntity.getXRot());

            model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, event.getPartialTick());
            model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            model.renderToBuffer(
                    poseStack,
                    buffer.getBuffer(outline),
                    event.getPackedLight(),
                    OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F
            );
        }
    }
}
