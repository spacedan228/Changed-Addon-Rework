package net.foxyas.changedaddon.mixins.entity.elytraFly;

import net.foxyas.changedaddon.variant.VariantExtraStats;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LivingEntity.class, priority = 1001)
public class LivingEntityMixin {

    @Redirect(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z"
            )
    )
    private boolean changedaddon$canElytraFlyRedirect(
            ItemStack stack,
            LivingEntity livingEntity
    ) {
        boolean original = stack.canElytraFly(livingEntity);
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(livingEntity))
                .map(latexVariant -> {
                    if (latexVariant.getChangedEntity() instanceof VariantExtraStats extra) {
                        return extra.getFlyType().canGlide();
                    }
                    return latexVariant.getParent().canGlide;
                })
                .orElse(original);
    }

    @Redirect(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;elytraFlightTick(Lnet/minecraft/world/entity/LivingEntity;I)Z"
            )
    )
    private boolean changedaddon$elytraFlightTickRedirect(
            ItemStack instance, LivingEntity livingEntity, int fallFlyTicks
    ) {
        boolean original = instance.elytraFlightTick(livingEntity, fallFlyTicks);
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(livingEntity))
                .map(latexVariant -> {
                    if (latexVariant.getChangedEntity() instanceof VariantExtraStats extra) {
                        return extra.getFlyType().canGlide();
                    }
                    return latexVariant.getParent().canGlide;
                })
                .orElse(original);
    }
}
