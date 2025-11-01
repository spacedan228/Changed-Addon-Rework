package net.foxyas.changedaddon.block;

import net.ltxprogrammer.changed.block.NonLatexCoverableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;

public class ReinforcedCrossBlock extends Block implements NonLatexCoverableBlock {
    public ReinforcedCrossBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.NETHERITE_BLOCK).strength(20f, 30f).requiresCorrectToolForDrops());
    }

    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        return 15;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        var e = super.canHarvestBlock(state,world,pos, player);
        if (player.getInventory().getSelected().getItem() instanceof PickaxeItem tieredItem)
            return TierSortingRegistry.isCorrectTierForDrops(Tiers.NETHERITE, state) || tieredItem.getTier().getLevel() >= Tiers.NETHERITE.getLevel();
        return super.canHarvestBlock(state, world, pos, player);
    }
}
