package net.foxyas.changedaddon.procedures;

import io.netty.buffer.Unpooled;
import net.foxyas.changedaddon.world.inventory.CatalyzerGuiMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
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

public class CatalyzerOnBlockRightClickedProcedure {

    public static void execute(Level level, BlockPos pos, BlockState state, @NotNull Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity == null) return;

        if(player.isShiftKeyDown()){
            if(blockEntity.getTileData().getBoolean("start_recipe")){
                blockEntity.getTileData().putBoolean("start_recipe", false);
                level.setBlockAndUpdate(pos, state);
                player.displayClientMessage(new TextComponent(("you stop the " + new TranslatableComponent(("block." + (ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString()).replace(":", "."))).getString())), true);
            } else {
                blockEntity.getTileData().putBoolean("start_recipe", true);
                level.setBlockAndUpdate(pos, state);
                player.displayClientMessage(new TextComponent(("you start the " + new TranslatableComponent(("block." + (ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString()).replace(":", "."))).getString())), true);
            }
            return;
        }

        if(!(player instanceof ServerPlayer sPlayer)) return;

        NetworkHooks.openGui(sPlayer, new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return new TextComponent("CatalyzerGui");
            }

            @Override
            public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
                return new CatalyzerGuiMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
            }
        }, pos);
    }
}
