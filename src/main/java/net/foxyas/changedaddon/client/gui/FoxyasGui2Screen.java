package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.network.FoxyasGui2ButtonMessage;
import net.foxyas.changedaddon.world.inventory.FoxyasGui2Menu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FoxyasGui2Screen extends AbstractContainerScreen<FoxyasGui2Menu> {

    private static final ResourceLocation texture = new ResourceLocation("changed_addon:textures/screens/foxyas_gui_2.png");

    private final Player player;
    Button button_deal;
    Button button_no;
    Button button_aa;

    public FoxyasGui2Screen(FoxyasGui2Menu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        player = container.player;
        imageWidth = 205;
        imageHeight = 166;
    }

    @Override
    public void render(@NotNull PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, texture);
        blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        RenderSystem.disableBlend();
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }

    @Override
    protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, new TranslatableComponent("gui.changed_addon.foxyas_gui_2.label_oh_human_why_but_if_you_really"), 4, 5, -12829636);
        this.font.draw(poseStack, new TranslatableComponent("gui.changed_addon.foxyas_gui_2.label_it_i_can_do_it"), 5, 17, -12829636);
        if (player.getPersistentData().getBoolean("Deal"))
            this.font.draw(poseStack, new TranslatableComponent("gui.changed_addon.foxyas_gui_2.label_it_i_can_do_it1"), 8, 36, -12829636);
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        button_deal = new Button(this.leftPos + 135, this.topPos + 32, 46, 20, new TranslatableComponent("gui.changed_addon.foxyas_gui_2.button_deal"), e -> {
            if (!player.getPersistentData().getBoolean("Deal")) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new FoxyasGui2ButtonMessage(0));
                FoxyasGui2ButtonMessage.handleButtonAction(player, 0);
            }
        }) {
            @Override
            public void render(@NotNull PoseStack ms, int gx, int gy, float ticks) {
                if (!player.getPersistentData().getBoolean("Deal")) super.render(ms, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_deal);

        button_no = new Button(this.leftPos + 48, this.topPos + 51, 35, 20, new TranslatableComponent("gui.changed_addon.foxyas_gui_2.button_no"), e -> {
            if (player.getPersistentData().getBoolean("Deal")) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new FoxyasGui2ButtonMessage(1));
                FoxyasGui2ButtonMessage.handleButtonAction(player, 1);
            }
        }) {
            @Override
            public void render(@NotNull PoseStack ms, int gx, int gy, float ticks) {
                if (player.getPersistentData().getBoolean("Deal")) super.render(ms, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_no);

        button_aa = new Button(this.leftPos + 6, this.topPos + 51, 35, 20, new TranslatableComponent("gui.changed_addon.foxyas_gui_2.button_aa"), e -> {
            if (player.getPersistentData().getBoolean("Deal")) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new FoxyasGui2ButtonMessage(2));
                FoxyasGui2ButtonMessage.handleButtonAction(player, 2);
            }
        }) {
            @Override
            public void render(@NotNull PoseStack ms, int gx, int gy, float ticks) {
                if (player.getPersistentData().getBoolean("Deal")) super.render(ms, gx, gy, ticks);
            }
        };
        this.addRenderableWidget(button_aa);
    }
}
