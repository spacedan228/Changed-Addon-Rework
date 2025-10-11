package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.menu.CustomMerchantMenu;
import net.foxyas.changedaddon.menu.CustomMerchantOffer;
import net.foxyas.changedaddon.menu.CustomMerchantOffers;
import net.foxyas.changedaddon.network.packets.ServerboundCustomSelectTradePacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class CustomMerchantScreen extends AbstractContainerScreen<CustomMerchantMenu> {

    /** The GUI texture for the villager merchant GUI. */
    private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
    private static final Component TRADES_LABEL = new TranslatableComponent("merchant.trades");
    private static final Component DEPRECATED_TOOLTIP = new TranslatableComponent("merchant.deprecated");
    /** The integer value corresponding to the currently selected merchant recipe. */
    private int shopItem;
    private final TradeOfferButton[] tradeOfferButtons = new TradeOfferButton[7];
    private int scrollOff;
    private boolean isDragging;

    public CustomMerchantScreen(CustomMerchantMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = 276;
        inventoryLabelX = 107;
    }

    protected void init() {
        super.init();
        int i = (width - imageWidth) / 2;
        int j = (height - imageHeight) / 2;
        int k = j + 16 + 2;

        for(int l = 0; l < 7; ++l) {
            tradeOfferButtons[l] = addRenderableWidget(new TradeOfferButton(i + 5, k, l, (button) -> {
                if (button instanceof TradeOfferButton) {
                    shopItem = ((TradeOfferButton)button).getIndex() + scrollOff;
                    menu.setSelectionHint(shopItem);
                    menu.tryMoveItems(shopItem);
                    ChangedAddonMod.PACKET_HANDLER.sendToServer(new ServerboundCustomSelectTradePacket(shopItem));
                }
            }));
            k += 20;
        }
    }

    protected void renderLabels(@NotNull PoseStack stack, int x, int y) {
        font.draw(stack, title, (float)(49 + imageWidth / 2 - font.width(title) / 2), 6.0F, 4210752);
        font.draw(stack, playerInventoryTitle, (float)inventoryLabelX, (float)inventoryLabelY, 4210752);
        int l = font.width(TRADES_LABEL);
        font.draw(stack, TRADES_LABEL, (float)(5 - l / 2 + 48), 6.0F, 4210752);
    }

    protected void renderBg(@NotNull PoseStack stack, float partialTick, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
        int i = (width - imageWidth) / 2;
        int j = (height - imageHeight) / 2;
        blit(stack, i, j, getBlitOffset(), 0.0F, 0.0F, imageWidth, imageHeight, 512, 256);
        CustomMerchantOffers merchantoffers = menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int k = shopItem;
            if (k < 0 || k >= merchantoffers.size()) return;

            CustomMerchantOffer merchantoffer = merchantoffers.get(k);
            if (merchantoffer.isOutOfStock()) {
                RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                blit(stack, leftPos + 83 + 99, topPos + 35, getBlitOffset(), 311.0F, 0.0F, 28, 21, 512, 256);
            }
        }
    }

    private void renderScroller(PoseStack stack, int x, int y, CustomMerchantOffers offers) {
        int i = offers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int i1 = Math.min(113, scrollOff * k);
            if (scrollOff == i - 1) {
                i1 = 113;
            }

            blit(stack, x + 94, y + 18 + i1, getBlitOffset(), 0.0F, 199.0F, 6, 27, 512, 256);
        } else {
            blit(stack, x + 94, y + 18, getBlitOffset(), 6.0F, 199.0F, 6, 27, 512, 256);
        }

    }

    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTick) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTick);
        CustomMerchantOffers offers = menu.getOffers();
        if(offers.isEmpty()){
            renderTooltip(stack, mouseX, mouseY);
            return;
        }

        int i = (width - imageWidth) / 2;
        int j = (height - imageHeight) / 2;
        int k = j + 16 + 1;
        int l = i + 5 + 5;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
        renderScroller(stack, i, j, offers);
        int i1 = 0;

        Ingredient ingredient;
        ItemStack cost, result;
        for(CustomMerchantOffer offer : offers) {
            if (!canScroll(offers.size()) || (i1 >= scrollOff && i1 < 7 + scrollOff)) {
                itemRenderer.blitOffset = 100.0F;
                int j1 = k + 2;

                ingredient = offer.getCostA();
                cost = currentStack(ingredient);
                itemRenderer.renderAndDecorateFakeItem(cost, l, j1);
                itemRenderer.renderGuiItemDecorations(font, cost, l, j1);

                ingredient = offer.getCostB();
                if (!ingredient.isEmpty()) {
                    cost = currentStack(ingredient);
                    itemRenderer.renderAndDecorateFakeItem(cost, i + 5 + 35, j1);
                    itemRenderer.renderGuiItemDecorations(font, cost, i + 5 + 35, j1);
                }

                result = offer.getResult();
                renderButtonArrows(stack, offer, i, j1);

                PoseStack poseStack = new PoseStack();
                poseStack.translate(0.0D, 0.0D, itemRenderer.blitOffset + 200.0F);
                MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                String str = String.valueOf(offer.getUsesLeft());
                font.drawInBatch(str, i + 5 + 46 + 19 - 2 - font.width(str), (float)(j1 + 6 + 3), 16777215, true, poseStack.last().pose(), multibuffersource$buffersource, false, 0, 15728880);
                multibuffersource$buffersource.endBatch();

                itemRenderer.renderAndDecorateFakeItem(result, i + 5 + 68, j1);
                itemRenderer.renderGuiItemDecorations(font, result, i + 5 + 68, j1);
                itemRenderer.blitOffset = 0.0F;
                k += 20;
            }

            ++i1;
        }

        int k1 = shopItem;
        CustomMerchantOffer merchantoffer1 = offers.get(k1);

        if (merchantoffer1.isOutOfStock() && isHovering(186, 35, 22, 21, mouseX, mouseY) && menu.canRestock()) {
            renderTooltip(stack, DEPRECATED_TOOLTIP, mouseX, mouseY);
        }

        for(TradeOfferButton button : tradeOfferButtons) {
            if (button.isHoveredOrFocused()) {
                button.renderToolTip(stack, mouseX, mouseY);
            }
            button.visible = button.index < menu.getOffers().size();
        }

        RenderSystem.enableDepthTest();

        renderTooltip(stack, mouseX, mouseY);
    }

    private static final int MS_PER_ITEM = 1000;

    private static ItemStack currentStack(Ingredient ingredient){
        ItemStack[] stacks = ingredient.getItems();
        int time = Math.toIntExact(System.currentTimeMillis() % ((long) stacks.length * MS_PER_ITEM));
        return stacks[time / MS_PER_ITEM];
    }

    private void renderButtonArrows(PoseStack stack, CustomMerchantOffer offer, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
        if (offer.isOutOfStock()) {
            blit(stack, x + 5 + 35 + 20, y + 3, getBlitOffset(), 25.0F, 171.0F, 10, 9, 512, 256);
        } else {
            blit(stack, x + 5 + 35 + 20, y + 3, getBlitOffset(), 15.0F, 171.0F, 10, 9, 512, 256);
        }
    }

    private boolean canScroll(int numOffers) {
        return numOffers > 7;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int i = menu.getOffers().size();
        if (canScroll(i)) {
            int j = i - 7;
            scrollOff = Mth.clamp((int)((double)scrollOff - delta), 0, j);
        }

        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            int j = topPos + 18;
            int k = j + 139;
            int l = menu.getOffers().size() - 7;
            float f = ((float)mouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            scrollOff = Mth.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        isDragging = false;
        int i = (width - imageWidth) / 2;
        int j = (height - imageHeight) / 2;
        if (canScroll(menu.getOffers().size()) && mouseX > (double)(i + 94) && mouseX < (double)(i + 94 + 6) && mouseY > (double)(j + 18) && mouseY <= (double)(j + 18 + 139 + 1)) {
            isDragging = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    class TradeOfferButton extends Button {

        final int index;

        public TradeOfferButton(int x, int y, int index, OnPress onPress) {
            super(x, y, 89, 20, TextComponent.EMPTY, onPress);
            this.index = index;
            visible = false;
        }

        public int getIndex() {
            return index;
        }

        public void renderToolTip(@NotNull PoseStack stack, int mouseX, int mouseY) {
            if (isHovered && CustomMerchantScreen.this.menu.getOffers().size() > index + CustomMerchantScreen.this.scrollOff) {
                CustomMerchantOffer offer = CustomMerchantScreen.this.menu.getOffers().get(index + CustomMerchantScreen.this.scrollOff);

                if (mouseX < x + 20) {
                    ItemStack itemstack = currentStack(offer.getCostA());
                    CustomMerchantScreen.this.renderTooltip(stack, itemstack, mouseX, mouseY);
                } else if (mouseX < x + 50 && mouseX > x + 30) {
                    Ingredient ingredient = offer.getCostB();
                    if(!ingredient.isEmpty()){
                        CustomMerchantScreen.this.renderTooltip(stack, currentStack(ingredient), mouseX, mouseY);
                    }
                } else if (mouseX > x + 65) {
                    CustomMerchantScreen.this.renderTooltip(stack, offer.getResult(), mouseX, mouseY);
                }
            }
        }
    }
}
