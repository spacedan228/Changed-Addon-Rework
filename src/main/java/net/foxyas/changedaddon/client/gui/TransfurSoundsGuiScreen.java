package net.foxyas.changedaddon.client.gui;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.menu.TransfurSoundsGuiMenu;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.network.packet.TransfurSoundsGuiButtonPacket;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.foxyas.changedaddon.variant.TransfurSoundsDetails;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransfurSoundsGuiScreen
        extends AbstractContainerScreen<TransfurSoundsGuiMenu> {

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

    /* ------------------------------------------------------------
     * Action order (visual chain)
     * ------------------------------------------------------------ */
    private static final TransfurSoundsDetails.TransfurSoundAction[] ACTION_CHAIN = Arrays.stream(TransfurSoundsDetails.TransfurSoundAction.values()).toArray(TransfurSoundsDetails.TransfurSoundAction[]::new);

    /* ------------------------------------------------------------
     * Constructor
     * ------------------------------------------------------------ */
    public TransfurSoundsGuiScreen(
            TransfurSoundsGuiMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        this.player = menu.player;
        this.imageWidth = 176;
        this.imageHeight = 150;
    }

    /* ------------------------------------------------------------
     * Init
     * ------------------------------------------------------------ */
    @Override
    public void init() {
        super.init();

        int y = topPos + START_Y;

        for (TransfurSoundsDetails.TransfurSoundAction action : ACTION_CHAIN) {
            if (!action.canUse(player))
                continue;

            addRenderableWidget(
                    createSoundButton(leftPos + START_X, y, action)
            );

            y += GAP_Y;
        }

        if (this.player != null) {
            ChangedAddonVariables.PlayerVariables playerVariables = ChangedAddonVariables.nonNullOf(player);
            playerVariables.actCooldown = false;
            playerVariables.syncPlayerVariables(player);
        }
    }

    /* ------------------------------------------------------------
     * Rendering
     * ------------------------------------------------------------ */
    @Override
    public void render(
            @NotNull GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTicks
    ) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                leftPos + 89,
                topPos + 133,
                30,
                (float) Math.atan((leftPos + 89 - mouseX) / 40.0),
                (float) Math.atan((topPos + 83 - mouseY) / 40.0),
                player
        );

        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(
            @NotNull GuiGraphics graphics,
            float partialTicks,
            int x,
            int y
    ) {
        // background handled elsewhere
    }

    @Override
    protected void renderLabels(
            @NotNull GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        // -------------------------------
        // Title
        // -------------------------------
        graphics.drawCenteredString(
                font,
                Component.translatable("gui.changed_addon.transfur_sounds_gui.label_transfur_sounds"),
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
            graphics.drawCenteredString(
                    font,
                    subtitles.get(i),
                    imageWidth / 2,
                    startY + (i * lineHeight),
                    0x3A3A3A
            );
        }
    }

    /* ------------------------------------------------------------
     * Buttons
     * ------------------------------------------------------------ */
    protected Button createSoundButton(
            int x,
            int y,
            TransfurSoundsDetails.TransfurSoundAction action
    ) {
        return Button.builder(
                Component.translatable(
                        "gui.changed_addon.transfur_sounds_gui." +
                                action.name().toLowerCase()
                ),
                b -> sendSoundPacket(action)
        ).bounds(
                x, y,
                BUTTON_W, BUTTON_H
        ).build();
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
            return List.of(Component.literal("§7Not Transfurred"));
        }

        List<Component> subtitles = new ArrayList<>();

        // Prefixo base
        subtitles.add(Component.literal("§fYou are a"));

        // ===============================
        // Species / family
        // ===============================

        List<MutableComponent> species = new ArrayList<>();

        if (PlayerUtil.isCatTransfur(player)) {
            species.add(Component.literal("§fCat"));
        }

        if (PlayerUtil.isFoxTransfur(player)) {
            species.add(Component.literal("§fFox"));
        }

        if (PlayerUtil.isWolfTransfur(player)) {
            species.add(Component.literal("§fCanine"));
        }

        if (species.isEmpty()) {
            species.add(Component.literal("§7Unknown"));
        }

        subtitles.add(joinWithSeparator(species, "§7 / "));

        // ===============================
        // Special traits
        // ===============================

        if (canRoar()) {
            subtitles.add(Component.literal("§6Apex Predator"));
        }

        return subtitles;
    }

    private static MutableComponent joinWithSeparator(List<MutableComponent> components, String separator) {
        MutableComponent result = components.get(0);

        for (int i = 1; i < components.size(); i++) {
            result = result
                    .append(Component.literal(separator))
                    .append(components.get(i));
        }

        return result;
    }

    protected boolean isOnCooldown() {
        return ChangedAddonVariables.ofOrDefault(player).actCooldown;
    }

    protected boolean canRoar() {
        if (!ProcessTransfur.isPlayerTransfurred(player))
            return false;

        ResourceLocation id =
                ProcessTransfur.getPlayerTransfurVariant(player).getFormId();

        if (id == null)
            return false;

        String path = id.toString();

        return path.contains("lion")
                || path.contains("tiger")
                || path.startsWith("changed_addon:form_experiment009");
    }
}