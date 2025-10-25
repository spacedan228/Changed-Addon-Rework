package net.foxyas.changedaddon.block.entity;

import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.foxyas.changedaddon.recipes.RecipesHandle;
import net.foxyas.changedaddon.recipes.UnifuserRecipe;
import net.foxyas.changedaddon.world.inventory.UnifuserGuiMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class AdvancedUnifuserBlockEntity extends UnifuserBlockEntity {

    public AdvancedUnifuserBlockEntity(BlockPos position, BlockState state) {
        super(ChangedAddonBlockEntities.ADVANCED_UNIFUSER.get(), position, state);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return super.getUpdatePacket();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return super.getUpdateTag();
    }

    @Override
    public int getContainerSize() {
        return super.getContainerSize();
    }

    @Override
    public @NotNull Component getDefaultName() {
        return new TextComponent("advanced_unifuser");
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
        return super.createMenu(id, inventory);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent("Advanced Unifuser");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return super.getItems();
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> stacks) {
        super.setItems(stacks);
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return super.canPlaceItem(index, stack);
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return super.getSlotsForFace(side);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        if (index == 0)
            return false;
        if (index == 1)
            return false;
        return index != 2;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return handlers[facing.ordinal()].cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (LazyOptional<? extends IItemHandler> handler : handlers)
            handler.invalidate();
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        UnifuserBlockEntity.clientTick(level, blockPos, blockState, blockEntity);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        UnifuserBlockEntity.serverTick(level, blockPos, blockState, blockEntity);
    }
}
