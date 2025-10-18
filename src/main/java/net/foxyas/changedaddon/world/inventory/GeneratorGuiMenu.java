package net.foxyas.changedaddon.world.inventory;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GeneratorGuiMenu extends AbstractContainerMenu {

    public final Level level;
    public final Player entity;
    public final BlockPos pos;

    public GeneratorGuiMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ChangedAddonMenus.GENERATORGUI.get(), id);
        this.entity = inv.player;
        this.level = inv.player.level;
        pos = extraData.readBlockPos();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return level.getBlockState(pos).is(ChangedAddonBlocks.GENERATOR.get()) && pos.distToCenterSqr(player.position()) < 64;
    }
}
