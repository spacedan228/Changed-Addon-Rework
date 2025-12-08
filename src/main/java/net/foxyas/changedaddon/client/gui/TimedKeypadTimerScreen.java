package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.menu.FoxyasInventoryMenu;
import net.foxyas.changedaddon.menu.TimedKeypadTimerMenu;
import net.foxyas.changedaddon.network.packet.simple.UpdateTimedKeypadTimerPacket;
import net.foxyas.changedaddon.util.ComponentUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber
public class TimedKeypadTimerScreen extends AbstractContainerScreen<TimedKeypadTimerMenu> {

    private final EditBox textBox;

    public TimedKeypadTimerScreen(TimedKeypadTimerMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);

        textBox = new EditBox(Minecraft.getInstance().font, 0, 0, 100, 20, ComponentUtil.literal(""));
    }

    public void updateTimer(String text) {
        int value;

        try {
            value = Integer.parseInt(text); // <-- esta é a forma correta
        } catch (NumberFormatException exception) {
            value = 0;
        }

        ChangedAddonMod.PACKET_HANDLER.sendToServer(
                new UpdateTimedKeypadTimerPacket(this.menu.blockEntity.getBlockPos(), value)
        );
    }


    @Override
    protected void init() {
        super.init();
        textBox.x = width/2 - textBox.getWidth()/2;
        textBox.y = (int)(height * .66f) - textBox.getHeight()/2;
        addRenderableWidget(textBox);

        setInitialFocus(textBox);
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float v, int i, int i1) { //is just here to shutup the compiler
        this.renderBackground(poseStack);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY) {
    }

    @Override
    public boolean isPauseScreen() {
        return super.isPauseScreen();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        switch (pKeyCode) {
            case GLFW.GLFW_KEY_ENTER , GLFW.GLFW_KEY_KP_ENTER -> updateTimer(textBox.getValue());
        }


        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (pCodePoint < '0' || pCodePoint > '9')
            return false;

        textBox.charTyped(pCodePoint, pModifiers);

        return true;
    }
}
