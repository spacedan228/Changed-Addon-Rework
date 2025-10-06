package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.foxyas.changedaddon.procedures.SignalBlockFeatureProcedure;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SignalCatcherItem extends Item {

    public SignalCatcherItem() {
        super(new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemstack) {
        return 15;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player entity, @NotNull InteractionHand hand) {
        InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        entity.startUsingItem(hand);
        return ar;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull LivingEntity entity) {
        ItemStack retval = super.finishUsingItem(itemstack, world, entity);

        SignalBlockFeatureProcedure.execute(world, entity, itemstack);
        return retval;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull LivingEntity entity, int time) {
        if (!itemstack.getOrCreateTag().getBoolean("set")) {
            if (entity instanceof Player player && !player.level.isClientSide())
                player.displayClientMessage(new TextComponent("§o§bNo Location Found §l[Not Close Enough]"), false);
        }
    }
}
