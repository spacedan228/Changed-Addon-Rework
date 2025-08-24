package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.foxyas.changedaddon.procedures.SyringeWithLitixCammoniaAttack;
import net.foxyas.changedaddon.procedures.SyringeWithLitixCammoniaPlayerFinishesUsingItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SyringeWithLitixCammoniaItem extends AbstractSyringeItem {
    public SyringeWithLitixCammoniaItem() {
        super(new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).durability(2)
                .rarity(Rarity.UNCOMMON)
        );
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull LivingEntity entity) {
        ItemStack retval = super.finishUsingItem(itemstack, world, entity);
        SyringeWithLitixCammoniaPlayerFinishesUsingItem.run(world, entity);
        return retval;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack itemstack, @NotNull LivingEntity entity, @NotNull LivingEntity sourceentity) {
        boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        SyringeWithLitixCammoniaAttack.run(entity, sourceentity, itemstack);
        return retval;
    }
}
