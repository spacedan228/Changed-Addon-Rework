package net.foxyas.changedaddon.menu;

import net.foxyas.changedaddon.entity.advanced.FoxyasEntity;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.IContainerFactory;

public class FoxyasMenu extends AbstractEntityMenu<FoxyasEntity> {

    public FoxyasMenu(int containerId, Inventory playerInv, FoxyasEntity prototype) {
        super(ChangedAddonMenus.PROTOTYPE_MENU, containerId, playerInv, prototype);
        IItemHandler combinedInv = prototype.getItemHandler();

        //Inventory
        for(int i = 0; i < 3; i++){
            for(int ii = 0; ii < 3; ii++){
                addSlot(new SlotItemHandler(combinedInv, 6 + i * 3 + ii, 107 + ii * 18, 18 + i * 18));
            }
        }
    }

    public FoxyasMenu(int containerId, Inventory playerInv, FriendlyByteBuf data) {
        this(containerId, playerInv, (FoxyasEntity) playerInv.player.level.getEntity(data.readVarInt()));
    }

    public static class Factory implements IContainerFactory<FoxyasMenu> {
        @Override
        public FoxyasMenu create(final int windowId, final Inventory inv, final FriendlyByteBuf data) {
            return new FoxyasMenu(windowId, inv, data);
        }
    }
}
