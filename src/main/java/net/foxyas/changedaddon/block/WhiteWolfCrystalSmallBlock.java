package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.ltxprogrammer.changed.block.DarkLatexBlock;
import net.ltxprogrammer.changed.block.WolfCrystalBlock;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class WhiteWolfCrystalSmallBlock extends AbstractWolfCrystalExtender.AbstractWolfCrystalSmall {

    public WhiteWolfCrystalSmallBlock() {
        super(ChangedAddonItems.WHITE_WOLF_CRYSTAL_FRAGMENT);
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return blockState.getBlock() == ChangedAddonBlocks.WHITE_WOLF_CRYSTAL_BLOCK.get()
                || blockState.getBlock() instanceof WolfCrystalBlock
                || blockState.getBlock() instanceof DarkLatexBlock
                || LatexCoverState.getAt(level, blockPos).getType() == ChangedLatexTypes.DARK_LATEX.get();
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        BlockState blockStateOn = level.getBlockState(blockPos.below());
        if (!canSupportRigidBlock(level, blockPos.below()))
            return false;
        return blockStateOn.getBlock() == ChangedAddonBlocks.WHITE_WOLF_CRYSTAL_BLOCK.get()
                || blockStateOn.getBlock() instanceof WolfCrystalBlock
                || blockStateOn.getBlock() instanceof DarkLatexBlock
                || LatexCoverState.getAt(level, blockPos).getType() == ChangedLatexTypes.DARK_LATEX.get();
    }
}
