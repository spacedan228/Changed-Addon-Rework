package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlueWolfCrystalBlockBlock extends AbstractWolfCrystalExtender.AbstractWolfCrystalBlock {

    public BlueWolfCrystalBlockBlock() {
        super();
    }

    @Override
    public boolean canSustainPlant(@NotNull BlockState state, @NotNull BlockGetter world, BlockPos pos, @NotNull Direction facing, net.minecraftforge.common.IPlantable plantable) {
        BlockState plant = plantable.getPlant(world, pos.relative(facing));
        if (plant.getBlock() instanceof BlueWolfCrystalSmallBlock)
            return true;
        else
            return super.canSustainPlant(state, world, pos, facing, plantable);
    }

    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);
        BlockPos above = pos.above();
        if (level.getBlockState(above).is(Blocks.AIR)) {
            level.setBlock(above, ChangedAddonBlocks.BLUE_WOLF_CRYSTAL_SMALL.get().defaultBlockState(), 3);
            level.playSound(null, pos, ChangedSounds.CRYSTAL_EXTEND.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }
}
