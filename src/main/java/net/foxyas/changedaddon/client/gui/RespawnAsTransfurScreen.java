package net.foxyas.changedaddon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.network.packet.RespawnAsTransfur;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class RespawnAsTransfurScreen extends Screen {
    private static final ResourceLocation TEXTURE = ChangedAddonMod.resourceLoc("textures/gui/infected_spawn.png");

    private final DeathScreen previousDeathScreen;

    public RespawnAsTransfurScreen(DeathScreen previousDeathScreen) {
        super(new TextComponent("Spawn as Transfured"));
        this.previousDeathScreen = previousDeathScreen;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(new Button(centerX - 75, centerY - 10, 150, 20,
                new TextComponent("Spawn as Infected"),
                btn -> {
                    // Enviar pacote para o servidor
                    // PacketHandler.INSTANCE.sendToServer(new SpawnInfectedPacket());
                    assert this.minecraft != null;
                    this.minecraft.setScreen(null);
                }));

        this.addRenderableWidget(new Button(centerX - 75, centerY + 20, 150, 20,
                new TextComponent("Cancel"),
                btn -> {
                    assert this.minecraft != null;
                    this.minecraft.setScreen(previousDeathScreen);
                }));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, (this.width - 176) / 2, (this.height - 166) / 2,
                0, 0, 176, 166);

        drawCenteredString(poseStack, this.font, "Choose Your Fate", this.width / 2, 20, 0xFFFFFF);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void handleRespawnAsTransfur() {


        Player player = Minecraft.getInstance().player;
        if (player != null) {
            // executa um respawn direto pelo client (geralmente envia pro servidor)
            // player.closeContainer();
            ChangedAddonMod.PACKET_HANDLER.sendToServer(new RespawnAsTransfur(player.getId(), TransfurVariant.getPublicTransfurVariants().toList()));
        }

        // fecha todas as telas
        Minecraft.getInstance().setScreen(null);
    }
}
