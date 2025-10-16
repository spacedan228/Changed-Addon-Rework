package net.foxyas.changedaddon.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockstartinfoProcedure {

    public static String execute(Level level, double x, double y, double z) {
        BlockPos pos = new BlockPos(x, y, z);
        String block = new TranslatableComponent("block." + ForgeRegistries.BLOCKS.getKey((level.getBlockState(pos)).getBlock()).toString().replace(":", ".")).getString();

        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity == null || !blockEntity.getTileData().getBoolean("start_recipe") ? block + " is deactivated" : block + " is activated";
    }
}
