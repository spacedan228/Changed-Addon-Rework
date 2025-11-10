package net.foxyas.changedaddon.block.entity;

import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WolfPlushyBlockEntity extends AbstractPlushyBlockEntity {

    public WolfPlushyBlockEntity(BlockPos position, BlockState state) {
        super(ChangedAddonBlockEntities.WOLF_PLUSHY.get(), position, state);
    }
}
