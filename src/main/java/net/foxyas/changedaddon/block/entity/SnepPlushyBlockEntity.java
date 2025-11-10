package net.foxyas.changedaddon.block.entity;

import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SnepPlushyBlockEntity extends AbstractPlushyBlockEntity {

    private static final String GLOWING_TAG = "glowingEyes";
    public boolean glowingEyes = false;

    public SnepPlushyBlockEntity(BlockPos position, BlockState state) {
        super(ChangedAddonBlockEntities.SNEP_PLUSHY.get(), position, state);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        if (compound.contains(GLOWING_TAG)) this.glowingEyes = compound.getBoolean(GLOWING_TAG);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean(GLOWING_TAG, this.glowingEyes);
    }
}
