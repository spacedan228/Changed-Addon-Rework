package net.foxyas.changedaddon.menu;

import net.foxyas.changedaddon.entity.advanced.LatexSnowFoxFoxyasEntity;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class FoxyasInventoryMenu extends AbstractEntityMenu<LatexSnowFoxFoxyasEntity> {

    public static final int X_OFFSET = -64, Y_OFFSET = 0;

    public FoxyasInventoryMenu(int containerId, Inventory playerInv, FriendlyByteBuf data) {
        this(containerId, playerInv, (LatexSnowFoxFoxyasEntity) playerInv.player.level.getEntity(data.readVarInt()));
    }

    public FoxyasInventoryMenu(int containerId, Inventory playerInv, LatexSnowFoxFoxyasEntity foxyas) {
        super(ChangedAddonMenus.FOXYAS_INVENTORY_MENU.get(), containerId, playerInv, foxyas, X_OFFSET, Y_OFFSET);
        IItemHandler combinedInv = foxyas.getItemHandler();

        //Inventory
        for (int i = 0; i < 3; i++) { //VERTICAL
            for (int ii = 0; ii < 3; ii++) { //HORIZONTAL
                addSlot(new SlotItemHandler(combinedInv, 6 + i * 3 + ii, 107 + ii * 18 + X_OFFSET, 18 + i * 18 + Y_OFFSET));
            }
        }

        for (int i = 0; i < 3; i++) { //VERTICAL
            for (int ii = 0; ii < 9; ii++) { //HORIZONTAL
                // 6 = non inventory slots
                // 9 = first 9 slots
                // 4 = extra offset to look more clean
                addSlot(new SlotItemHandler(combinedInv, (6 + 9) + i * 9 + ii, 107 + 4 + (18 * 4) + ii * 18 + X_OFFSET, 18 + i * 18 + Y_OFFSET));
            }
        }
    }
}
