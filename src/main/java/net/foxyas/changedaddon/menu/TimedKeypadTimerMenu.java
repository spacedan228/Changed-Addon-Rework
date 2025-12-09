package net.foxyas.changedaddon.menu;

import net.foxyas.changedaddon.block.advanced.TimedKeypadBlockEntity;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimedKeypadTimerMenu extends AbstractContainerMenu {

    public final Level level;
    public final Player player;
    @Nullable
    public final TimedKeypadBlockEntity blockEntity;

    public TimedKeypadTimerMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, extraData == null ? BlockPos.ZERO : extraData.readBlockPos());
    }

    public TimedKeypadTimerMenu(int id, Inventory inv, BlockPos pos) {
        super(ChangedAddonMenus.TIMED_KEYPAD_TIMER.get(), id);
        this.player = inv.player;
        this.level = inv.player.level;
        blockEntity = (TimedKeypadBlockEntity) level.getBlockEntity(pos);
    }


    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (blockEntity != null) {
            BlockPos pos = blockEntity.getBlockPos();
            return level.getBlockState(blockEntity.getBlockPos()).is(blockEntity.getBlockState().getBlock())
                    && player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) <= (double) 64.0F;
        }
        return false;
    }
}
