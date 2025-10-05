package net.foxyas.changedaddon.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class UnifuserBlockAddedProcedure {

    public static void execute(Level level, BlockPos pos, BlockState state) {
        if(level.isClientSide()) return;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity == null) return;

        blockEntity.getTileData().putBoolean("start_recipe", true);
        level.sendBlockUpdated(pos, state, state, 3);
    }
}
