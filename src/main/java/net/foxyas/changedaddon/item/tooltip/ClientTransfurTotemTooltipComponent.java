package net.foxyas.changedaddon.item.tooltip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.foxyas.changedaddon.client.renderer.blockEntitys.InformantBlockEntityRenderer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class ClientTransfurTotemTooltipComponent implements ClientTooltipComponent {
    private final TransfurVariant<?> variant;
    private final ItemStack transfurTotemStack;
    private final ChangedEntity entity;

    public ClientTransfurTotemTooltipComponent(TransfurTotemTooltipComponent component) {
        this.variant = component.getVariant();
        this.entity = InformantBlockEntityRenderer.getDisplayEntity(variant);
        this.transfurTotemStack = component.getTransfurTotemStack();
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return 80;
    }

    public TransfurVariant<?> getVariant() {
        return variant;
    }

    public void prepareEntityForRender() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            BasicPlayerInfo basicPlayerInfo = Changed.config.client.basicPlayerInfo;
            BasicPlayerInfo basicEntityInfo = this.entity.getBasicPlayerInfo();
            basicEntityInfo.setEyeStyle(basicPlayerInfo.getEyeStyle());
            basicEntityInfo.setLeftIrisColor(basicPlayerInfo.getLeftIrisColor());
            basicEntityInfo.setRightIrisColor(basicPlayerInfo.getRightIrisColor());
            basicEntityInfo.setSize(1f);

            this.entity.setCustomName(entity.getType().getDescription());
            this.entity.setCustomNameVisible(true);

            CompoundTag transfurTotemStackDataTag = this.transfurTotemStack.getOrCreateTag();
            if (transfurTotemStackDataTag.contains("TransfurVariantData")) {
                CompoundTag transfurVariantData = transfurTotemStackDataTag.getCompound("TransfurVariantData");
                this.entity.readAdditionalSaveData(transfurVariantData);
                if (transfurTotemStackDataTag.contains("entityData")) {
                    CompoundTag entityData = transfurVariantData.getCompound("entityData");
                    this.entity.readPlayerVariantData(entityData);
                }
            }
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int posX, int posY, @NotNull GuiGraphics guiGraphics) {
        if (entity == null)
            return;
        this.prepareEntityForRender();

        float bbMax = Math.max(entity.getBbWidth(), entity.getBbHeight());

        // Ajuste de scale baseado no tamanho do BB
        int baseScale = 30;
        int scale = (int) (baseScale / (bbMax * 0.5));

        // Ajuste de posição para centralizar
        float offsetX = posX + 45; // centro horizontal do tooltip
        float offsetY = posY + 75; // centro vertical do tooltip

        // Rotação animada
        float spin = (Minecraft.getInstance().player != null
                ? Minecraft.getInstance().player.tickCount % 360
                : 0);

        renderEntityInInventory(offsetX, offsetY, scale, spin, 0, guiGraphics.pose(), entity);
    }

    @Override
    public void renderText(@NotNull Font font, int posX, int posY,
                           @NotNull Matrix4f matrix,
                           @NotNull MultiBufferSource.BufferSource bufferSource) {
    }

    public static void renderEntityInInventory(float posX, float posY, int scale,
                                               float mouseX, float mouseY, @NotNull PoseStack poseStack, @NotNull LivingEntity livingEntity) {
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(posX, posY, 3000.0D); // bem na frente do tooltip
        modelViewStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();

        poseStack.translate(0.0D, 0.0D, 2500.0D); // força ainda mais na frente
        poseStack.scale((float) scale, (float) scale, (float) scale);

        Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf quaternion1 = Axis.XP.rotationDegrees(mouseY);
        Quaternionf quaternion2 = Axis.YP.rotationDegrees(mouseX);
        quaternion.mul(quaternion1);
        quaternion.mul(quaternion2);
        poseStack.mulPose(quaternion);

        float f2 = livingEntity.yBodyRot;
        float f3 = livingEntity.getYRot();
        float f4 = livingEntity.getXRot();
        float f5 = livingEntity.yHeadRotO;
        float f6 = livingEntity.yHeadRot;

        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion.conjugate();
        quaternion.mul(Axis.ZP.rotationDegrees(180.0F));

        dispatcher.overrideCameraOrientation(quaternion);
        dispatcher.setRenderShadow(false);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        //noinspection deprecation
        RenderSystem.runAsFancy(() -> dispatcher.render(
                livingEntity, 0.0D, 0.0D, 0.0D,
                0.0F, 1.0F, poseStack, buffer, 15728880));
        buffer.endBatch();

        dispatcher.setRenderShadow(true);
        livingEntity.yBodyRot = f2;
        livingEntity.setYRot(f3);
        livingEntity.setXRot(f4);
        livingEntity.yHeadRotO = f5;
        livingEntity.yHeadRot = f6;

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }
}
