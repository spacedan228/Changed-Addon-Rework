package net.foxyas.changedaddon.block.entity;

import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DarkLatexWolfPlushBlockEntity extends AbstractPlushBlockEntity {

    public DarkLatexWolfPlushBlockEntity(BlockPos position, BlockState state) {
        super(ChangedAddonBlockEntities.DARK_LATEX_WOLF_PLUSH.get(), position, state);
    }
}
