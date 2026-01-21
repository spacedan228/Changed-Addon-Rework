package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.event.ClientMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class ChangedAdditionsModConflictWarningScreen extends Screen {

    public ChangedAdditionsModConflictWarningScreen() {
        super(new TextComponent("⚠ MOD CONFLICT WARNING"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(
                new Button(
                        centerX - 100,
                        centerY + 20,
                        200,
                        20,
                        new TextComponent("I understand, continue"),
                        btn -> this.onClose()
                )
        );

        this.addRenderableWidget(
                new Button(
                        centerX - 100,
                        centerY + 50,
                        200,
                        20,
                        new TextComponent("Quit Game"),
                        btn -> Minecraft.getInstance().stop()
                )
        );
    }

    @Override
    public void onClose() {
        ClientMod.changedAdditionsWarningScreenShowed = true;
        super.onClose();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        MutableComponent warningText = new TextComponent(
                "Changed Additions detected.\n" +
                        "This mod should not be used together with " + ChangedAddonMod.MODID + ".\n" +
                        "Both mods implement overlapping systems."
        );

        int textWidth = 320;
        int y = this.height / 2 - 60;

        for (var line : this.font.split(warningText, textWidth)) {
            drawCenteredString(
                    poseStack,
                    this.font,
                    line,
                    this.width / 2,
                    y,
                    0xFF5555
            );
            y += 10;
        }

        drawCenteredString(
                poseStack,
                this.font,
                "Expect incompatibility, broken recipe, or small corruption.",
                this.width / 2,
                this.height / 2 - 25,
                0xFFFFFF
        );

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
