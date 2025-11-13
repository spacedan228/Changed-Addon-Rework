package net.foxyas.changedaddon.recipes.special;

import com.google.gson.JsonObject;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonRecipeTypes;
import net.foxyas.changedaddon.item.KeycardItem;
import net.foxyas.changedaddon.recipes.UnifuserRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KeycardColorRecipe extends CustomRecipe {

    public KeycardColorRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, @NotNull Level world) {
        boolean foundKeycard = false;

        boolean topDye = false;
        boolean midDye = false;
        boolean bottomDye = false;

        int w = container.getWidth();
        int h = container.getHeight();

        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                ItemStack stack = container.getItem(row * w + col);

                if (stack.getItem() instanceof KeycardItem)
                    foundKeycard = true;

                if (stack.getItem() instanceof DyeItem) {
                    if (row == 0) topDye = true;
                    else if (row == h - 1) bottomDye = true;
                    else midDye = true;
                }
            }
        }

        // Válido: keycard + pelo menos um dye em qualquer linha
        return foundKeycard && (topDye || bottomDye || midDye);
    }


    @Override
    public @NotNull ItemStack assemble(CraftingContainer container) {
        ItemStack keycard = ItemStack.EMPTY;

        List<DyeColor> topColors = new ArrayList<>();
        List<DyeColor> bottomColors = new ArrayList<>();

        int w = container.getWidth();
        int h = container.getHeight();

        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                ItemStack item = container.getItem(row * w + col);

                if (item.getItem() instanceof KeycardItem)
                    keycard = item.copy();

                if (item.getItem() instanceof DyeItem dye) {
                    DyeColor color = dye.getDyeColor();

                    if (row == 0) {
                        topColors.add(color);
                    }
                    else if (row == h - 1) {
                        bottomColors.add(color);
                    }
                    else { // linha do meio → aplica nos dois
                        topColors.add(color);
                        bottomColors.add(color);
                    }
                }
            }
        }

        if (keycard.isEmpty())
            return ItemStack.EMPTY;

        CompoundTag tag = keycard.getOrCreateTag();

        if (!topColors.isEmpty())
            tag.putInt("ColorTop", mixDyes(topColors));

        if (!bottomColors.isEmpty())
            tag.putInt("ColorBottom", mixDyes(bottomColors));

        return keycard;
    }


    private static int mixDyes(List<DyeColor> dyes) {
        if (dyes.isEmpty())
            return 0xFFFFFF; // fallback (branco)

        int r = 0, g = 0, b = 0;
        int count = 0;

        for (DyeColor color : dyes) {
            float[] rgb = color.getTextureDiffuseColors();

            r += (int)(rgb[0] * 255);
            g += (int)(rgb[1] * 255);
            b += (int)(rgb[2] * 255);

            count++;
        }

        // Média, igual vanilla
        r /= count;
        g /= count;
        b /= count;

        return (r << 16) | (g << 8) | b;
    }


    @Override
    public boolean canCraftInDimensions(int w, int h) { return w * h >= 2; }

    public static class Type implements RecipeType<KeycardColorRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "keycard_coloring";

        private Type() {
        }
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ChangedAddonRecipeTypes.KEYCARD_COLOR.get();
    }

    public static class Serializer implements RecipeSerializer<KeycardColorRecipe> {

        public static final ResourceLocation ID = ChangedAddonMod.resourceLoc("keycard_coloring");

        @Override
        public @NotNull KeycardColorRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            // Nenhum dado necessário no JSON
            return new KeycardColorRecipe(id);
        }

        @Override
        public KeycardColorRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
            // Nenhum dado transmitido, então só retorna a instância
            return new KeycardColorRecipe(id);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull KeycardColorRecipe recipe) {
            // Nada para escrever
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
