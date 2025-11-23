package net.foxyas.changedaddon.client.gui.ftkc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.network.packet.ServerboundProgressFTKCPacket;
import net.foxyas.changedaddon.qte.FightToKeepConsciousness;
import net.foxyas.changedaddon.util.RenderUtil;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import static net.foxyas.changedaddon.qte.FightToKeepConsciousness.STRUGGLE_NEED;
import static net.foxyas.changedaddon.qte.FightToKeepConsciousness.STRUGGLE_TIME;

public class KeyPressMinigameScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = ChangedAddonMod.textureLoc(
            "textures/screens/qtes/fight_to_keep_consciousness/fight_to_keep_consciousness_minigame");
    private static final int imageWidth = 200;
    private static final int halfImgWidth = imageWidth / 2;
    private static final int imageHeight = 166;
    private static final int halfImgHeight = imageHeight / 2;

    private final Player player;
    private final Button button_fight;
    private final Button button_give_up;

    public KeyPressMinigameScreen() {
        super(TextComponent.EMPTY);
        minecraft = Minecraft.getInstance();
        player = minecraft.player;

        button_fight = new Button(0, 0, 166, 20,
                Component.translatable("gui.changed_addon.fight_to_keep_consciousness_minigame.button_fight"),
                e ->
                        ChangedAddonMod.PACKET_HANDLER.sendToServer(new ServerboundProgressFTKCPacket())
        );

        button_give_up = new Button(0, 0, 166, 20,
                Component.translatable("gui.changed_addon.fight_to_keep_consciousness_minigame.button_give_up"),
                e -> minecraft.setScreen(null)
        );
    }

    /* ----------------------------- STATIC METHODS ----------------------------- */
    public static String getProgressText(@NotNull Player entity) {
        return ChangedAddonVariables.ofOrDefault(entity)
                .consciousnessFightProgress + "/" + STRUGGLE_NEED;
    }

    public static String getTimeRemaining(@NotNull Player player) {
        TransfurVariantInstance<?> transfurInstance = ProcessTransfur.getPlayerTransfurVariant(player);

        return transfurInstance == null ? "" : Integer.toString(STRUGGLE_TIME - transfurInstance.ageAsVariant);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        int halfWidth = width / 2;
        int halfHeight = height / 2;

        button_fight.x = halfWidth - button_fight.getWidth() / 2;
        button_fight.y = halfHeight - button_fight.getHeight() - 20;

        button_give_up.x = halfWidth - button_give_up.getWidth() / 2;
        button_give_up.y = halfHeight + button_give_up.getHeight() + 20;

        addRenderableWidget(button_fight);
        addRenderableWidget(button_give_up);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(pGuiGraphics.pose(), partialTick);

        int halfWidth = width / 2;
        int halfHeight = height / 2;

        super.render(pGuiGraphics, mouseX, mouseY, partialTick);

        RenderUtil.drawCentered(font, pGuiGraphics, Component.translatable("gui.changed_addon.fight_to_keep_consciousness_minigame.label_text", getTimeRemaining(player)), halfWidth, halfHeight - 50, -12829636);
        RenderUtil.drawCentered(font, pGuiGraphics, getProgressText(player), halfWidth, halfHeight + 7, -12829636);
    }

    public void renderBackground(@NotNull PoseStack poseStack, float partialTick) {
        TransfurVariantInstance<?> tf = ProcessTransfur.getPlayerTransfurVariant(player);

        if (tf != null) {
            float fightProgress = ChangedAddonVariables.nonNullOf(player).consciousnessFightProgress / FightToKeepConsciousness.STRUGGLE_NEED;
            float loseProgress = Mth.lerp(partialTick, Math.max(0, tf.ageAsVariant - 1), tf.ageAsVariant) / FightToKeepConsciousness.STRUGGLE_TIME;

            int alpha = (int) (128 + 128 * (loseProgress - fightProgress));

            RenderUtil.fill(poseStack.last().pose(), 0, 0, width, height, alpha << 24 | tf.getTransfurColor().toInt());
        } else {
            RenderUtil.fill(poseStack.last().pose(), 0, 0, width, height, -8355712);
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        blit(poseStack, width / 2 - halfImgWidth, height / 2 - halfImgHeight, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        RenderSystem.disableBlend();
    }

    @Override
    public void tick() {
        if (ChangedAddonVariables.ofOrDefault(player).FTKCminigameType == null) {
            minecraft.setScreen(null);
        }
    }
}
