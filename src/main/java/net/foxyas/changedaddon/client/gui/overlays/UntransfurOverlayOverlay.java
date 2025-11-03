package net.foxyas.changedaddon.client.gui.overlays;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class UntransfurOverlayOverlay {

    //Todo: Make this a InGameOverlay check ChangedAddonOverlays to examples
    public static void renderUntransfurProgressOverlay(ForgeIngameGui forgeIngameGui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        ChangedAddonVariables.PlayerVariables playerVariables = null;
        playerVariables = ChangedAddonVariables.of(player);
        if (playerVariables == null) return;

        boolean canShow = playerVariables.untransfurProgress > 0;
        double progress = playerVariables.untransfurProgress;
        double doubleProgress = progress / 8.33;
        int intProgress = (int) doubleProgress;


        if (canShow) {
            RenderSystem.setShaderTexture(0, ResourceLocation.parse("changed_addon:textures/screens/untransfurprogress.png"));
            GuiComponent.blit(poseStack, 10, screenHeight - 73, 0, 0, 14, 5, 14, 5);

            RenderSystem.setShaderTexture(0, ResourceLocation.parse("changed_addon:textures/screens/untransfurprogress_full.png"));
            GuiComponent.blit(poseStack, 11, screenHeight - 72, 0, 0, intProgress, 3, intProgress, 3);
        }

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
}
