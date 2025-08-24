package net.foxyas.changedaddon.menu;

import com.mojang.datafixers.util.Pair;
import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PrototypeMenu extends AbstractMenu {

    public static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    public static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
    public static final int TOO_FAR = 64;

    private final PrototypeEntity prototype;

    public PrototypeMenu(int containerId, Inventory playerInv, PrototypeEntity prototype) {
        super(ChangedAddonMenus.PROTOTYPE_MENU, containerId);
        this.prototype = prototype;
        IItemHandler combinedInv = prototype.getCombinedInv();

        createPlayerHotbar(playerInv);
        createPlayerInventory(playerInv);

        //Armor
        for(int i = 0; i < 4; i++) {
            final EquipmentSlot equipmentslot = SLOT_IDS[i];
            addSlot(new SlotItemHandler(combinedInv, i, 8, 8 + (3 - i) * 18) {

                public int getMaxStackSize() {
                    return 1;
                }

                public boolean mayPlace(@NotNull ItemStack stack) {
                    return stack.canEquip(equipmentslot, prototype);
                }

                public boolean mayPickup(Player player) {
                    ItemStack itemstack = getItem();
                    return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(player);
                }

                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[equipmentslot.getIndex()]);
                }
            });
        }

        //Hands
        addSlot(new SlotItemHandler(combinedInv, 4, 77, 8 + 2 * 18));//Main
        addSlot(new SlotItemHandler(combinedInv, 5, 77, 8 + 3 * 18));//Off

        //Inventory
        for(int i = 0; i < 3; i++){
            for(int ii = 0; ii < 3; ii++){
                addSlot(new SlotItemHandler(combinedInv, 6 + i * 3 + ii, 107 + ii * 18, 18 + i * 18));
            }
        }
    }

    public PrototypeMenu(int containerId, Inventory playerInv, FriendlyByteBuf data) {
        this(containerId, playerInv, (PrototypeEntity) playerInv.player.level.getEntity(data.readVarInt()));
    }

    public PrototypeEntity getPrototype(){
        return prototype;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return player.distanceToSqr(prototype) < TOO_FAR;
    }
}
