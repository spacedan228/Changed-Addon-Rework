package net.foxyas.changedaddon.menu;

import com.mojang.datafixers.util.Pair;
import net.foxyas.changedaddon.entity.advanced.LatexSnowFoxFoxyasEntity;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class FoxyasMenu extends AbstractEntityMenu<LatexSnowFoxFoxyasEntity> {

    public static final int OffsetX = -64, OffsetY = 0;

    public FoxyasMenu(int containerId, Inventory playerInv, LatexSnowFoxFoxyasEntity foxyas) {
        super(ChangedAddonMenus.TEST_FOXYAS_MENU.get(), containerId, playerInv, foxyas);
        IItemHandler combinedInv = foxyas.getItemHandler();

        //Inventory
        for (int i = 0; i < 3; i++) { //VERTICAL
            for (int ii = 0; ii < 3; ii++) { //HORIZONTAL
                addSlot(new SlotItemHandler(combinedInv, 6 + i * 3 + ii, 107 + ii * 18, 18 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) { //VERTICAL
            for (int ii = 0; ii < 9; ii++) { //HORIZONTAL
                // 6 = non inventory slots
                // 9 = first 9 slots
                // 4 = extra offset to look more clean
                addSlot(new SlotItemHandler(combinedInv, (6 + 9) + i * 9 + ii, 107 + 4 + (18 * 4) + ii * 18, 18 + i * 18));
            }
        }

        offsetAllSlots(OffsetX, OffsetY);
    }

    public FoxyasMenu(int containerId, Inventory playerInv, FriendlyByteBuf data) {
        this(containerId, playerInv, (LatexSnowFoxFoxyasEntity) playerInv.player.level.getEntity(data.readVarInt()));
    }

    public void offsetAllSlots(int offsetX, int offsetY) {
        // Cria uma nova lista para guardar os slots copiados
        NonNullList<Slot> newSlots = NonNullList.withSize(this.slots.size(), null);

        for (int i = 0; i < this.slots.size(); i++) {
            Slot slot = this.slots.get(i);

            // Cria uma cópia do slot original, mas movido
            Slot copiedSlot = new Slot(slot.container, slot.index, slot.x + offsetX, slot.y + offsetY) {

                @Override
                public int getSlotIndex() {
                    return slot.getSlotIndex();
                }

                @Override
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    return slot.mayPlace(stack);
                }

                @Override
                public int getContainerSlot() {
                    return slot.getContainerSlot();
                }

                @Override
                @Nonnull
                public ItemStack getItem() {
                    return slot.getItem();
                }

                @Override
                public int getMaxStackSize() {
                    return slot.getMaxStackSize();
                }

                @Override
                public int getMaxStackSize(@Nonnull ItemStack stack) {
                    return slot.getMaxStackSize(stack);
                }

                @Override
                public boolean mayPickup(@NotNull Player playerIn) {
                    return slot.mayPickup(playerIn);
                }

                @Override
                public @Nullable Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return slot.getNoItemIcon();
                }
            };

            newSlots.set(i, copiedSlot);
        }

        // Substitui a lista antiga pela nova
        this.slots.clear();
        this.slots.addAll(newSlots);
    }


    public static class Factory implements IContainerFactory<FoxyasMenu> {
        @Override
        public FoxyasMenu create(final int windowId, final Inventory inv, final FriendlyByteBuf data) {
            return new FoxyasMenu(windowId, inv, data);
        }
    }
}
