package net.foxyas.changedaddon.extension.jeiSuport;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.recipes.special.KeycardColorRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class KeycardColorRecipeCategory implements IRecipeCategory<KeycardColorRecipe> {

    private final IDrawable icon;

    public KeycardColorRecipeCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ChangedAddonItems.KEYCARD_ITEM.get()));
    }

    public static final ResourceLocation ID =
            new ResourceLocation(ChangedAddonMod.MODID, "keycard_coloring");

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends KeycardColorRecipe> getRecipeClass() {
        return KeycardColorRecipe.class;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.changed_addon.keycard_color");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createBlankDrawable(150, 60);
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, KeycardColorRecipe recipe, IFocusGroup focus) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 10);
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 40);
        builder.addSlot(RecipeIngredientRole.INPUT, 40, 25);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 25);
    }
}
