package net.foxyas.changedaddon.block.entity;

import io.netty.buffer.Unpooled;
import net.foxyas.changedaddon.block.AdvancedCatalyzerBlock;
import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.foxyas.changedaddon.recipes.CatalyzerRecipe;
import net.foxyas.changedaddon.recipes.RecipesHandle;
import net.foxyas.changedaddon.world.inventory.CatalyzerGuiMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class CatalyzerBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    protected final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());
    protected NonNullList<ItemStack> stacks = NonNullList.withSize(2, ItemStack.EMPTY);

    public boolean startRecipe = true;
    public double nitrogenPower = 0;
    public double recipeProgress = 0;
    protected boolean recipeOn = false;
    public int tickCount = 0;

    public CatalyzerBlockEntity(BlockPos position, BlockState state) {
        super(ChangedAddonBlockEntities.CATALYZER.get(), position, state);
    }

    public CatalyzerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos position, BlockState state) {
        super(blockEntityType, position, state);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (!this.tryLoadLootTable(tag))
            this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.stacks);

        nitrogenPower = tag.getDouble("nitrogen_power");
        recipeProgress = tag.getDouble("recipe_progress");
        recipeOn = tag.getBoolean("recipe_on");
        startRecipe = tag.getBoolean("start_recipe");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.stacks);
        }

        tag.putDouble("nitrogen_power", nitrogenPower);
        tag.putDouble("recipe_progress", recipeProgress);
        tag.putBoolean("recipe_on", recipeOn);
        tag.putBoolean("start_recipe", startRecipe);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }

    @Override
    public int getContainerSize() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.stacks)
            if (!itemstack.isEmpty())
                return false;
        return true;
    }

    public boolean isSlotFull(int index) {
        return getItem(index).getCount() >= getItem(index).getMaxStackSize();
    }

    @Override
    public @NotNull Component getDefaultName() {
        return new TextComponent("catalyzer");
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
        return new CatalyzerGuiMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent("Catalyzer");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return IntStream.range(0, this.getContainerSize()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return index != 0;
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
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(blockEntity instanceof CatalyzerBlockEntity catalyzerBlockEntity)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        boolean shouldTick = false;
        if (catalyzerBlockEntity.tickCount >= 5) {
            shouldTick = true;
            catalyzerBlockEntity.tickCount = 0;
        }

        if (!shouldTick) {
            catalyzerBlockEntity.tickCount ++;
            update(serverLevel, pos, state, catalyzerBlockEntity);
            return;
        }

        if (catalyzerBlockEntity.nitrogenPower < 200) {
            catalyzerBlockEntity.nitrogenPower += 1;
            level.sendBlockUpdated(pos, state, state, 3);
            catalyzerBlockEntity.setChanged();
            return;
        }

        IItemHandlerModifiable handler = (IItemHandlerModifiable)
                blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null);
        if (handler == null) return;

        if (handler.getStackInSlot(0).isEmpty()) {
            catalyzerBlockEntity.recipeOn = false;
            catalyzerBlockEntity.recipeProgress = Math.max(0, catalyzerBlockEntity.recipeProgress - 5);
            update(serverLevel, pos, state, catalyzerBlockEntity);
            return;
        }

        boolean isFull = handler.getStackInSlot(1).getCount() >= handler.getStackInSlot(1).getMaxStackSize();
        if (isFull) {
            update(serverLevel, pos, state, catalyzerBlockEntity);
            return;
        }

        if (!catalyzerBlockEntity.startRecipe) {
            update(serverLevel, pos, state, catalyzerBlockEntity);
            return;
        }

        ItemStack input = handler.getStackInSlot(0).copy();
        CatalyzerRecipe recipe = RecipesHandle.findRecipeForCatalyzer(serverLevel, input);
        catalyzerBlockEntity.recipeOn = recipe != null;

        if (recipe != null) {
            if (catalyzerBlockEntity.recipeProgress < 100) {
                double speed = recipe.getProgressSpeed();
                if (state.getBlock() instanceof AdvancedCatalyzerBlock) {
                    speed *= 4;
                }
                catalyzerBlockEntity.recipeProgress += speed;
            }

            if (catalyzerBlockEntity.recipeProgress >= 100) {
                ItemStack outputSlot = handler.getStackInSlot(1);
                ItemStack output = recipe.getResultItem();

                if (outputSlot.isEmpty() || outputSlot.getItem() == output.getItem()) {
                    handler.extractItem(0, 1, false);
                    handler.insertItem(1, output.copy(), false);
                    catalyzerBlockEntity.nitrogenPower -= recipe.getNitrogenUsage();
                    catalyzerBlockEntity.recipeProgress = 0;
                }
            }
        } else {
            catalyzerBlockEntity.recipeProgress = 0;
        }

        update(serverLevel, pos, state, catalyzerBlockEntity);
    }

    private static void update(ServerLevel level, BlockPos pos, BlockState state, CatalyzerBlockEntity be) {
        be.setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }
}
