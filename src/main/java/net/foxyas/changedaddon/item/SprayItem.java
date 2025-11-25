package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SprayItem extends Item {

    protected final LatexType latexType;

    public SprayItem(LatexType latexType) {
        super(new Item.Properties()
                //.tab(ChangedAddonTabs.CHANGED_ADDON_MAIN_TAB).durability(64).rarity(Rarity.COMMON)
                );
        this.latexType = latexType;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();
        player.getCooldowns().addCooldown(stack.getItem(), 20);

        BlockPos origin = context.getClickedPos();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        Level level = player.level;
        pos.set(origin);
        BlockState bs = level.getBlockState(pos);
        if (LatexCoverState.getAt(level, pos).getType() != latexType)
            LatexCoverState.setAtAndUpdate(level,pos, latexType.defaultCoverState());

        for(Direction dir : Direction.values()){
            pos.set(origin).move(dir);
            bs = level.getBlockState(pos);
            if (LatexCoverState.getAt(level, pos).getType() != latexType)
                LatexCoverState.setAtAndUpdate(level,pos, latexType.defaultCoverState());
        }

        if(!player.isCreative() && EnchantmentHelper.getTagEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) == 0){
            if (stack.hurt(1, player.getRandom(), player instanceof ServerPlayer sPlayer ? sPlayer : null)) {
                stack.shrink(1);
                stack.setDamageValue(0);
            }
        }

        level.playSound(null, player, ChangedAddonSoundEvents.SPRAY_SOUND.get(), SoundSource.PLAYERS, 1, 1);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag pIsAdvanced) {
        tooltip.add(Component.literal(stack.getMaxDamage() - stack.getDamageValue() + "/" + stack.getMaxDamage() + " Uses"));
    }

    @Mod.EventBusSubscriber
    public static class OnBreak {

        @SubscribeEvent
        public static void onBreak(PlayerDestroyItemEvent event){
            Player player = event.getEntity();
            if (player == null) return;

            ItemStack itemstack = event.getOriginal();
            if (itemstack.is(ChangedAddonItems.LITIX_CAMONIA_SPRAY.get())
                    || itemstack.is(ChangedAddonItems.WHITE_LATEX_SPRAY.get())
                    || itemstack.is(ChangedAddonItems.DARK_LATEX_SPRAY.get())) {
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ChangedAddonItems.EMPTY_SPRAY.get()));
            }
        }
    }
}
