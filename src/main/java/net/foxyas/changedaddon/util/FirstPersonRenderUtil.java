package net.foxyas.changedaddon.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.foxyas.changedaddon.mixins.client.renderer.ItemInHandRendererAccessor;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;

@OnlyIn(Dist.CLIENT)
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
        }

    }


    private static boolean lock;
    public static void renderOffHandWithMainHandStackIfTransfured(Player pPlayer, InteractionHand hand, PoseStack stack, MultiBufferSource buffer, int light, float partialTicks) {
        if (lock) return;
        if (!(pPlayer instanceof AbstractClientPlayer player)) return;

        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            EntityRenderer<? super LivingEntity> entRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            if (entRenderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) {
                if (livingEntityRenderer instanceof PlayerRenderer playerRenderer) {
                    lock = true;
                    if (hand == InteractionHand.MAIN_HAND) {
                        stack.pushPose();
                        boolean rightHand = player.getMainArm() == HumanoidArm.RIGHT;

                        float playerSwimAmount = player.getSwimAmount(partialTicks);
                        ItemInHandRenderer itemInHandRenderer = Minecraft.getInstance().getItemInHandRenderer();
                        float equipProgress = 1.0F - Mth.lerp(partialTicks, ((ItemInHandRendererAccessor) itemInHandRenderer).getoMainHandHeight(), ((ItemInHandRendererAccessor) itemInHandRenderer).getMainHandHeight());

                        float f = rightHand ? -1.0F : 1.0F;
                        float f1 = Mth.sqrt(playerSwimAmount);
                        float f2 = -0.3F * Mth.sin(f1 * (float) Math.PI);
                        float f3 = 0.4F * Mth.sin(f1 * ((float) Math.PI * 2F));
                        float f4 = -0.4F * Mth.sin(playerSwimAmount * (float) Math.PI);

                        stack.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equipProgress * -0.6F, f4 + -0.71999997F);// 0 here is an inaccessible variable from ItemInHandRenderer
                        stack.mulPose(Vector3f.YP.rotationDegrees(f * 45.0F));
                        float f5 = Mth.sin(playerSwimAmount * playerSwimAmount * (float) Math.PI);
                        float f6 = Mth.sin(f1 * (float) Math.PI);
                        stack.mulPose(Vector3f.YP.rotationDegrees(f * f6 * 70.0F));
                        stack.mulPose(Vector3f.ZP.rotationDegrees(f * f5 * -20.0F));
                        stack.translate(f * -1.0F, 3.6F, 3.5D);
                        stack.mulPose(Vector3f.ZP.rotationDegrees(f * 120.0F));
                        stack.mulPose(Vector3f.XP.rotationDegrees(200.0F));
                        stack.mulPose(Vector3f.YP.rotationDegrees(f * -135.0F));
                        stack.translate(f * 5.6F, 0.0D, 0.0D);
                        //applyBobbing(stack, partialTicks);
                        if (rightHand) {
                            playerRenderer.renderLeftHand(stack, buffer, light, player);
                        } else {
                            //playerRenderer.renderLeftHand(stack, buffer, light, player);
                            playerRenderer.renderRightHand(stack, buffer, light, player);
                        }
                        stack.popPose();
                    }
                    lock = false;
                }
            }
            return true;
        });
    }
}
