package net.foxyas.changedaddon.mixins.client;

import net.foxyas.changedaddon.init.ChangedAddonGameRules;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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
    private void addTFButton(CallbackInfo ci){
        if(!minecraft.player.level.getGameRules().getBoolean(ChangedAddonGameRules.RESPAWN_AS_RANDOM_TF)) return;
        
        exitButtons.add(this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, hardcore ? new TranslatableComponent("deathScreen.select_tf.hardcore") : new TranslatableComponent("deathScreen.select_tf"), (button) -> {
            minecraft.player.respawn();
            //TODO tf
            minecraft.setScreen(null);
        })));
    }
}
