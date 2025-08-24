package net.foxyas.changedaddon.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class WallWhiteStairBlock extends StairBlock {
    public WallWhiteStairBlock() {
        super(() -> Blocks.AIR.defaultBlockState(), BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ).sound(SoundType.STONE).strength(1.5f, 6.5f).requiresCorrectToolForDrops().dynamicShape());
    }

    @Override
    public float getExplosionResistance() {
        return 6.5f;
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState p_56947_) {
        return false;
    }

    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        return 0;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        if (player.getInventory().getSelected().getItem() instanceof PickaxeItem tieredItem)
            return TierSortingRegistry.isCorrectTierForDrops(Tiers.WOOD, state) || tieredItem.getTier().getLevel() >= 0;
        return super.canHarvestBlock(state, world, pos, player);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootContext.@NotNull Builder builder) {
        List<ItemStack> dropsOriginal = super.getDrops(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }
}
