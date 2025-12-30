package net.foxyas.changedaddon.mixins.entity.elytraFly;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.foxyas.changedaddon.variant.VariantExtraStats;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Player.class, priority = 1001)
public class PlayerMixin {

    @ModifyExpressionValue(
            method = "tryToStartFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z",
                    remap = false
            )
    )
    private boolean changedaddon$canElytraFlyRedirect(
            boolean original
    ) {
        LivingEntity self = (LivingEntity) (Object) this;
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(self))
                .map(latexVariant -> {
                    if (latexVariant.getChangedEntity() instanceof VariantExtraStats extra) {
                        return extra.getFlyType().canGlide() || original;
                    }
                    return latexVariant.getParent().canGlide || original;
                })
                .orElse(original);
    }
}
