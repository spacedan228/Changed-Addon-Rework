package net.foxyas.changedaddon.client.gui.ftkc;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.network.packet.ServerboundProgressFTKCPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class MousePullMinigameScreen extends CircleMinigameScreen {

    public MousePullMinigameScreen() {
        super(Component.literal(""));
    }

    @Override
    protected void init() {
        super.init();
        circle.set(halfWidth, halfHeight);
        if (cursor.x == 0 && cursor.y == 0) randomizeCursorPos(width / 3f, height / 3f);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        drawProgressBar(guiGraphics, halfWidth, halfHeight + 25, partialTick);

        guiGraphics.drawCenteredString(font, Component.translatable("gui.changed_addon.fight_to_keep_consciousness_minigame.label_text", KeyPressMinigameScreen.getTimeRemaining(player)), (int) halfWidth, (int) (halfHeight - 40), -1);
        guiGraphics.drawCenteredString(font, KeyPressMinigameScreen.getProgressText(player), (int) halfWidth, (int) (halfHeight - 20), -1);

        drawCircles(guiGraphics);
    }

    protected void increaseStruggle() {
        struggleProgressO = struggleProgress;
        struggleProgress += 0.25f;
        if (struggleProgress < 1) return;

        struggleProgressO = 0;
        struggleProgress = 0;
        randomizeCursorPos(width / 3f, height / 3f);

        ChangedAddonMod.PACKET_HANDLER.sendToServer(new ServerboundProgressFTKCPacket());
    }
}
