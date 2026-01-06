package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.datagen.customData.AdvancementWriter;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRecipeTypes;
import net.ltxprogrammer.changed.recipe.InfuserRecipe;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class AdvancementProvider extends net.minecraft.data.advancements.AdvancementProvider {

    public static final String[] ADDON_FORM_RECIPES = new String[]{
            "form_avali",
            "form_biosynth_snow_leopard",
            "form_blue_lizard",
            "form_buny",
            "form_dazed_latex",
            "form_exp6",
            "form_exp_2",
            "form_experiment009",
            "form_experiment_10",
            "form_fengqi_wolf",
            "form_himalayan_crystal_gas_cat",
            "form_latex_calico_cat",
            "form_latex_cheetah",
            "form_latex_dragon_snow_leopard_shark",
            "form_latex_kitsune",
            "form_latex_snow_fox",
            "form_latex_snow_leopard_partial",
            "form_latex_white_snow_leopard",
            "form_luminara_flower_beast",
            "form_luminarctic_leopard",
            "form_lynx",
            "form_mirror_white_tiger",
            "form_puro_kind",
            "form_wolfy"
    };

    public static final String[] ADDON_FORM_RECIPES_JSON = Arrays.stream(ADDON_FORM_RECIPES).map(id -> id.endsWith(".json") ? id : id + ".json").toArray(String[]::new);

    protected final DataGenerator generator;

    public AdvancementProvider(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
        this.generator = generatorIn;
    }

    @Override
    public void run(@NotNull HashCache pCache) {
        super.run(pCache);
        writeCustomAdvancements(pCache);
    }

    @Override
    protected void registerAdvancements(@NotNull Consumer<Advancement> consumer, @NotNull ExistingFileHelper fileHelper) {
    }

    protected void writeCustomAdvancements(HashCache cache) {
        PlayerPredicate foxtaBuild = PlayerPredicate.Builder
                .player()
                .addStat(Stats.ITEM_USED.get(ChangedAddonItems.FOXTA.get()), MinMaxBounds.Ints.atLeast(100))
                .build();

        PlayerPredicate Snepsi = PlayerPredicate.Builder
                .player()
                .addStat(Stats.ITEM_USED.get(ChangedAddonItems.SNEPSI.get()), MinMaxBounds.Ints.atLeast(100))
                .build();


        Advancement.Builder foxtaBuilder = Advancement.Builder.advancement()
                .parent(new ResourceLocation("changed_addon", "drink_foxta"))
                .display(
                        ChangedAddonItems.FOXTA.get(),
                        new TranslatableComponent("advancements.foxta_addictive.title"),
                        new TranslatableComponent("advancements.foxta_addictive.descr"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "foxta_addictive",
                        new ConsumeItemTrigger.TriggerInstance(
                                EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().player(
                                        foxtaBuild
                                ).build()),
                                ItemPredicate.Builder.item().of(ChangedAddonItems.FOXTA.get()).build()
                        )
                )
                .rewards(AdvancementRewards.Builder.experience(3500).build());

        Advancement.Builder snepsiBuilder = Advancement.Builder.advancement()
                .parent(new ResourceLocation("changed_addon", "drink_snepsi"))
                .display(
                        ChangedAddonItems.SNEPSI.get(),
                        new TranslatableComponent("advancements.snepsi_addictive.title"),
                        new TranslatableComponent("advancements.snepsi_addictive.descr"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "snepsi_addictive",
                        new ConsumeItemTrigger.TriggerInstance(
                                EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().player(
                                        Snepsi
                                ).build()),
                                ItemPredicate.Builder.item().of(ChangedAddonItems.SNEPSI.get()).build()
                        )
                )
                .rewards(AdvancementRewards.Builder.experience(3500).build());


        AdvancementWriter.write(cache, generator, ResourceLocation.parse("changed_addon:snepsi_addictive"), snepsiBuilder);
        AdvancementWriter.write(cache, generator, ResourceLocation.parse("changed_addon:foxta_addictive"), foxtaBuilder);

        AdvancementRewards.Builder formsRecipes = new AdvancementRewards.Builder();
        for (String id : ADDON_FORM_RECIPES) {
            formsRecipes.addRecipe(ChangedAddonMod.resourceLoc(id));
        }



        Advancement.Builder formsRecipesGiver = Advancement.Builder.advancement()
                .rewards(formsRecipes)
                .addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ChangedItems.LATEX_BASE.get()))
                .addCriterion("has_recipe", RecipeUnlockedTrigger.unlocked(Changed.modResource("form_white_latex_wolf")));

        AdvancementWriter.write(cache,
                generator,
                Path.of("recipes", "changed_addon_forms"),
                ResourceLocation.parse("changed_addon:latex_forms"),
                formsRecipesGiver
        );
    }


    // ---------------------------------------------------------
    //  BASIC PUBLIC METHODS
    // ---------------------------------------------------------

    /**
     * Create an advancement using a builder modifier.
     */
    public void add(Consumer<Advancement> consumer, ResourceLocation id, Function<Advancement.Builder, Advancement.Builder> modifier) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        builder = modifier.apply(builder);
        builder.save(consumer, id.toString());
    }

    public void addSimpleDisplayCriterion(Consumer<Advancement> consumer, ResourceLocation id, DisplayInfo displayInfo, String criterionId, Criterion criterion) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        builder.display(displayInfo);
        builder.addCriterion(criterionId, criterion);
        builder.save(consumer, id.toString());
    }

    public void addSimpleDisplayCriterion(Consumer<Advancement> consumer, ResourceLocation id, DisplayInfo displayInfo, String criterionId, CriterionTriggerInstance criterion) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        builder.display(displayInfo);
        builder.addCriterion(criterionId, criterion);
        builder.save(consumer, id.toString());
    }

    /**
     * Create a simple advancement with title, description and icon.
     */
    public void simple(Consumer<Advancement> consumer, ResourceLocation id, String title, String description, ItemLike icon, ItemPredicate itemPredicate) {
        add(consumer, id, b -> b
                .display(
                        icon,
                        new TextComponent(title),
                        new TextComponent(description),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(itemPredicate))
        );
    }

    /**
     * Simple advancement with parent + criteria.
     */
    public void simpleWithParent(Consumer<Advancement> consumer, ResourceLocation id, ResourceLocation parent, String title, String description, ItemLike icon, ItemPredicate itemPredicate) {
        add(consumer, id, b -> b
                .parent(Advancement.Builder.advancement().build(parent)) // or fileHelper support if needed
                .display(
                        icon,
                        new TextComponent(title),
                        new TextComponent(description),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(itemPredicate))
        );
    }

    /**
     * Create a reward-only advancement (used sometimes to trigger functions).
     */
    public void reward(Consumer<Advancement> consumer, ItemLike icon, ResourceLocation id, AdvancementRewards rewards, ItemPredicate itemPredicate) {
        add(consumer, id, b -> b
                .display(
                        icon,
                        new TextComponent(id.getPath()),
                        new TextComponent(""),
                        null,
                        FrameType.CHALLENGE,
                        false,
                        false,
                        false
                )
                .rewards(rewards)
                .addCriterion("tick", InventoryChangeTrigger.TriggerInstance.hasItems(itemPredicate)) // minimal criterion
        );
    }
}
