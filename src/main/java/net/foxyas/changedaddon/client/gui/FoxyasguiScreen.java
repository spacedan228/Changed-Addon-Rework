package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.entity.advanced.LatexSnowFoxFoxyasEntity;
import net.foxyas.changedaddon.network.FoxyasGuiButtonMessage;
import net.foxyas.changedaddon.world.inventory.FoxyasGuiMenu;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FoxyasguiScreen extends AbstractContainerScreen<FoxyasGuiMenu> {

    private static final ResourceLocation texture = ResourceLocation.parse("changed_addon:textures/screens/foxyasgui.png");

    private final Player player;
    private final LatexSnowFoxFoxyasEntity entity;
    Button button_trade;
    Button button_i_want_be_transfured_by_you;

    public FoxyasguiScreen(FoxyasGuiMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        player = container.player;
        entity = container.entity;
        this.imageWidth = 345;
        this.imageHeight = 177;
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
        this.font.draw(poseStack, new TranslatableComponent("gui.changed_addon.foxyasgui.label_hello_im_foxyas_i_dont_want_to"), 5, 4, -12829636);
        this.font.draw(poseStack, new TranslatableComponent("gui.changed_addon.foxyasgui.label_pls_dont_kill_me"), 5, 18, -12829636);
        this.font.draw(poseStack, new TranslatableComponent("gui.changed_addon.foxyasgui.label_i_just_need_2_oranges_and_one_gl"), 5, 32, -12829636);
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void init() {
        super.init();
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        button_trade = new Button(this.leftPos + 224, this.topPos + 115, 51, 20, new TranslatableComponent("gui.changed_addon.foxyasgui.button_trade"), e ->
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new FoxyasGuiButtonMessage(0, entity.getId())));

        addRenderableWidget(button_trade);
        button_i_want_be_transfured_by_you = new Button(this.leftPos + 13, this.topPos + 66, 165, 20, new TranslatableComponent("gui.changed_addon.foxyasgui.button_i_want_be_transfured_by_you"), e -> {
            if (!ProcessTransfur.isPlayerTransfurred(player)) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new FoxyasGuiButtonMessage(1, entity.getId()));
            }
        }) {
            @Override
            public void render(@NotNull PoseStack ms, int gx, int gy, float ticks) {
                if (!ProcessTransfur.isPlayerTransfurred(player)) super.render(ms, gx, gy, ticks);
            }
        };

        addRenderableWidget(button_i_want_be_transfured_by_you);
    }
}
