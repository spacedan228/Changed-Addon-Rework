package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.core.BlockPos;
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

public class WhiteLatexSprayItem extends Item {

    public WhiteLatexSprayItem() {
        super(new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).durability(64).rarity(Rarity.COMMON));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemstack) {
        return UseAnim.BLOCK;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();
        if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) == 0 && !player.isCreative()){
            if (stack.hurt(1, player.getRandom(), null)) {
                stack.shrink(1);
                stack.setDamageValue(0);
                return InteractionResult.PASS;
            }
        }

        Level level = player.level;
        level.playSound(null, player, ChangedAddonSoundEvents.SPRAY_SOUND, SoundSource.PLAYERS, 1, 1);

        int horizontalRadiusSphere = 2 - 1;
        int verticalRadiusSphere = 2 - 1;
        int yIterationsSphere = verticalRadiusSphere;
        BlockPos clickedPos = context.getClickedPos();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int i = -yIterationsSphere; i <= yIterationsSphere; i++) {
            for (int xi = -horizontalRadiusSphere; xi <= horizontalRadiusSphere; xi++) {
                for (int zi = -horizontalRadiusSphere; zi <= horizontalRadiusSphere; zi++) {
                    double distanceSq = (xi * xi) / (double) (horizontalRadiusSphere * horizontalRadiusSphere) + (i * i) / (double) (verticalRadiusSphere * verticalRadiusSphere)
                            + (zi * zi) / (double) (horizontalRadiusSphere * horizontalRadiusSphere);
                    if (distanceSq > 1.0) continue;

                    mutable.set(clickedPos.getX() + xi, clickedPos.getY() + i, clickedPos.getZ() + zi);
                    BlockState state = level.getBlockState(mutable);
                    if(!state.hasProperty(AbstractLatexBlock.COVERED) || state.getValue(AbstractLatexBlock.COVERED) == LatexType.WHITE_LATEX) continue;
                    level.setBlockAndUpdate(mutable, state.setValue(AbstractLatexBlock.COVERED, LatexType.WHITE_LATEX));
                }
            }
        }

        return InteractionResult.SUCCESS;
    }
}
