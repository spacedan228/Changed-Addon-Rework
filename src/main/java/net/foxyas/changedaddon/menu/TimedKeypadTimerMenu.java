package net.foxyas.changedaddon.menu;

import net.foxyas.changedaddon.block.advanced.TimedKeypadBlockEntity;
import net.foxyas.changedaddon.block.entity.InformantBlockEntity;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimedKeypadTimerMenu extends AbstractContainerMenu {

    public final Level level;
    public final Player player;
    public final TimedKeypadBlockEntity blockEntity;

    public TimedKeypadTimerMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, extraData.readBlockPos());
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
        BlockPos pos = blockEntity.getBlockPos();
        return level.getBlockState(blockEntity.getBlockPos()).is(blockEntity.getBlockState().getBlock())
                && player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) <= (double) 64.0F;
    }
}
