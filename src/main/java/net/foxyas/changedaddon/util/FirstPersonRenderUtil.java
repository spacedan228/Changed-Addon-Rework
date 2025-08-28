package net.foxyas.changedaddon.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class FirstPersonRenderUtil {

    /**
     * Renders a player's arm in first-person view with reversed swing if needed.
     *
     * @param stack      PoseStack used for rendering
     * @param buffer     MultiBufferSource for rendering
     * @param light      Packed light
     * @param equipProgress   Progress of item equip animation
     * @param swingProgress   Progress of swing animation
     * @param arm        Which arm to render (LEFT or RIGHT)
     */
    public static void renderPlayerArm(AbstractClientPlayer player, PoseStack stack, MultiBufferSource buffer, int light,
                                       float equipProgress, float swingProgress, HumanoidArm arm) {
        boolean rightHand = arm == HumanoidArm.RIGHT;
        float f = rightHand ? 1.0F : -1.0F;

        // Swing offsets
        float swingSqrt = Mth.sqrt(swingProgress);
        float offsetX = -0.3F * Mth.sin(swingSqrt * (float)Math.PI);
        float offsetY = 0.4F * Mth.sin(swingSqrt * ((float)Math.PI * 2F));
        float offsetZ = -0.4F * Mth.sin(swingProgress * (float)Math.PI);

        // Apply translations
        stack.translate(f * (offsetX + 0.64F), offsetY - 0.6F + equipProgress * -0.6F, offsetZ - 0.72F);

        // Apply swing rotations
        stack.mulPose(Vector3f.YP.rotationDegrees(f * 45.0F));
        float swingSin = Mth.sin(swingProgress * swingProgress * (float)Math.PI);
        float swingSqrtSin = Mth.sin(swingSqrt * (float)Math.PI);
        stack.mulPose(Vector3f.YP.rotationDegrees(f * swingSqrtSin * 70.0F));
        stack.mulPose(Vector3f.ZP.rotationDegrees(f * swingSin * -20.0F));

        // Additional arm orientation
        stack.translate(f * -1.0F, 3.6F, 3.5D);
        stack.mulPose(Vector3f.ZP.rotationDegrees(f * 120.0F));
        stack.mulPose(Vector3f.XP.rotationDegrees(200.0F));
        stack.mulPose(Vector3f.YP.rotationDegrees(f * -135.0F));
        stack.translate(f * 5.6F, 0.0D, 0.0D);

        // Render using player renderer
        var livingRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);

        if (livingRender instanceof PlayerRenderer renderer) {
            if (rightHand) {
                renderer.renderRightHand(stack, buffer, light, player);
            } else {
                renderer.renderLeftHand(stack, buffer, light, player);
            }
        } else if (livingRender instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer){

        }
    }

    public static void renderPlayerArmVanilla(PoseStack stack, MultiBufferSource buffer, int light,
                                              float equipProgress, float swingProgress, HumanoidArm arm) {
        boolean rightHand = arm == HumanoidArm.RIGHT;
        float f = rightHand ? 1.0F : -1.0F;

        // Swing offsets
        float swingSqrt = Mth.sqrt(swingProgress);
        float offsetX = -0.3F * Mth.sin(swingSqrt * (float)Math.PI);
        float offsetY = 0.4F * Mth.sin(swingSqrt * ((float)Math.PI * 2F));
        float offsetZ = -0.4F * Mth.sin(swingProgress * (float)Math.PI);

        // Translate arm
        stack.translate(f * (offsetX + 0.64F), offsetY - 0.6F + equipProgress * -0.6F, offsetZ - 0.72F);

        // Swing rotations
        stack.mulPose(Vector3f.YP.rotationDegrees(f * 45.0F));
        float swingSin = Mth.sin(swingProgress * swingProgress * (float)Math.PI);
        float swingSqrtSin = Mth.sin(swingSqrt * (float)Math.PI);
        stack.mulPose(Vector3f.YP.rotationDegrees(f * swingSqrtSin * 70.0F));
        stack.mulPose(Vector3f.ZP.rotationDegrees(f * swingSin * -20.0F));

        // Arm orientation relative to player
        stack.translate(f * -1.0F, 3.6F, 3.5D);
        stack.mulPose(Vector3f.ZP.rotationDegrees(f * 120.0F));
        stack.mulPose(Vector3f.XP.rotationDegrees(200.0F));
        stack.mulPose(Vector3f.YP.rotationDegrees(f * -135.0F));
        stack.translate(f * 5.6F, 0.0D, 0.0D);

        // Render the arm using the player renderer
        AbstractClientPlayer player = Minecraft.getInstance().player;
        var livingRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);

        if (livingRender instanceof PlayerRenderer renderer) {
            if (rightHand) {
                renderer.renderRightHand(stack, buffer, light, player);
            } else {
                renderer.renderLeftHand(stack, buffer, light, player);
            }
        } else if (livingRender instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer){

        }

    }
}
