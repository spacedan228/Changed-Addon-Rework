package net.foxyas.changedaddon.mixins.items;

import net.foxyas.changedaddon.variants.VariantExtraStats;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IForgeItemStack {
    @Shadow public abstract Item getItem();

    @Override
    public boolean canElytraFly(LivingEntity entity) {
        ItemStack self = (ItemStack)(IForgeItemStack)this;
        boolean variantCanFly = ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(entity))
                .map(latexVariant -> {
                    if (latexVariant.getParent().canGlide && latexVariant.getChangedEntity() instanceof VariantExtraStats variantExtraStats) {
                        return variantExtraStats.getFlyType().canGlide();
                    }
                    return latexVariant.getParent().canGlide;
                }).orElse(false);
        return variantCanFly || this.getItem().canElytraFly(self, entity);
    }
}
