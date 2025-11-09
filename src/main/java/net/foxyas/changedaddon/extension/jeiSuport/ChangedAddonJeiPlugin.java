package net.foxyas.changedaddon.extension.jeiSuport;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.foxyas.changedaddon.client.gui.FoxyasInventoryMenuScreen;
import net.foxyas.changedaddon.enchantment.TransfurAspectEnchantment;
import net.foxyas.changedaddon.extension.jeiSuport.guisHandlers.FoxyasGuiContainerHandler;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonEnchantments;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.recipes.CatalyzerRecipe;
import net.foxyas.changedaddon.recipes.UnifuserRecipe;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.item.Syringe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@JeiPlugin
public class ChangedAddonJeiPlugin implements IModPlugin {
    public static mezz.jei.api.recipe.RecipeType<CatalyzerRecipe> JeiCatalyzer_Type = new mezz.jei.api.recipe.RecipeType<>(CatalyzerRecipeCategory.UID, CatalyzerRecipe.class);
    public static mezz.jei.api.recipe.RecipeType<UnifuserRecipe> JeiUnifuser_Type = new mezz.jei.api.recipe.RecipeType<>(UnifuserRecipeCategory.UID, UnifuserRecipe.class);


    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.parse("changed_addon:jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CatalyzerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new UnifuserRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<CatalyzerRecipe> allCatalyzerRecipes = recipeManager.getAllRecipesFor(CatalyzerRecipe.Type.INSTANCE);
        List<CatalyzerRecipe> publicCatalyzerRecipes = allCatalyzerRecipes.stream().filter((catalyzerRecipe -> !catalyzerRecipe.isRecipeHided())).toList();
        registration.addRecipes(JeiCatalyzer_Type, publicCatalyzerRecipes);
        List<UnifuserRecipe> allUnifuserRecipes = recipeManager.getAllRecipesFor(UnifuserRecipe.Type.INSTANCE);
        List<UnifuserRecipe> publicUnifuserRecipes = allUnifuserRecipes.stream().filter((unifuserRecipe) -> !unifuserRecipe.isRecipeHided()).toList();
        registration.addRecipes(JeiUnifuser_Type, publicUnifuserRecipes);

        //Items Info
        ChangedAddonJeiDescriptionHandler.registerDescriptions(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ChangedAddonBlocks.CATALYZER.get().asItem()), JeiCatalyzer_Type);
        registration.addRecipeCatalyst(new ItemStack(ChangedAddonBlocks.ADVANCED_CATALYZER.get().asItem()), JeiCatalyzer_Type);
        registration.addRecipeCatalyst(new ItemStack(ChangedAddonBlocks.UNIFUSER.get().asItem()), JeiUnifuser_Type);
        registration.addRecipeCatalyst(new ItemStack(ChangedAddonBlocks.ADVANCED_UNIFUSER.get().asItem()), JeiUnifuser_Type);
    }


    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        ChangedAddonJeiGuiHandler.registerModMenusHandlers(registration);
    }


    public static class ChangedAddonJeiGuiHandler {
        public static void registerModMenusHandlers(IGuiHandlerRegistration registration) {
            registration.addGuiContainerHandler(FoxyasInventoryMenuScreen.class, new FoxyasGuiContainerHandler());
        }
    }

    public static class ChangedAddonJeiDescriptionHandler {
        public static void registerDescriptions(IRecipeRegistration registration) {
            // Item Information
            registration.addIngredientInfo(new ItemStack(ChangedAddonItems.TRANSFUR_TOTEM.get()), VanillaTypes.ITEM_STACK, new TranslatableComponent("changed_addon.jei_descriptions.latex_totem"));
            registration.addIngredientInfo(new ItemStack(ChangedAddonItems.EXPERIMENT_009_DNA.get()), VanillaTypes.ITEM_STACK, new TranslatableComponent("changed_addon.jei_descriptions.exp9_dna"));
            registration.addIngredientInfo(new ItemStack(ChangedAddonItems.SYRINGE_WITH_LITIX_CAMMONIA.get()), VanillaTypes.ITEM_STACK, new TranslatableComponent("changed_addon.jei_descriptions.litix_cammonia_syringe"));
            registration.addIngredientInfo(new ItemStack(ChangedAddonItems.LAETHIN_SYRINGE.get()), VanillaTypes.ITEM_STACK, new TranslatableComponent("changed_addon.jei_descriptions.laethin_syringe"));
            registration.addIngredientInfo(new ItemStack(ChangedAddonItems.POT_WITH_CAMONIA.get()), VanillaTypes.ITEM_STACK, new TranslatableComponent("changed_addon.jei_descriptions.pot_with_cammonia"));
            registration.addIngredientInfo(new ItemStack(ChangedAddonItems.DIFFUSION_SYRINGE.get()), VanillaTypes.ITEM_STACK, new TranslatableComponent("changed_addon.jei_descriptions.diffusion_syringe"));
            registration.addIngredientInfo(new ItemStack(ChangedAddonItems.IRIDIUM.get()), VanillaTypes.ITEM_STACK, new TranslatableComponent("changed_addon.jei_descriptions.iridium_use"));
            registration.addIngredientInfo(new ItemStack(ChangedAddonItems.INFORMANT_BLOCK.get()), VanillaTypes.ITEM_STACK, new TranslatableComponent("changed_addon.jei_descriptions.informant_block"));
            registration.addIngredientInfo(new ItemStack(ChangedAddonItems.LUNAR_ROSE.get()), VanillaTypes.ITEM_STACK, new TextComponent(new TranslatableComponent("changed_addon.jei_descriptions.lunar_rose").getString().replace("#", "\n")));

            ItemStack stack = new ItemStack(ChangedItems.LATEX_SYRINGE.get());
            Syringe.setVariant(stack, ChangedAddonTransfurVariants.LUMINARA_FLOWER_BEAST.get().getFormId());
            registration.addIngredientInfo(stack, VanillaTypes.ITEM_STACK, new TranslatableComponent("changed_addon.jei_descriptions.luminara.riddle"));

            addSharedDescriptions(registration, List.of(
                    ChangedAddonItems.BLUE_WOLF_CRYSTAL_FRAGMENT.get(),
                    ChangedAddonItems.ORANGE_WOLF_CRYSTAL_FRAGMENT.get(),
                    ChangedAddonItems.YELLOW_WOLF_CRYSTAL_FRAGMENT.get(),
                    ChangedAddonItems.WHITE_WOLF_CRYSTAL_FRAGMENT.get()
            ), "item.changed_addon.colorful_wolf_crystal_fragment_desc");

            // Enchant Information
            registerLatexSolventDescriptions(registration);
            registerChangedLureDescriptions(registration);
            registerTransfurAspectDescriptions(registration);
        }

        private static void registerLatexSolventDescriptions(IRecipeRegistration registration) {
            ItemStack enchantedBookWithSolvent = new ItemStack(Items.ENCHANTED_BOOK);
            for (int i = 1; i < 6; i++) { // Começa em 1 para ignorar o nível 0
                float math = LatexSolventMath(i) * 100;
                EnchantmentHelper.setEnchantments(Map.of(ChangedAddonEnchantments.LATEX_SOLVENT.get(), i), enchantedBookWithSolvent);
                String text = new TranslatableComponent("enchantment.changed_addon.latex_solvent.jei_desc", Math.round(math)).getString().replace(" T ", "% ");
                registration.addIngredientInfo(enchantedBookWithSolvent, VanillaTypes.ITEM_STACK, new TextComponent(text));
            }
        }

        private static void registerTransfurAspectDescriptions(IRecipeRegistration registration) {
            ItemStack enchantedBookWithEnchantment = new ItemStack(Items.ENCHANTED_BOOK);
            for (int i = 1; i < 6; i++) { // Começa em 1 para ignorar o nível 0
                float math = TransfurAspectMath(i);
                EnchantmentHelper.setEnchantments(Map.of(ChangedAddonEnchantments.TRANSFUR_ASPECT.get(), i), enchantedBookWithEnchantment);
                registration.addIngredientInfo(enchantedBookWithEnchantment, VanillaTypes.ITEM_STACK, new TranslatableComponent("enchantment.changed_addon.transfur_aspect.jei_desc", math));
            }
        }

        private static void registerChangedLureDescriptions(IRecipeRegistration registration) {
            ItemStack enchantedBookWithChangedLure = new ItemStack(Items.ENCHANTED_BOOK);
            for (int i = 1; i < 6; i++) { // Começa em 1 para ignorar o nível 0
                EnchantmentHelper.setEnchantments(Map.of(ChangedAddonEnchantments.CHANGED_LURE.get(), i), enchantedBookWithChangedLure);
                registration.addIngredientInfo(enchantedBookWithChangedLure, VanillaTypes.ITEM_STACK, new TranslatableComponent("enchantment.changed_addon.changed_lure.desc"));
            }
        }

        private static float LatexSolventMath(int EnchantLevel) {
            return (EnchantLevel) * 0.20f;
        }

        private static float TransfurAspectMath(int EnchantLevel) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                return TransfurAspectEnchantment.getTransfurDamage(player, null, EnchantLevel);
            }
            return 0.0f;
        }

        private static void addSharedDescriptions(IRecipeRegistration registration, List<Item> items, String translationKey) {
            items.forEach(item ->
                    registration.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM_STACK, new TranslatableComponent(translationKey))
            );
        }
    }
}


