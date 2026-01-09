package net.foxyas.changedaddon.mixins.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {


    @WrapOperation(
            method = "renderEntityInInventory(IIIFFLnet/minecraft/world/entity/LivingEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"
            )
    )
    private static void changedaddon$modifyPoseStack(PoseStack instance, Quaternion pQuaternion, Operation<Void> original, @Local(argsOnly = true) LivingEntity livingEntity) {
        original.call(instance, pQuaternion);
        if (resolveChangedEntity(livingEntity) instanceof IAlphaAbleEntity iAlphaAbleEntity) {
            float reduction = (1 / iAlphaAbleEntity.alphaScaleForRender());
            instance.scale(reduction, reduction, reduction);
        }
    }

    private static Entity resolveChangedEntity(Entity entity) {
        if (entity instanceof Player player) {
            TransfurVariantInstance<?> transfur = ProcessTransfur.getPlayerTransfurVariant(player);
            if (transfur != null) {
                return transfur.getChangedEntity();
            }
        }
        return entity;
    }
}
