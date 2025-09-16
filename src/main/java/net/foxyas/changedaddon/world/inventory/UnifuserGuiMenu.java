package net.foxyas.changedaddon.world.inventory;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class UnifuserGuiMenu extends AbstractContainerMenu {

    public final Level world;
    public final Player entity;
    public int x, y, z;
    private final ContainerLevelAccess access;
    private final BlockEntity boundBlockEntity;

    public UnifuserGuiMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ChangedAddonMenus.UNIFUSER_GUI, id);
        this.entity = inv.player;
        this.world = inv.player.level;
        IItemHandler internal = new ItemStackHandler(4);

        BlockPos pos = extraData.readBlockPos();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        access = ContainerLevelAccess.create(world, pos);

        boundBlockEntity = this.world.getBlockEntity(pos);
        if (boundBlockEntity != null)
            internal = boundBlockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).resolve().orElseThrow();

        addSlot(new SlotItemHandler(internal, 0, 15, 45) {

            @Override
            public boolean mayPlace(@NotNull ItemStack itemstack) {
                //if (itemstack.getItem() == ChangedAddonItems.AMMONIA.get() || itemstack.getItem() == ChangedAddonItems.LITIX_CAMONIA.get() || itemstack.getItem() == ChangedAddonItems.LAETHIN.get()
                //        || itemstack.getItem() == Blocks.TINTED_GLASS.asItem() || itemstack.getItem() == ChangedAddonItems.ANTI_LATEX_BASE.get()) {
                //    return true;
                //}
                return true;
            }
        });
        addSlot(new SlotItemHandler(internal, 3, 155, 57) {

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });
        addSlot(new SlotItemHandler(internal, 2, 50, 57) {

            @Override
            public boolean mayPlace(@NotNull ItemStack itemstack) {
                return itemstack.getItem() == ChangedAddonItems.CATALYZED_DNA.get() || itemstack.is(ChangedItems.BLOOD_SYRINGE.get())
                        || itemstack.is(ChangedItems.LATEX_SYRINGE.get());
            }
        });
        addSlot(new SlotItemHandler(internal, 1, 15, 70));
        for (int si = 0; si < 3; ++si)
            for (int sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(inv, sj + (si + 1) * 9, 12 + 8 + sj * 18, 21 + 84 + si * 18));
        for (int si = 0; si < 9; ++si)
            this.addSlot(new Slot(inv, si, 12 + 8 + si * 18, 21 + 142));
    }

    public boolean isSlotEmpty(int slot){
        return getSlot(slot).getItem().isEmpty();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 4) {
                if (!this.moveItemStackTo(itemstack1, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (!this.moveItemStackTo(itemstack1, 0, 4, false)) {
                if (index < 4 + 27) {
                    if (!this.moveItemStackTo(itemstack1, 4 + 27, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemstack1, 4, 4 + 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo(@NotNull ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
        boolean flag = false;
        int i = p_38905_;
        if (p_38907_) {
            i = p_38906_ - 1;
        }
        if (p_38904_.isStackable()) {
            while (!p_38904_.isEmpty()) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
                    int j = itemstack.getCount() + p_38904_.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), p_38904_.getMaxStackSize());
                    if (j <= maxSize) {
                        p_38904_.setCount(0);
                        itemstack.setCount(j);
                        slot.set(itemstack);
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        p_38904_.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.set(itemstack);
                        flag = true;
                    }
                }
                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        if (!p_38904_.isEmpty()) {
            if (p_38907_) {
                i = p_38906_ - 1;
            } else {
                i = p_38905_;
            }
            while (true) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
                    if (p_38904_.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(p_38904_.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(p_38904_.split(p_38904_.getCount()));
                    }
                    slot1.setChanged();
                    flag = true;
                    break;
                }
                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        return flag;
    }
}
