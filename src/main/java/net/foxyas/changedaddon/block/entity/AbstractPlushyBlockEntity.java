package net.foxyas.changedaddon.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPlushyBlockEntity extends BlockEntity {
    private static final String SQUEEZED_TAG = "squeezedTicks";
    public int squeezedTicks;

    public AbstractPlushyBlockEntity(BlockEntityType<?> type, BlockPos position, BlockState state) {
        super(type, position, state);
        this.squeezedTicks = 0;
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        if (compound.contains(SQUEEZED_TAG)) {
            this.squeezedTicks = compound.getInt(SQUEEZED_TAG);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt(SQUEEZED_TAG, this.squeezedTicks);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }

    public boolean isSqueezed() {
        return this.squeezedTicks > 0;
    }

    public void subSqueezedTicks(int i) {
        this.squeezedTicks = this.squeezedTicks - i;
    }
}
