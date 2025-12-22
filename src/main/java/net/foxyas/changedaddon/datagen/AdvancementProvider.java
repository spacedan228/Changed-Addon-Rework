package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.datagen.customData.AdvancementWriter;
import net.foxyas.changedaddon.init.ChangedAddonItems;
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

import java.util.function.Consumer;
import java.util.function.Function;

public class AdvancementProvider extends net.minecraft.data.advancements.AdvancementProvider {

    protected final DataGenerator generator;

    public AdvancementProvider(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
        this.generator = generatorIn;
    }

    @Override
    public void run(HashCache pCache) {
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
