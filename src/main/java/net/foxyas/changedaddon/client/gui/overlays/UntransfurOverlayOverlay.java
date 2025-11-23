package net.foxyas.changedaddon.client.gui.overlays;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;

@OnlyIn(Dist.CLIENT)
public class UntransfurOverlayOverlay {

    public static final ResourceLocation FULL_BAR = ResourceLocation.parse("changed_addon:textures/screens/untransfurprogress_full.png");
    public static final ResourceLocation NORMAL_BAR = ResourceLocation.parse("changed_addon:textures/screens/untransfurprogress.png");

    public static void renderUntransfurProgressOverlay(ForgeGui forgeGui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
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
            guiGraphics.blit(NORMAL_BAR, 10, screenHeight - 73, 0, 0, 14, 5, 14, 5);
            guiGraphics.blit(FULL_BAR, 11, screenHeight - 72, 0, 0, intProgress, 3, intProgress, 3);
        }

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
}
