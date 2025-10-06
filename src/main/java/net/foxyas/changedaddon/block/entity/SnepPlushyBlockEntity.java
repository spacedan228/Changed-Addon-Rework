package net.foxyas.changedaddon.block.entity;

import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SnepPlushyBlockEntity extends BlockEntity {
    private static final String SQUEEZED_TAG = "squeezedTicks";
    private static final String GLOWING_TAG = "glowingEyes";
    public int squeezedTicks;
    public boolean glowingEyes = false;

    public SnepPlushyBlockEntity(BlockPos position, BlockState state) {
        super(ChangedAddonBlockEntities.SNEP_PLUSHY.get(), position, state);
        this.squeezedTicks = 0;
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        if (compound.contains(SQUEEZED_TAG)) this.squeezedTicks = compound.getInt(SQUEEZED_TAG);
        if (compound.contains(GLOWING_TAG)) this.glowingEyes = compound.getBoolean(GLOWING_TAG);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt(SQUEEZED_TAG, this.squeezedTicks);
        compound.putBoolean(GLOWING_TAG, this.glowingEyes);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    public boolean isSqueezed() {
        return this.squeezedTicks > 0;
    }

    public void subSqueezedTicks(int i) {
        this.squeezedTicks = this.squeezedTicks - i;
    }
}
