package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SprayItem extends Item {

    protected final LatexType latexType;

    public SprayItem(LatexType latexType) {
        super(new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).durability(64).rarity(Rarity.COMMON));
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
        if (bs.hasProperty(AbstractLatexBlock.COVERED) && bs.getValue(AbstractLatexBlock.COVERED) != latexType)
            level.setBlockAndUpdate(pos, bs.setValue(AbstractLatexBlock.COVERED, latexType));

        for(Direction dir : Direction.values()){
            pos.set(origin).relative(dir);
            bs = level.getBlockState(pos);
            if (bs.hasProperty(AbstractLatexBlock.COVERED) && bs.getValue(AbstractLatexBlock.COVERED) != latexType)
                level.setBlockAndUpdate(pos, bs.setValue(AbstractLatexBlock.COVERED, latexType));
        }

        if(!player.isCreative() && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) == 0){
            if (stack.hurt(1, player.getRandom(), player instanceof ServerPlayer sPlayer ? sPlayer : null)) {
                stack.shrink(1);
                stack.setDamageValue(0);
            }
        }

        level.playSound(null, player, ChangedAddonSoundEvents.SPRAY_SOUND, SoundSource.PLAYERS, 1, 1);

        return InteractionResult.SUCCESS;
    }
}
