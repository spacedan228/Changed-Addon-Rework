package net.foxyas.changedaddon.block.advanced;

import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.foxyas.changedaddon.menu.TimedKeypadTimerMenu;
import net.foxyas.changedaddon.util.ComponentUtil;
import net.ltxprogrammer.changed.block.KeypadBlock;
import net.ltxprogrammer.changed.block.entity.KeypadBlockEntity;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TimedKeypadBlockEntity extends KeypadBlockEntity {

    public static final int MAX_TIMER = 9999;
    private int timer = 0;
    private int lastTimerSet = timer;
    private boolean canTick = false;
    private int ticks = 0;

    public TimedKeypadBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return ChangedAddonBlockEntities.TIMED_KEYPAD_BLOCK_ENTITY.get();
    }

    public void setCanTick(boolean canTick) {
        this.canTick = canTick;
        this.lastTimerSet = timer;
    }

    public boolean canTick() {
        return canTick;
    }

    public void addTimer(int timer) {
        this.timer = Math.max(0, Math.min(this.timer + timer, MAX_TIMER));
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = Math.max(0, Math.min(timer, MAX_TIMER));
        this.lastTimerSet = timer;
    }

    public int getLastTimerSet() {
        return lastTimerSet;
    }

    public void setLastTimerSet(int lastTimerSet) {
        this.lastTimerSet = lastTimerSet;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("timer", timer);
        tag.putInt("lastTimerSet", lastTimerSet);
        tag.putBoolean("canTick", canTick);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("timer")) timer = tag.getInt("timer");
        if (tag.contains("lastTimerSet")) lastTimerSet = tag.getInt("lastTimerSet");
        if (tag.contains("canTick")) canTick = tag.getBoolean("canTick");
    }

    private void playSound(RegistryObject<SoundEvent> event, float volume, float pitch) {
        if (this.level != null && this.level.getServer() != null) {
            if (this.level instanceof ServerLevel serverLevel) {
                ChangedSounds.broadcastSound(serverLevel, event, this.worldPosition, volume, pitch);
            }
        }

    }

    public void playUnlockSuccess() {
        this.playSound(ChangedSounds.KEYPAD_UNLOCK_SUCCESS, 1.0F, 1.0F);
    }

    public void playUnlockFail() {
        this.playSound(ChangedSounds.KEYPAD_UNLOCK_FAIL, 1.0F, 1.0F);
    }

    public void playLock() {
        this.playSound(ChangedSounds.KEYPAD_LOCK, 1.0F, 1.0F);
    }

    public void playTimerAdjust(boolean isPositive) {
        this.playSound(ChangedSounds.KEYPAD_LOCK, 1.0F, isPositive ? 1f : 0.75f);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    public @NotNull CompoundTag getUpdatePacketTag() {
        return getUpdateTag();
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return super.createMenu(id, inv, player);
    }

    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider((id, inventory, player) -> this.getMenu(pPos, id, inventory, player), ComponentUtil.literal(""));
    }

    private AbstractContainerMenu getMenu(BlockPos pPos, int id, Inventory inventory, Player player) {
        if (player.isShiftKeyDown()) {
            return new TimedKeypadTimerMenu(id, inventory, pPos);
        } else {
            return super.createMenu(id, inventory, player);
        }
    }

    public void tick(@NotNull Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }
        if (!canTick) {
            return;
        }
        if (!this.getBlockState().getValue(KeypadBlock.POWERED)) {
            return;
        }
        ticks++;

        if (ticks % 10 == 0) {
            if (timer > 0) {
                timer--;
            } else {
                timer = lastTimerSet;
                lockKeypad(level, pos, getBlockState());
                this.playLock();
                canTick = false;
            }
        }

        if (ticks >= 4096) {
            ticks = 0;
        }
    }

    public void lockKeypad(Level level, BlockPos blockPos, BlockState state) {
        level.setBlockAndUpdate(blockPos, this.getBlockState().setValue(KeypadBlock.POWERED, Boolean.FALSE));
        level.updateNeighborsAt(blockPos, state.getBlock());
        level.updateNeighborsAt(blockPos.relative(state.getValue(KeypadBlock.FACING).getOpposite()), state.getBlock());
    }

    @Override
    public void useCode(List<Byte> attemptedCode) {
        super.useCode(attemptedCode);
        if (this.level != null && this.code != null) {
            if (getTimer() > 0) {
                this.setCanTick(true);
            }
        }
    }
}
