package net.foxyas.changedaddon.menu;

import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TransfurSoundsGuiMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public final static HashMap<String, Object> guistate = new HashMap<>();
    public final Level world;
    public final Player entity;
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    public int x, y, z;
    private ContainerLevelAccess access = ContainerLevelAccess.NULL;
    private final IItemHandler internal;
    private final boolean bound = false;
    private final Supplier<Boolean> boundItemMatcher = null;
    private final Entity boundEntity = null;
    private final BlockEntity boundBlockEntity = null;

    public TransfurSoundsGuiMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ChangedAddonMenus.TRANSFUR_SOUNDS_GUI.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level;
        this.internal = new ItemStackHandler(0);
        BlockPos pos = null;
        if (extraData != null) {
            pos = extraData.readBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            access = ContainerLevelAccess.create(world, pos);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.bound) {
            if (this.boundItemMatcher != null)
                return this.boundItemMatcher.get();
            else if (this.boundBlockEntity != null)
                return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
            else if (this.boundEntity != null)
                return this.boundEntity.isAlive();
        }
        return true;
    }

    public Map<Integer, Slot> get() {
        return customSlots;
    }
}
