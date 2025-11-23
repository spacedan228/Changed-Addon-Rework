package net.foxyas.changedaddon.datagen;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class AdvancementProvider extends net.minecraft.data.advancements.AdvancementProvider {

    public AdvancementProvider(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(@NotNull Consumer<Advancement> consumer, @NotNull ExistingFileHelper fileHelper) {

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
                        Component.literal(title),
                        Component.literal(description),
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
                        Component.literal(title),
                        Component.literal(description),
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
                        Component.literal(id.getPath()),
                        Component.literal(""),
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
