package net.foxyas.changedaddon.client.gui.ftkc;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.qte.FightToKeepConsciousness;
import net.foxyas.changedaddon.util.RenderUtil;
import net.foxyas.changedaddon.util.Vector2f;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Random;

public abstract class CircleMinigameScreen extends Screen {

    public static final ResourceLocation CIRCLE_SLOT = ChangedAddonMod.textureLoc(
            "textures/screens/qtes/fight_to_keep_consciousness/struggle_circle_slot");
    public static final ResourceLocation CIRCLE_CURSOR = ChangedAddonMod.textureLoc(
            "textures/screens/qtes/fight_to_keep_consciousness/struggle_circle_cursor");
    protected static final float INTERACTION_RADIUS = 15f;
    protected static final float INTERACTION_RADIUS_SQR = INTERACTION_RADIUS * INTERACTION_RADIUS;

    protected final Minecraft minecraft;
    protected final Player player;
    protected final Vector2f circle = new Vector2f();
    protected final Vector2f mouseLast = new Vector2f();
    protected final Vector2f cursor = new Vector2f();
    protected float struggleProgressO = 0;
    protected float struggleProgress = 0;
    protected float halfWidth;
    protected float halfHeight;

    protected CircleMinigameScreen(Component title) {
        super(title);
        minecraft = Minecraft.getInstance();
        player = minecraft.player;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        halfWidth = width / 2f;
        halfHeight = height / 2f;
        GLFW.glfwSetInputMode(minecraft.getWindow().getWindow(), GLFW.GLFW_CURSOR, InputConstants.CURSOR_DISABLED);
    }

    protected void drawProgressBar(PoseStack stack, float x, float y, float partialTick) {
        final int barWidth = 100;
        final int barHeight = 10;

        float filledHalfWidth = (int) (Mth.lerp(partialTick, struggleProgressO, struggleProgress) * barWidth / 2);
        RenderUtil.fill(stack, x - filledHalfWidth, y - barHeight / 2f, x + filledHalfWidth, y + barHeight / 2f, Color.WHITE.getRGB());
    }

    protected void drawCircles(PoseStack stack) {
        RenderSystem.setShaderTexture(0, CIRCLE_SLOT);
        blit(stack, (int) circle.x - 9, (int) circle.y - 9, 0, 0, 19, 19, 19, 19);

        RenderSystem.setShaderTexture(0, CIRCLE_CURSOR);
        blit(stack, (int) cursor.x - 9, (int) cursor.y - 9, 0, 0, 19, 19, 19, 19);
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack, pPartialTick);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    public void renderBackground(@NotNull PoseStack stack, float partialTick) {
        TransfurVariantInstance<?> tf = ProcessTransfur.getPlayerTransfurVariant(player);
        if (tf == null) {
            RenderUtil.fill(stack.last().pose(), 0, 0, width, height, -8355712);
            return;
        }

        float fightProgress = ChangedAddonVariables.nonNullOf(player).consciousnessFightProgress / FightToKeepConsciousness.STRUGGLE_NEED;
        float loseProgress = Mth.lerp(partialTick, Math.max(0, tf.ageAsVariant - 1), tf.ageAsVariant) / FightToKeepConsciousness.STRUGGLE_TIME;

        int alpha = (int) (128 + 128 * (loseProgress - fightProgress));

        RenderUtil.fill(stack.last().pose(), 0, 0, width, height, alpha << 24 | tf.getTransfurColor().toInt());
    }

    @Override
    public void tick() {
        if (ChangedAddonVariables.ofOrDefault(player).FTKCminigameType == null) {
            minecraft.setScreen(null);
            return;
        }

        struggleProgressO = struggleProgress;
        if (Vector2f.distSqr(cursor, circle) <= INTERACTION_RADIUS_SQR) {
            increaseStruggle();
            return;
        }

        if (struggleProgress < 0) {
            struggleProgress = 0;
            return;
        }

        if (struggleProgress > 0) {
            struggleProgress = Math.max(0, struggleProgress - .1f);
        }
    }

    protected abstract void increaseStruggle();

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        cursor.add((float) mouseX - mouseLast.x, (float) mouseY - mouseLast.y);
        cursor.clamp(5, width - 5, 5, height - 5);
        mouseLast.set((float) mouseX, (float) mouseY);
    }

    protected void randomizeCursorPos(float offsetX, float offsetY) {
        Random rand = player.getRandom();
        float x = Mth.clamp(halfWidth + rand.nextFloat(-offsetX, offsetX), 5, width - 5);
        float y = Mth.clamp(halfHeight + rand.nextFloat(-offsetY, offsetY), 5, height - 5);
        mouseLast.set(x, y);
        cursor.set(x, y);

        Window window = minecraft.getWindow();
        float scale = (float) window.getGuiScale();
        GLFW.glfwSetCursorPos(window.getWindow(), x * scale, y * scale);
    }

    @Override
    public void removed() {
        GLFW.glfwSetInputMode(minecraft.getWindow().getWindow(), GLFW.GLFW_CURSOR, InputConstants.CURSOR_NORMAL);
    }
}
