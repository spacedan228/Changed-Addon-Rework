package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.block.AdvancedCatalyzerBlock;
import net.foxyas.changedaddon.recipes.CatalyzerRecipe;
import net.foxyas.changedaddon.recipes.RecipesHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CatalyzerUpdateTickProcedure {

    public static void execute(ServerLevel level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity == null) return;
        CompoundTag tag = blockEntity.getTileData();

        double nitrogen = tag.getDouble("nitrogen_power");
        if (nitrogen < 200) {
            tag.putDouble("nitrogen_power", nitrogen + 1);
            level.sendBlockUpdated(pos, state, state, 3);
            return;
        }

        IItemHandler cap = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null);
        if(!(cap instanceof IItemHandlerModifiable handler)) return;

        double recipeProgress = tag.getDouble("recipe_progress");

        if (handler.getStackInSlot(0).isEmpty()) {
            tag.putBoolean("recipe_on", false);

            if (recipeProgress > 0) {
                recipeProgress -= 5;
            } else if (recipeProgress <= 0) {
                recipeProgress = 0;
            }
            tag.putDouble("recipe_progress", recipeProgress);
            level.sendBlockUpdated(pos, state, state, 3);
            return;
        }

        boolean isFull = handler.getStackInSlot(1).getCount() >= handler.getStackInSlot(1).getMaxStackSize();
        tag.putBoolean("Full", isFull);
        if(isFull){
            level.sendBlockUpdated(pos, state, state, 3);
            return;
        }

        if (!tag.getBoolean("start_recipe")) {
            tag.putDouble("recipe_progress", 0);
            level.sendBlockUpdated(pos, state, state, 3);
            return;
        }

        ItemStack slot0 = handler.getStackInSlot(0).copy();
        CatalyzerRecipe recipe = RecipesHandle.findRecipe(level, slot0);
        boolean isRecipeOn = recipe != null;

        tag.putBoolean("recipe_on", isRecipeOn);

        if (isRecipeOn) {
            if (recipeProgress < 100) {
                recipeProgress += recipe.getProgressSpeed() * (state.getBlock() instanceof AdvancedCatalyzerBlock ? 4 : 1);
            }
        } else {
            recipeProgress = 0;
        }

        if (isRecipeOn && recipeProgress >= 100) {
            ItemStack slot1 = handler.getStackInSlot(1);
            ItemStack output = recipe.getResultItem();
            if (slot1.getItem() == output.getItem() || slot1.isEmpty()) {
                handler.extractItem(0, 1, false);
                handler.insertItem(1, output, false);

                recipeProgress = 0;
                tag.putDouble("nitrogen_power", nitrogen - recipe.getNitrogenUsage());
            }
        }

        tag.putDouble("recipe_progress", recipeProgress);
        level.sendBlockUpdated(pos, state, state, 3);
    }
}
