package net.foxyas.changedaddon.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UnifuserRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final float ProgressSpeed;

    public UnifuserRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems, float ProgressSpeed) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.ProgressSpeed = ProgressSpeed;
    }

    public boolean isRecipeHided() {
        String string = this.getId().getPath();
        return string.contains("_hided") || string.contains("_secret");
    }

    public CompoundTag getTagOfIngredient(Ingredient ingredient) {
        return Arrays.stream(ingredient.getItems()).findAny().orElse(ItemStack.EMPTY).getOrCreateTag();
    }

    @Override
    public boolean matches(@NotNull SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        // Verifica se a lista de ingredientes não está vazia
        if (!recipeItems.isEmpty()) {
            if (NBTIngredient.of(this.output).test(pContainer.getItem(3))) {
                return true;
            }
        }

        return false; // Retorna false se a lista de ingredientes estiver vazia
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer pContainer) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return output.copy();
    }

    public float getProgressSpeed() {
        return ProgressSpeed;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Type implements RecipeType<UnifuserRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "unifuser";

        private Type() {
        }
    }

    public static class Serializer implements RecipeSerializer<UnifuserRecipe>, IForgeRegistryEntry<RecipeSerializer<?>> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("changed_addon", "unifuser");

        @Override
        public @NotNull UnifuserRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); i++) {
                JsonElement ingredientElement = ingredients.get(i);

                Ingredient ingredient;

                if (ingredientElement.isJsonObject() && ingredientElement.getAsJsonObject().has("nbt")) {
                    ItemStack stack = ShapedRecipe.itemStackFromJson(ingredientElement.getAsJsonObject());
                    ingredient = NBTIngredient.of(stack);
                    ChangedAddonMod.LOGGER.info("[Changed Addon Recipes Types] Parsing nbt recipe with id {} of type {}", pRecipeId, Type.ID);
                } else {
                    ingredient = Ingredient.fromJson(ingredientElement);
                }

                inputs.set(i, ingredient);
            }

            float progressSpeed = GsonHelper.getAsFloat(pSerializedRecipe, "ProgressSpeed", 1.0f);

            return new UnifuserRecipe(pRecipeId, output, inputs, progressSpeed);
        }

        @Override
        public @Nullable UnifuserRecipe fromNetwork(@NotNull ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }
            ItemStack output = buf.readItem();
            float ProgressSpeed = buf.readFloat();
            return new UnifuserRecipe(id, output, inputs, ProgressSpeed);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, UnifuserRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeFloat(recipe.getProgressSpeed());
        }

        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return this;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return (Class<RecipeSerializer<?>>) (Class<?>) RecipeSerializer.class;
        }
    }
}
