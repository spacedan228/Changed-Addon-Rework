package net.foxyas.changedaddon.mixins.items;

import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.extensions.IForgeItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class ItemMixin extends Item {

    public ItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @Shadow public abstract Block getBlock();

    @Override
    public @Nullable FoodProperties getFoodProperties() {
        if (this.getBlock().equals(ChangedBlocks.CANNED_PEACHES.get())) {
            return new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).alwaysEat().build();
        }
        return super.getFoodProperties();
    }

    @Override
    public boolean isEdible() {
        return super.isEdible();
    }
}
