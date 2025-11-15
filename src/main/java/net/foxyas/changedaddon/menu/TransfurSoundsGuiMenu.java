package net.foxyas.changedaddon.menu;

import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TransfurSoundsGuiMenu extends AbstractContainerMenu {

    public final Level level;
    public final Player player;

    public TransfurSoundsGuiMenu(int id, Inventory inv, FriendlyByteBuf extraData){
        this(id, inv);
    }

    public TransfurSoundsGuiMenu(int id, Inventory inv) {
        super(ChangedAddonMenus.TRANSFUR_SOUNDS_GUI.get(), id);
        this.player = inv.player;
        this.level = inv.player.level;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
