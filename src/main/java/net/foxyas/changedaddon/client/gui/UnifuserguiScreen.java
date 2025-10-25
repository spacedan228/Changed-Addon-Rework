package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.block.entity.CatalyzerBlockEntity;
import net.foxyas.changedaddon.block.entity.UnifuserBlockEntity;
import net.foxyas.changedaddon.world.inventory.UnifuserGuiMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class UnifuserGuiScreen extends AbstractContainerScreen<UnifuserGuiMenu> {

    private final UnifuserGuiMenu menu;
    private final Level world;
    private final int x, y, z;
    private final BlockPos containerPos;

    public UnifuserGuiScreen(UnifuserGuiMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        menu = container;
        this.world = container.world;
        this.containerPos = container.getBlockPos();
        this.x = containerPos.getX();
        this.y = containerPos.getY();
        this.z = containerPos.getZ();
        this.imageWidth = 200;
        this.imageHeight = 187;
    }

    @Override
    public void render(@NotNull PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
        if (menu.isSlotEmpty(0))
            if (mouseX > leftPos + 10 && mouseX < leftPos + 34 && mouseY > topPos + 41 && mouseY < topPos + 65)
                this.renderTooltip(ms, new TranslatableComponent("gui.changed_addon.unifuser_gui.tooltip_place_the_powders"), mouseX, mouseY);
        if (menu.isSlotEmpty(2))
            if (mouseX > leftPos + 45 && mouseX < leftPos + 69 && mouseY > topPos + 53 && mouseY < topPos + 77)
                this.renderTooltip(ms, new TranslatableComponent("gui.changed_addon.unifuser_gui.tooltip_place_a_syringe_with_dna"), mouseX, mouseY);
        if (menu.isSlotEmpty(3))
            if (mouseX > leftPos + 10 && mouseX < leftPos + 34 && mouseY > topPos + 65 && mouseY < topPos + 89)
                this.renderTooltip(ms, new TranslatableComponent("gui.changed_addon.unifuser_gui.tooltip_put_the_second_ingredient"), mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderTexture(0, ResourceLocation.parse("changed_addon:textures/screens/unifusergui_new.png"));
        blit(ms, this.leftPos, this.topPos, 0, 0, 200, 187, 200, 187);

        RenderSystem.setShaderTexture(0, ResourceLocation.parse("changed_addon:textures/screens/empty_bar.png"));
        blit(ms, this.leftPos + 84, this.topPos + 59, 0, 0, 32, 12, 32, 12);

        BlockEntity be = world.getBlockEntity(new BlockPos(x, y, z));
        if (be instanceof UnifuserBlockEntity unifuserBlockEntity) {
            int progressInt = (int) (unifuserBlockEntity.recipeProgress / 3.57);

            RenderSystem.setShaderTexture(0, ResourceLocation.parse("changed_addon:textures/screens/bar_full.png"));
            blit(ms, this.leftPos + 84 + 2, this.topPos + 59 + 2, 0, 0, progressInt, 8, progressInt, 8);
        }


        RenderSystem.setShaderTexture(0, ResourceLocation.parse("changed_addon:textures/screens/dusts.png"));
        blit(ms, this.leftPos + 15, this.topPos + 46, 0, 0, 16, 16, 16, 16);

        RenderSystem.setShaderTexture(0, ResourceLocation.parse("changed_addon:textures/screens/syringe_withlitixcamonia_screen.png"));
        blit(ms, this.leftPos + 50, this.topPos + 57, 0, 0, 16, 16, 16, 16);

        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, getMachineState(world, x, y, z), 9, 10, -12829636);
        if (isBlockFull())
            this.font.draw(poseStack, new TranslatableComponent("gui.changed_addon.unifuser_gui.label_full"), 153, 78, -12829636);
        this.font.draw(poseStack, getRecipeState(world, x, y, z), 89, 47, -12829636);
    }

    private boolean isBlockFull() {
        return world.getBlockEntity(containerPos) instanceof UnifuserBlockEntity unifuserBlockEntity && unifuserBlockEntity.isSlotFull(3);
    }

    public static String getMachineState(Level level, double x, double y, double z) {
        BlockPos pos = new BlockPos(x, y, z);
        String block = new TranslatableComponent("block." + ForgeRegistries.BLOCKS.getKey((level.getBlockState(pos)).getBlock()).toString().replace(":", ".")).getString();

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof UnifuserBlockEntity unifuserBlockEntity) {
            return !unifuserBlockEntity.startRecipe ? block + " is deactivated" : block + " is activated";
        } else if (blockEntity instanceof CatalyzerBlockEntity catalyzerBlockEntity) {
            return !catalyzerBlockEntity.startRecipe ? block + " is deactivated" : block + " is activated";
        }
        return "Something ODD Happen..";
    }

    public static String getRecipeState(LevelAccessor level, double x, double y, double z) {
        BlockEntity blockEntity = level.getBlockEntity(new BlockPos(x, y, z));
        if(blockEntity == null) return "THIS SHOULD NEVER HAPPEN";
        double number = 0;
        if (blockEntity instanceof UnifuserBlockEntity unifuserBlockEntity) {
            number = unifuserBlockEntity.recipeProgress;
        } else if (blockEntity instanceof CatalyzerBlockEntity catalyzerBlockEntity) {
            number = catalyzerBlockEntity.recipeProgress;
        }

        return Math.round(number) + "%";
    }
}
