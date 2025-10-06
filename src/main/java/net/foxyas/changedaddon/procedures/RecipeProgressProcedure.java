package net.foxyas.changedaddon.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RecipeProgressProcedure {

    public static String execute(LevelAccessor level, double x, double y, double z) {
        BlockEntity blockEntity = level.getBlockEntity(new BlockPos(x, y, z));
        if(blockEntity == null) return "THIS SHOULD NEVER HAPPEN";

        return Math.round(blockEntity.getTileData().getDouble("recipe_progress")) + "%";
    }
}
