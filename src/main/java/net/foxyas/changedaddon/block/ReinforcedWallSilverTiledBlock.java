package net.foxyas.changedaddon.block;

import static net.foxyas.changedaddon.block.interfaces.ConditionalLatexCoverableBlock.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;

public class ReinforcedWallSilverTiledBlock extends Block implements NonLatexCoverableBlock {

    public ReinforcedWallSilverTiledBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.NETHERITE_BLOCK).strength(6f, 30f));
    }

    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        return 15;
    }
}
