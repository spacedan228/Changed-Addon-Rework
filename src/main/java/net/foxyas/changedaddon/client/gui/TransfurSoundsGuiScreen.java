package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.menu.TransfurSoundsGuiMenu;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.network.packet.TransfurSoundsGuiButtonPacket;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TransfurSoundsGuiScreen extends AbstractContainerScreen<TransfurSoundsGuiMenu> {

    private final Player player;
    Button button_1;
    Button button_2;
    Button button_3;
    Button button_4;
    Button button_5;
    Button button_6;
    Button button_7;
    Button button_cooldown;
    Button button_61;

    public TransfurSoundsGuiScreen(TransfurSoundsGuiMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.player = container.player;
        this.imageWidth = 176;
        this.imageHeight = 150;
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, mouseX, mouseY, partialTicks);

        InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics, this.leftPos + 89, this.topPos + 133, 30, (float) Math.atan((this.leftPos + 89 - mouseX) / 40.0), (float) Math.atan((this.topPos + 83 - mouseY) / 40.0), player);

        renderTooltip(pGuiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float partialTicks, int gx, int gy) {
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int mouseX, int mouseY) {
        pGuiGraphics.drawString(font, Component.translatable("gui.changed_addon.transfur_sounds_gui.label_transfur_sounds"), 49, -24, -1);
        pGuiGraphics.drawString(font, execute(), 36, -11, -12829636);
    }

    public String execute() {
        TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant == null)
            return "§fYou are a Transfur";

        if (PlayerUtil.isCatTransfur(player)) return "§fYou are a Cat Transfur";

        if (PlayerUtil.isWolfTransfur(player)) return "§fYou are a Dog Transfur";

        return "§fYou are a Transfur";
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void init() {
        super.init();
        assert this.minecraft != null;
        button_1 = new Button(this.leftPos + 4, this.topPos + 6, 30, 20, Component.translatable("gui.changed_addon.transfur_sounds_gui.button_1"), e -> {
            if (PlayerUtil.isCatTransfur(player)) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TransfurSoundsGuiButtonPacket(0));
                TransfurSoundsGuiButtonPacket.handleButtonAction(player, 0);
            }
        }, Supplier::get) {
            @Override
            public void render(@NotNull GuiGraphics pGuiGraphics, int gx, int gy, float ticks) {
                if (PlayerUtil.isCatTransfur(player))
                    super.render(pGuiGraphics, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_1);

        button_2 = new Button(this.leftPos + 4, this.topPos + 28, 30, 20, Component.translatable("gui.changed_addon.transfur_sounds_gui.button_2"), e -> {
            if (PlayerUtil.isCatTransfur(player)) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TransfurSoundsGuiButtonPacket(1));
                TransfurSoundsGuiButtonPacket.handleButtonAction(player, 1);
            }
        }, Supplier::get) {
            @Override
            public void render(@NotNull GuiGraphics pGuiGraphics, int gx, int gy, float ticks) {
                if (PlayerUtil.isCatTransfur(player))
                    super.render(pGuiGraphics, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_2);

        button_3 = new Button(this.leftPos + 138, this.topPos + 7, 30, 20, Component.translatable("gui.changed_addon.transfur_sounds_gui.button_3"), e -> {
            if (PlayerUtil.isWolfTransfur(player)) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TransfurSoundsGuiButtonPacket(2));
                TransfurSoundsGuiButtonPacket.handleButtonAction(player, 2);
            }
        }, Supplier::get) {
            @Override
            public void render(@NotNull GuiGraphics pGuiGraphics, int gx, int gy, float ticks) {
                if (PlayerUtil.isWolfTransfur(player))
                    super.render(pGuiGraphics, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_3);

        button_4 = new Button(this.leftPos + 138, this.topPos + 29, 30, 20, Component.translatable("gui.changed_addon.transfur_sounds_gui.button_4"), e -> {
            if (PlayerUtil.isWolfTransfur(player)) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TransfurSoundsGuiButtonPacket(3));
                TransfurSoundsGuiButtonPacket.handleButtonAction(player, 3);
            }
        }, Supplier::get) {
            @Override
            public void render(@NotNull GuiGraphics pGuiGraphics, int gx, int gy, float ticks) {
                if (PlayerUtil.isWolfTransfur(player))
                    super.render(pGuiGraphics, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_4);

        button_5 = new Button(this.leftPos + 138, this.topPos + 51, 30, 20, Component.translatable("gui.changed_addon.transfur_sounds_gui.button_5"), e -> {
            if (PlayerUtil.isWolfTransfur(player)) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TransfurSoundsGuiButtonPacket(4));
                TransfurSoundsGuiButtonPacket.handleButtonAction(player, 4);
            }
        }, Supplier::get) {
            @Override
            public void render(@NotNull GuiGraphics pGuiGraphics, int gx, int gy, float ticks) {
                if (PlayerUtil.isWolfTransfur(player))
                    super.render(pGuiGraphics, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_5);

        button_6 = new Button(this.leftPos + 4, this.topPos + 50, 30, 20, Component.translatable("gui.changed_addon.transfur_sounds_gui.button_6"), e -> {
            if (PlayerUtil.isCatTransfur(player)) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TransfurSoundsGuiButtonPacket(5));
                TransfurSoundsGuiButtonPacket.handleButtonAction(player, 5);
            }
        }, Supplier::get) {
            @Override
            public void render(@NotNull GuiGraphics pGuiGraphics, int gx, int gy, float ticks) {
                if (PlayerUtil.isCatTransfur(player))
                    super.render(pGuiGraphics, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_6);

        button_7 = new Button(this.leftPos + 4, this.topPos + 72, 40, 20, Component.translatable("gui.changed_addon.transfur_sounds_gui.button_7"), e -> {
            if (PlayerUtil.isCatTransfur(player)) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TransfurSoundsGuiButtonPacket(6));
                TransfurSoundsGuiButtonPacket.handleButtonAction(player, 6);
            }
        }, Supplier::get) {
            @Override
            public void render(@NotNull GuiGraphics pGuiGraphics, int gx, int gy, float ticks) {
                if (PlayerUtil.isCatTransfur(player))
                    super.render(pGuiGraphics, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_7);

        button_cooldown = new Button(this.leftPos + 47, this.topPos + 5, 77, 20, Component.translatable("gui.changed_addon.transfur_sounds_gui.button_cooldown"), e -> {
            if (isOnCooldown()) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TransfurSoundsGuiButtonPacket(7));
                TransfurSoundsGuiButtonPacket.handleButtonAction(player, 7);
            }
        }, Supplier::get) {
            @Override
            public void render(@NotNull GuiGraphics pGuiGraphics, int gx, int gy, float ticks) {
                if (isOnCooldown()) super.render(pGuiGraphics, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_cooldown);

        button_61 = new Button(this.leftPos + 4, this.topPos + 94, 30, 20, Component.translatable("gui.changed_addon.transfur_sounds_gui.button_61"), e -> {
            if (canRoar()) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TransfurSoundsGuiButtonPacket(8));
                TransfurSoundsGuiButtonPacket.handleButtonAction(player, 8);
            }
        }, Supplier::get) {
            @Override
            public void render(@NotNull GuiGraphics pGuiGraphics, int gx, int gy, float ticks) {
                if (canRoar())
                    super.render(pGuiGraphics, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_61);
    }

    protected boolean isOnCooldown() {
        return ChangedAddonVariables.ofOrDefault(player).actCooldown;
    }

    protected boolean canRoar() {
        if (ProcessTransfur.isPlayerTransfurred(player)) {
            ResourceLocation Res = ProcessTransfur.getPlayerTransfurVariant(player).getFormId();
            if (Res != null) {
                String id = Res.toString();
                return id.contains("lion")
                        || id.contains("tiger")
                        || id.startsWith("changed_addon:form_experiment009");
            }
        }
        return false;
    }
}
