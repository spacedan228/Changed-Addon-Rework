package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.event.TransfurEvents;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.network.packet.TransfurSoundsGuiButtonPacket;
import net.foxyas.changedaddon.util.ComponentUtil;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.foxyas.changedaddon.variant.TransfurSoundsDetails;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TransfurSoundsGuiScreen extends Screen {

    /* ------------------------------------------------------------
     * Player
     * ------------------------------------------------------------ */
    private final Player player;

    /* ------------------------------------------------------------
     * Layout constants
     * ------------------------------------------------------------ */
    private static final int BUTTON_W = 30;
    private static final int BUTTON_H = 20;
    private static final int START_X = 4;
    private static final int START_Y = 6;
    private static final int GAP_Y = 22;
    private static final int imageWidth = 176;
    private static final int imageHeight = 150;

    protected int topPos;
    protected int leftPos;

    List<Button> buttons = new ArrayList<>();

    /* ------------------------------------------------------------
     * Constructor
     * ------------------------------------------------------------ */
    public TransfurSoundsGuiScreen() {
        super(ComponentUtil.literal("TransfurSoundsGui"));
        this.player = Minecraft.getInstance().player;
    }

    /* ------------------------------------------------------------
     * Init
     * ------------------------------------------------------------ */
    @Override
    public void init() {
        super.init();

        leftPos = (this.width - imageWidth) / 2;
        topPos = (this.height - imageHeight) / 2;

        int y = topPos + START_Y;

        buttons.clear();
        Button button;
        for (TransfurSoundsDetails.TransfurSoundType type : TransfurSoundsDetails.TransfurSoundType.values()) {
            if (!type.predicate.test(player)) continue;

            for (TransfurSoundsDetails.TransfurSoundAction action : type.actions) {
                button = createSoundButton(leftPos + START_X, y, action);

                buttons.add(button);
                addRenderableWidget(button);

                y += GAP_Y;
            }
        }
    }


    @Override
    public void tick() {
        boolean active = isNotOnCooldown();
        for (Button button : buttons) {
            button.active = active;
        }
    }

    /* ------------------------------------------------------------
     * Rendering
     * ------------------------------------------------------------ */
    @Override
    public void render(
            @NotNull PoseStack stack,
            int mouseX,
            int mouseY,
            float partialTicks
    ) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);

        InventoryScreen.renderEntityInInventory(
                leftPos + 89,
                topPos + 133,
                30,
                (float) Math.atan((leftPos + 89 - mouseX) / 40.0),
                (float) Math.atan((topPos + 83 - mouseY) / 40.0),
                player
        );

        renderLabels(stack, mouseX, mouseY);
    }

    protected void renderLabels(
            @NotNull PoseStack stack,
            int mouseX,
            int mouseY
    ) {
        stack.pushPose();
        stack.translate(leftPos, topPos, 0);
        // -------------------------------
        // Title
        // -------------------------------
        GuiComponent.drawCenteredString(
                stack,
                font,
                ComponentUtil.translatable("gui.changed_addon.transfur_sounds_gui.label_transfur_sounds"),
                imageWidth / 2,
                -24,
                0xFFFFFF
        );

        // -------------------------------
        // Subtitles (multi-line)
        // -------------------------------
        List<Component> subtitles = getPlayerSubtitle();

        int startY = -11;
        int lineHeight = font.lineHeight + 2;

        for (int i = 0; i < subtitles.size(); i++) {
            GuiComponent.drawCenteredString(
                    stack,
                    font,
                    subtitles.get(i),
                    imageWidth / 2,
                    startY + (i * lineHeight),
                    0x3A3A3A
            );
        }
        stack.popPose();
    }

    /* ------------------------------------------------------------
     * Buttons
     * ------------------------------------------------------------ */
    protected Button createSoundButton(
            int x,
            int y,
            TransfurSoundsDetails.TransfurSoundAction action
    ) {
        return new Button(x, y, BUTTON_W, BUTTON_H,
                ComponentUtil.translatable(
                "gui.changed_addon.transfur_sounds_gui." +
                        action.name().toLowerCase()
        ), b -> {
                    if (isNotOnCooldown()) sendSoundPacket(action);
                });
    }

    protected void sendSoundPacket(
            TransfurSoundsDetails.TransfurSoundAction action
    ) {
        ChangedAddonMod.PACKET_HANDLER.sendToServer(
                new TransfurSoundsGuiButtonPacket(action.ordinal())
        );
    }

    /* ------------------------------------------------------------
     * Titles & state
     * ------------------------------------------------------------ */
    protected List<Component> getPlayerSubtitle() {

        if (!ProcessTransfur.isPlayerTransfurred(player)) {
            return List.of(ComponentUtil.literal("§7Not Transfurred"));
        }

        List<Component> subtitles = new ArrayList<>();

        // Prefixo base
        subtitles.add(ComponentUtil.literal("§fYou are a"));

        // ===============================
        // Species / family
        // ===============================

        List<MutableComponent> species = new ArrayList<>();

        if (PlayerUtil.isCatTransfur(player)) {
            species.add(ComponentUtil.literal("§fCat"));
        }

        if (PlayerUtil.isFoxTransfur(player)) {
            species.add(ComponentUtil.literal("§fFox"));
        }

        if (PlayerUtil.isWolfTransfur(player)) {
            species.add(ComponentUtil.literal("§fCanine"));
        }

        if (PlayerUtil.isDragonTransfur(player)) {
            species.add(ComponentUtil.literal("§fDragon"));
        }

        if (PlayerUtil.isAquaticTransfur(player)) {
            species.add(ComponentUtil.literal("§fFish"));
        }

        if (species.isEmpty()) {
            species.add(ComponentUtil.literal("§7Unknown"));
        }

        subtitles.add(joinWithSeparator(species, "§7 / "));

        // ===============================
        // Special traits
        // ===============================

        if (isApexPredator()) {
            subtitles.add(ComponentUtil.literal("§6Apex Predator"));
        }

        return subtitles;
    }

    private static MutableComponent joinWithSeparator(List<MutableComponent> components, String separator) {
        MutableComponent result = components.get(0);

        for (int i = 1; i < components.size(); i++) {
            result = result
                    .append(ComponentUtil.literal(separator))
                    .append(components.get(i));
        }

        return result;
    }

    protected boolean isNotOnCooldown() {
        return !ChangedAddonVariables.ofOrDefault(player).actCooldown;
    }

    protected boolean isApexPredator() {
        if (!ProcessTransfur.isPlayerTransfurred(player))
            return false;

        ResourceLocation id =
                ProcessTransfur.getPlayerTransfurVariant(player).getFormId();

        if (id == null)
            return false;

        String path = id.toString();

        return path.contains("lion")
                || path.contains("tiger")
                || path.startsWith("changed_addon:form_experiment009") || TransfurEvents.resolveChangedEntity(player).getType().is(ChangedAddonTags.EntityTypes.CAN_ROAR);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}