package net.foxyas.changedaddon.block.entity;

import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DarkLatexWolfPlushyBlockEntity extends AbstractPlushyBlockEntity {

    public DarkLatexWolfPlushyBlockEntity(BlockPos position, BlockState state) {
        super(ChangedAddonBlockEntities.DARK_LATEX_WOLF_PLUSHY.get(), position, state);
    }
}
