package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.advancements.critereon.UsedItemAmountTrigger;
import net.foxyas.changedaddon.datagen.customData.AdvancementWriter;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class AdvancementProvider extends net.minecraft.data.advancements.AdvancementProvider {

    protected final PackOutput output;

    public AdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper fileHelperIn) {
        super(output, lookup, List.of(AdvancementProvider::generate));
        this.output = output;
    }

    private static void generate(HolderLookup.@NotNull Provider lookup, @NotNull Consumer<Advancement> out) {

    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        CompletableFuture<?> normalAdvancements = super.run(pOutput);
        CompletableFuture<?> customAdvancements = writeCustomAdvancements(pOutput);
        return CompletableFuture.allOf(normalAdvancements, customAdvancements);
    }

    protected CompletableFuture<?> writeCustomAdvancements(CachedOutput cache) {
        Advancement.Builder foxtaBuilder = Advancement.Builder.advancement()
                .parent(ResourceLocation.fromNamespaceAndPath("changed_addon", "drink_foxta"))
                .display(
                        ChangedAddonItems.FOXTA.get(),
                        Component.translatable("advancements.foxta_addictive.title"),
                        Component.translatable("advancements.foxta_addictive.descr"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "foxta_addictive",
                        new UsedItemAmountTrigger.Instance(ContextAwarePredicate.ANY, ChangedAddonItems.FOXTA.get(), 100, null)
                )
                .rewards(AdvancementRewards.Builder.experience(3500).build());

        Advancement.Builder snepsiBuilder = Advancement.Builder.advancement()
                .parent(ResourceLocation.fromNamespaceAndPath("changed_addon", "drink_snepsi"))
                .display(
                        ChangedAddonItems.SNEPSI.get(),
                        Component.translatable("advancements.snepsi_addictive.title"),
                        Component.translatable("advancements.snepsi_addictive.descr"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "snepsi_addictive",
                        new UsedItemAmountTrigger.Instance(ContextAwarePredicate.ANY, ChangedAddonItems.SNEPSI.get(), 100, null)
                )
                .rewards(AdvancementRewards.Builder.experience(3500).build());


        CompletableFuture<?> snepsi = AdvancementWriter.write(cache, output, ResourceLocation.parse("changed_addon:snepsi_addictive"), snepsiBuilder);
        CompletableFuture<?> foxta = AdvancementWriter.write(cache, output, ResourceLocation.parse("changed_addon:foxta_addictive"), foxtaBuilder);

        return CompletableFuture.allOf(snepsi, foxta);
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
