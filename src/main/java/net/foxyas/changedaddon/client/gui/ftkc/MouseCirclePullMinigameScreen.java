package net.foxyas.changedaddon.client.gui.ftkc;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.network.packet.ServerboundProgressFTKCPacket;
import net.foxyas.changedaddon.util.RenderUtil;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MouseCirclePullMinigameScreen extends CircleMinigameScreen {

    public MouseCirclePullMinigameScreen() {
        super(TextComponent.EMPTY);
    }

    @Override
    protected void init() {
        super.init();
        if (circle.x == 0 && circle.y == 0) randomizeCirclePos(width / 3f, height / 3f);
        if (cursor.x == 0 && cursor.y == 0) randomizeCursorPos(width / 3f, height / 3f);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        drawProgressBar(poseStack, halfWidth, halfHeight + 25, partialTick);

        RenderUtil.drawCentered(font, poseStack, new TranslatableComponent("gui.changed_addon.fight_to_keep_consciousness_minigame.label_text", KeyPressMinigameScreen.getTimeRemaining(player)), halfWidth, halfHeight - 40, -1);
        RenderUtil.drawCentered(font, poseStack, KeyPressMinigameScreen.getProgressText(player), halfWidth, halfHeight - 20, -1);

        drawCircles(poseStack);
    }

    protected void increaseStruggle() {
        struggleProgressO = struggleProgress;
        struggleProgress += 0.25f;
        if (struggleProgress < 1) return;

        struggleProgressO = 0;
        struggleProgress = 0;
        randomizeCursorPos(width / 3f, height / 3f);
        randomizeCirclePos(width / 3f, height / 3f);

        ChangedAddonMod.PACKET_HANDLER.sendToServer(new ServerboundProgressFTKCPacket());
    }

    protected void randomizeCirclePos(float offsetX, float offsetY) {
        Random rand = player.getRandom();
        float x = Mth.clamp(halfWidth + rand.nextFloat(-offsetX, offsetX), 5, width - 5);
        float y = Mth.clamp(halfHeight + rand.nextFloat(-offsetY, offsetY), 5, height - 5);
        circle.set(x, y);
    }
}
