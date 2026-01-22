package net.foxyas.changedaddon.client.gui;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.event.ClientMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

public class ChangedAdditionsModConflictWarningScreen extends Screen {

    public ChangedAdditionsModConflictWarningScreen() {
        super(Component.literal("⚠ MOD CONFLICT WARNING"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("I understand, continue"),
                        btn -> this.onClose()
                ).bounds(centerX - 100, centerY + 20, 200, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Quit Game"),
                        btn -> Minecraft.getInstance().stop()
                ).bounds(centerX - 100, centerY + 50, 200, 20).build()
        );
    }

    @Override
    public void onClose() {
        ClientMod.changedAdditionsWarningScreenShowed = true;
        super.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);


        MutableComponent warningText = Component.literal(
                "Changed Additions detected.\n" +
                        "This mod should not be used together with " + ChangedAddonMod.MODID + ".\n" +
                        "Both mods implement overlapping systems."
        );

        int textWidth = 320;
        int y = this.height / 2 - 60;

        for (FormattedCharSequence line : this.font.split(warningText, textWidth)) {
            guiGraphics.drawCenteredString(
                    this.font,
                    line,
                    this.width / 2,
                    y,
                    0xFF5555
            );
            y += 10;
        }

        guiGraphics.drawCenteredString(
                this.font,
                Component.literal("Expect incompatibility, broken recipe, or small corruption."),
                this.width / 2,
                this.height / 2 - 25,
                0xFFFFFF
        );

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
