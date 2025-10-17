package net.foxyas.changedaddon.menu;

import com.mojang.datafixers.util.Pair;
import net.foxyas.changedaddon.entity.advanced.LatexSnowFoxFoxyasEntity;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
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
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;

public class FoxyasMenu extends AbstractEntityMenu<LatexSnowFoxFoxyasEntity> {

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

        this.slots.replaceAll((slot) -> {
            int OffsetX = -64;
            int OffsetY = 64;
            if (slot instanceof SlotItemHandler slotItemHandler) {
                if (slotItemHandler.getSlotIndex() > 3) {
                    return new SlotItemHandler(slotItemHandler.getItemHandler(), slotItemHandler.getSlotIndex(), slotItemHandler.x + OffsetX, slotItemHandler.y + OffsetY);
                }
                final EquipmentSlot equipmentslot = SLOT_IDS[slotItemHandler.getSlotIndex()];
                return new SlotItemHandler(slotItemHandler.getItemHandler(), slotItemHandler.getSlotIndex(), slotItemHandler.x + OffsetX, slotItemHandler.y + OffsetY) {

                    public int getMaxStackSize() {
                        return 1;
                    }

                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.canEquip(equipmentslot, entity);
                    }

                    public boolean mayPickup(Player player) {
                        ItemStack itemstack = getItem();
                        return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(player);
                    }

                    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                        return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[equipmentslot.getIndex()]);
                    }
                };
            }


            return new Slot(slot.container, slot.index, slot.x + OffsetX, slot.y + OffsetY);
        });
    }

    public FoxyasMenu(int containerId, Inventory playerInv, FriendlyByteBuf data) {
        this(containerId, playerInv, (LatexSnowFoxFoxyasEntity) playerInv.player.level.getEntity(data.readVarInt()));
    }

    public static class Factory implements IContainerFactory<FoxyasMenu> {
        @Override
        public FoxyasMenu create(final int windowId, final Inventory inv, final FriendlyByteBuf data) {
            return new FoxyasMenu(windowId, inv, data);
        }
    }
}
