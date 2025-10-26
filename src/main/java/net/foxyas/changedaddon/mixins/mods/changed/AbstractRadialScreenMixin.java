package net.foxyas.changedaddon.mixins.mods.changed;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.ltxprogrammer.changed.client.gui.AbstractRadialScreen;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(value = AbstractRadialScreen.class, remap = false)
public abstract class AbstractRadialScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public AbstractRadialScreenMixin(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Shadow
    public abstract Optional<Integer> getSectionAt(int mouseX, int mouseY);

    @Shadow
    public abstract boolean handleClicked(int i, SingleRunnable singleRunnable);

    @Shadow
    @Nullable
    public abstract List<Component> tooltipsFor(int i);

    @Inject(method = "mouseClicked", at = @At("HEAD"), remap = true, cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (ChangedAddonServerConfiguration.ALLOW_SECOND_ABILITY_USE.get()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                Optional<Integer> section = this.getSectionAt((int) mouseX, (int) mouseY);
                if (section.isPresent()) {
                    assert this.minecraft != null;
                    LocalPlayer localPlayer = this.minecraft.player;
                    Objects.requireNonNull(localPlayer);
                    SingleRunnable single = new SingleRunnable(localPlayer::closeContainer);
                    if (this.handleClicked(section.get(), single)) {
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        single.run();
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}
