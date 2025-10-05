package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.world.inventory.UnifuserGuiMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class UnifuserOnBlockRightClickedProcedure {

    public static void execute(Level level, BlockPos pos, BlockState blockstate, @NotNull Player player) {
        if(level.isClientSide) return;
        BlockEntity be = level.getBlockEntity(pos);
        if(be == null) return;

        if (player.isShiftKeyDown()) {
            if (be.getTileData().getBoolean("start_recipe")) {
                be.getTileData().putBoolean("start_recipe", false);
                level.sendBlockUpdated(pos, blockstate, blockstate, 3);
                player.displayClientMessage(new TextComponent(("you stop the " + new TranslatableComponent(("block." + (ForgeRegistries.BLOCKS.getKey(blockstate.getBlock()).toString()).replace(":", "."))).getString())), true);
            } else {
                be.getTileData().putBoolean("start_recipe", true);
                level.sendBlockUpdated(pos, blockstate, blockstate, 3);
                player.displayClientMessage(new TextComponent(("you start the " + new TranslatableComponent(("block." + (ForgeRegistries.BLOCKS.getKey(blockstate.getBlock()).toString()).replace(":", "."))).getString())), true);
            }
            return;
        }

        NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return new TextComponent("Unifusergui");
            }

            @Override
            public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
                return new UnifuserGuiMenu(id, inventory, pos);
            }
        }, pos);
    }
}
