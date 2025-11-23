package net.foxyas.changedaddon.menu;

import net.foxyas.changedaddon.block.entity.InformantBlockEntity;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class InformantGuiMenu extends AbstractMenu {

    public final Level level;
    public final Player player;
    public final InformantBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final SlotItemHandler slot;

    public InformantGuiMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, (InformantBlockEntity) inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public InformantGuiMenu(int id, Inventory inv, InformantBlockEntity blockEntity) {
        super(ChangedAddonMenus.INFORMANT_MENU.get(), id);
        player = inv.player;
        level = player.level;

        this.blockEntity = blockEntity;
        access = ContainerLevelAccess.create(level, blockEntity.getBlockPos());

        slot = new SlotItemHandler(blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get(), 0, 152, 89);

        createPlayerHotbar(inv, 0, 31);
        createPlayerInventory(inv, 0, 31);

        addSlot(slot);
    }

    public ItemStack getStackInSlot() {
        return slot.getItem();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(access, player, blockEntity.getBlockState().getBlock());
    }
}
