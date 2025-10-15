package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class UntransfurOverlayOverlay {
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void eventHandler(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            int w = event.getWindow().getGuiScaledWidth();
            int h = event.getWindow().getGuiScaledHeight();
            int posX = w;
            int posY = h;
            Level world = null;
            double x = 0;
            double y = 0;
            double z = 0;
            Player entity = Minecraft.getInstance().player;
            if (entity != null) {
                world = entity.level;
                x = entity.getX();
                y = entity.getY();
                z = entity.getZ();
            }
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            double progress = 0;
            int intprogress = 0;
            double aprogress = 0;
            boolean canshow = false;
            canshow = (entity.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ChangedAddonVariables.PlayerVariables())).untransfurProgress > 0;
            progress = (entity.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ChangedAddonVariables.PlayerVariables())).untransfurProgress;
            aprogress = progress / 8.33;
            intprogress = (int) aprogress;


            if (canshow) {
                RenderSystem.setShaderTexture(0, new ResourceLocation("changed_addon:textures/screens/untransfurprogress.png"));
                GuiComponent.blit(event.getMatrixStack(), 10, posY - 73, 0, 0, 14, 5, 14, 5);

                RenderSystem.setShaderTexture(0, new ResourceLocation("changed_addon:textures/screens/untransfurprogress_full.png"));
                GuiComponent.blit(event.getMatrixStack(), 11, posY - 72, 0, 0, intprogress, 3, intprogress, 3);


                //Minecraft.getInstance().font.draw(event.getMatrixStack(), intprogress + "%", 9, posY + -105, -1);


            }

            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }
}
