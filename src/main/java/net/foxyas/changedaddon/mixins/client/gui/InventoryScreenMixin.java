package net.foxyas.changedaddon.mixins.client.gui;

import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(method = "renderEntityInInventory", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;runAsFancy(Ljava/lang/Runnable;)V", shift = At.Shift.BY))
    private static void scaleDownHook(GuiGraphics pGuiGraphics, int pX, int pY, int pScale, Quaternionf pPose, Quaternionf pCameraOrientation, LivingEntity pEntity, CallbackInfo ci) {
        if (pEntity instanceof IAlphaAbleEntity iAlphaAbleEntity && iAlphaAbleEntity.isAlpha()) {
            float reduction = (1 / iAlphaAbleEntity.alphaScaleForRender());
            pGuiGraphics.pose().scale(reduction, reduction, reduction);
        }
    }
}
