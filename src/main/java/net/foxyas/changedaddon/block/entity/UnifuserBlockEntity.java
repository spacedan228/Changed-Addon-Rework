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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class UnifuserBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    protected final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());
    protected NonNullList<ItemStack> stacks = NonNullList.withSize(4, ItemStack.EMPTY);

    public boolean startRecipe = true;
    public double recipeProgress = 0;
    protected boolean recipeOn = false;
    public int tickCount;

    public UnifuserBlockEntity(BlockPos position, BlockState state) {
        super(ChangedAddonBlockEntities.UNIFUSER.get(), position, state);
    }

    public UnifuserBlockEntity(BlockEntityType<?> blockEntityType, BlockPos position, BlockState state) {
        super(blockEntityType, position, state);
    }
    
    public boolean isAdvanced() {
        return false;
    }

    public @NotNull LazyOptional<IItemHandler> getItemHandler(@Nullable Direction facing) {
        return getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (!this.tryLoadLootTable(tag))
            this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.stacks);

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
        return new TextComponent("unifuser");
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
        return new UnifuserGuiMenu(id, inventory, worldPosition);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent("Unifuser");
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
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return index != 3;
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
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        if (level.isClientSide) return;
        if (!(blockEntity instanceof UnifuserBlockEntity unifuserBlockEntity)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        boolean shouldTick = false;
        if (unifuserBlockEntity.tickCount % 20 == 0) {
            shouldTick = true;
        }

        if (!shouldTick) {
            unifuserBlockEntity.tickCount ++;
            level.sendBlockUpdated(blockPos, blockState, blockState, 3);
            return;
        }

        // --- Checar inputs e output ---
        ItemStack input0 = unifuserBlockEntity.getItem(0);
        ItemStack input1 = unifuserBlockEntity.getItem(1);
        ItemStack input2 = unifuserBlockEntity.getItem(2);
        ItemStack output = unifuserBlockEntity.getItem(3);

        // Nenhum input = parar receita
        if (input0.isEmpty() && input1.isEmpty() && input2.isEmpty()) {
            unifuserBlockEntity.recipeOn = false;
            unifuserBlockEntity.startRecipe = false;

            if (unifuserBlockEntity.recipeProgress > 0) {
                unifuserBlockEntity.recipeProgress = Math.max(0, unifuserBlockEntity.recipeProgress - 5);
            }

            unifuserBlockEntity.setChanged();
            return;
        }

        // Output cheio = travar processo
        boolean outputFull = !output.isEmpty() && output.getCount() >= output.getMaxStackSize();
        if (outputFull) {
            unifuserBlockEntity.setChanged();
            return;
        }

        // Sem receita iniciada
        if (!unifuserBlockEntity.startRecipe) {
            unifuserBlockEntity.recipeProgress = 0;
            unifuserBlockEntity.setChanged();
            return;
        }

        // Encontrar receita válida
        UnifuserRecipe recipe = RecipesHandle.findRecipeForUnifuser(serverLevel, input0, input1, input2);
        boolean hasRecipe = recipe != null;
        unifuserBlockEntity.recipeOn = hasRecipe;
        boolean isAdvanced = unifuserBlockEntity.isAdvanced();

        // Progresso da receita
        if (hasRecipe) {
            if (unifuserBlockEntity.recipeProgress < 100) {
                unifuserBlockEntity.recipeProgress += recipe.getProgressSpeed() * (isAdvanced ? 4 : 1);
            }
        } else {
            unifuserBlockEntity.recipeProgress = 0;
        }

        // Concluir receita
        if (hasRecipe && unifuserBlockEntity.recipeProgress >= 100) {
            ItemStack result = recipe.getResultItem();

            boolean canOutput =
                    output.isEmpty() ||
                            (output.getItem() == result.getItem() && output.getCount() + result.getCount() <= output.getMaxStackSize());

            if (canOutput) {
                // Consumir inputs
                unifuserBlockEntity.extractItem(0, 1, false);
                unifuserBlockEntity.extractItem(1, 1, false);
                unifuserBlockEntity.extractItem(2, 1, false);

                // Adicionar output
                unifuserBlockEntity.insertItem(3, result.copy(), false);

                // Resetar progresso e consumir energia
                unifuserBlockEntity.recipeProgress = 0;
            }
        }

        unifuserBlockEntity.setChanged();
        level.sendBlockUpdated(blockPos, blockState, blockState, 3);
    }


    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < 0 || slot >= stacks.size() || amount <= 0)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = stacks.get(slot);
        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        int extractAmount = Math.min(amount, stackInSlot.getCount());
        ItemStack extracted = stackInSlot.copy();
        extracted.setCount(extractAmount);

        if (!simulate) {
            stackInSlot.shrink(extractAmount);
            if (stackInSlot.isEmpty()) {
                stacks.set(slot, ItemStack.EMPTY);
            }
            setChanged(); // notifica que algo mudou
        }

        return extracted;
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot < 0 || slot >= stacks.size() || stack.isEmpty())
            return stack;

        ItemStack slotStack = stacks.get(slot);

        // Slot vazio → coloca direto
        if (slotStack.isEmpty()) {
            if (!simulate) {
                stacks.set(slot, stack.copy());
                setChanged();
            }
            return ItemStack.EMPTY;
        }

        // Se item diferente → não empilha
        if (!ItemStack.isSameItemSameTags(slotStack, stack))
            return stack;

        int space = slotStack.getMaxStackSize() - slotStack.getCount();
        if (space <= 0)
            return stack;

        int toInsert = Math.min(space, stack.getCount());

        if (!simulate) {
            slotStack.grow(toInsert);
            setChanged();
        }

        ItemStack remaining = stack.copy();
        remaining.shrink(toInsert);
        return remaining.getCount() <= 0 ? ItemStack.EMPTY : remaining;
    }

}
