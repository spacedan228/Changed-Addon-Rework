package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.procedures.BlockstartinfoProcedure;
import net.foxyas.changedaddon.procedures.IfBlockisfullProcedure;
import net.foxyas.changedaddon.procedures.RecipeProgressProcedure;
import net.foxyas.changedaddon.world.inventory.CatalyzerGuiMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class CatalyzerGuiScreen extends AbstractContainerScreen<CatalyzerGuiMenu> {

    private static final ResourceLocation texture = ResourceLocation.parse("changed_addon:textures/screens/catalyzer_gui_new.png");

    private final Level world;
    private final int x, y, z;
    private final CatalyzerGuiMenu menu;

    public CatalyzerGuiScreen(CatalyzerGuiMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        menu = container;
        this.imageWidth = 200;
        this.imageHeight = 170;
    }

    @Override
    public void render(@NotNull PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
        if (menu.isSlotEmpty(0))
            if (mouseX > leftPos + 18 && mouseX < leftPos + 42 && mouseY > topPos + 40 && mouseY < topPos + 64)
                this.renderTooltip(ms, new TranslatableComponent("gui.changed_addon.catalyzer_gui.tooltip_put_the_powders_or_syringe"), mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, texture);
        blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        BlockEntity be = world.getBlockEntity(new BlockPos(x, y, z));
        int progressInt = be != null ? (int) (be.getTileData().getDouble("recipe_progress") / 3.57) : -1;

        RenderSystem.setShaderTexture(0, ResourceLocation.parse("changed_addon:textures/screens/empty_bar.png"));
        blit(ms, this.leftPos + 83, this.topPos + 46, 0, 0, 32, 12, 32, 12);

        RenderSystem.setShaderTexture(0, ResourceLocation.parse("changed_addon:textures/screens/bar_full.png"));
        blit(ms, this.leftPos + 83 + 2, this.topPos + 46 + 2, 0, 0, progressInt, 8, progressInt, 8);

        assert this.minecraft != null;
        assert this.minecraft.level != null;
        long gameTime = this.minecraft.level.getGameTime();
        int animationPeriod = 40; // ticks (2 segundos)
        boolean showSyringe = (gameTime % animationPeriod) < (animationPeriod / 2);

        ResourceLocation icon = showSyringe
                ? ResourceLocation.parse("changed_addon:textures/screens/syringes.png")
                : ResourceLocation.parse("changed_addon:textures/screens/dusts.png");

        int yOffset = showSyringe ? 44 : 45;
        RenderSystem.setShaderTexture(0, icon);
        blit(ms, this.leftPos + 23, this.topPos + yOffset, 0, 0, 16, 16, 16, 16);

        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, nitrogenPercentage(x, y, z), 6, 8, -12829636);
        this.font.draw(poseStack,

                BlockstartinfoProcedure.execute(world, x, y, z), 6, 20, -12829636);
        if (IfBlockisfullProcedure.execute(world, x, y, z))
            this.font.draw(poseStack, new TranslatableComponent("gui.changed_addon.catalyzergui.label_full"), 151, 65, -12829636);
        this.font.draw(poseStack,

                RecipeProgressProcedure.execute(world, x, y, z), 90, 34, -12829636);
    }

    protected String nitrogenPercentage(double x, double y, double z) {
        BlockEntity blockEntity = world.getBlockEntity(new BlockPos(x, y, z));

        return "Nitrogen % = " + (blockEntity == null ? 0 : blockEntity.getTileData().getDouble("nitrogen_power")) + "%";
    }
}
