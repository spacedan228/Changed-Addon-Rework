package net.foxyas.changedaddon.mixins.client;

import net.foxyas.changedaddon.client.gui.RespawnAsTransfurScreen;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    @Shadow
    @Final
    private List<Button> exitButtons;

    @Shadow
    @Final
    private boolean hardcore;

    protected DeathScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", shift = At.Shift.AFTER), method = "init")
    private void addTFButton(CallbackInfo ci) {
        assert minecraft != null;
        LocalPlayer player = this.minecraft.player;
        assert player != null;
        boolean config = ChangedAddonServerConfiguration.ALLOW_RESPAWN_AS_TRANSFUR.get();
        if (!config) return;
        if (ProcessTransfur.isPlayerTransfurred(player)) return;

        exitButtons.add(this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, hardcore ? new TranslatableComponent("deathScreen.select_tf.hardcore") : new TranslatableComponent("deathScreen.select_tf"), (button) -> {
            RespawnAsTransfurScreen respawnAsTransfurScreen = new RespawnAsTransfurScreen((DeathScreen) (Object) this);
            if (player.hasPermissions(2) || ChangedAddonServerConfiguration.ALLOW_PLAYERS_TO_SELECT_RESPAWN_TRANSFUR.get()) {
                this.minecraft.setScreen(respawnAsTransfurScreen);
            } else {
                ConfirmScreen confirmscreen = new ConfirmScreen(respawnAsTransfurScreen::handleRespawnAsTransfur,
                        new TranslatableComponent("deathScreen.select_tf.confirm"),
                        ChangedAddonServerConfiguration.APPLY_UNTRANSFUR_IMMUNITY_AFTER_RESPAWN_AS_TRANSFUR.get() ?
                                new TranslatableComponent("deathScreen.select_tf.confirm.info") :
                                TextComponent.EMPTY,
                        new TranslatableComponent("deathScreen.select_tf.spawn_as_infected"),
                        new TranslatableComponent("deathScreen.select_tf.cancel_spawn_as_infected")
                );
                this.minecraft.setScreen(confirmscreen);
                confirmscreen.setDelay(20);
            }
        })));
    }
}
