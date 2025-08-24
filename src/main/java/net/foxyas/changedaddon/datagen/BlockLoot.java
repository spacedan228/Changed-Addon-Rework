package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.block.LuminarCrystalSmallBlock;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.advancements.critereon.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import static net.foxyas.changedaddon.init.ChangedAddonBlocks.*;

public class BlockLoot extends net.minecraft.data.loot.BlockLoot {

    public static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));

    @Override
    protected void addTables() {
        dropSelf(LATEX_INSULATOR.get());

        add(IRIDIUM_ORE.get(), createOreDrop(IRIDIUM_ORE.get(), ChangedAddonItems.RAW_IRIDIUM.get()));
        dropSelf(IRIDIUM_BLOCK.get());

        dropSelf(IRIDIUM_BLOCK.get());
        add(DEEPSLATE_PAINITE_ORE.get(), createOreDrop(DEEPSLATE_PAINITE_ORE.get(), ChangedAddonItems.PAINITE.get()));
        dropSelf(PAINITE_BLOCK.get());

        dropSelf(CATALYZER.get());
        dropSelf(ADVANCED_CATALYZER.get());

        dropSelf(UNIFUSER.get());
        dropSelf(ADVANCED_UNIFUSER.get());

        dropSelf(DARK_LATEX_PUDDLE.get());
        dropSelf(SIGNAL_BLOCK.get());
        dropSelf(INFORMANT_BLOCK.get());
        dropSelf(SNEP_PLUSH.get());
        dropSelf(WOLF_PLUSH.get());
        dropSelf(CONTAINMENT_CONTAINER.get());
        dropSelf(REINFORCED_WALL.get());
        dropSelf(REINFORCED_WALL_SILVER_STRIPED.get());
        dropSelf(REINFORCED_WALL_SILVER_TILED.get());
        dropSelf(REINFORCED_WALL_CAUTION.get());
        dropSelf(REINFORCED_CROSS_BLOCK.get());
        dropSelf(WALL_WHITE_CRACKED.get());

        add(LUMINAR_CRYSTAL_BLOCK.get(), createSilkTouchDispatchTable(LUMINAR_CRYSTAL_BLOCK.get(), LootItem.lootTableItem(ChangedAddonItems.LUMINAR_CRYSTAL_SHARD.get())
                .apply(ApplyExplosionDecay.explosionDecay())
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));

        add(LUMINAR_CRYSTAL_SMALL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(AlternativesEntry.alternatives(
                                LootItem.lootTableItem(ChangedAddonItems.LUMINAR_CRYSTAL_SMALL.get()).when(HAS_SILK_TOUCH),
                                LootItem.lootTableItem(ChangedAddonItems.LUMINAR_CRYSTAL_SHARD.get())
                                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(LUMINAR_CRYSTAL_SMALL.get())
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(LuminarCrystalSmallBlock.HEARTED, true)))
                                        .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(EntityType.PLAYER))))
                                        .apply(ApplyExplosionDecay.explosionDecay())
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                                        .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)),
                                LootItem.lootTableItem(ChangedAddonItems.LUMINAR_CRYSTAL_SHARD.get())
                                        .apply(ApplyExplosionDecay.explosionDecay())
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                        .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
                        ))
                )
        );

        add(GOO_CORE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ChangedAddonItems.GOO_CORE_FRAGMENT.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
                        )
                )
        );

        dropSelf(GENERATOR.get());
        dropOther(FOXTA_CAN.get(), ChangedAddonItems.FOXTA.get());
        dropOther(SNEPSI_CAN.get(), ChangedAddonItems.SNEPSI.get());
        dropSelf(HAND_SCANNER.get());
        dropSelf(PAWS_SCANNER.get());

        dropSelf(LUMINARA_BLOOM.get());

        add(WOLF_CRYSTAL_PILLAR.get(), createSilkTouchOnlyTable(WOLF_CRYSTAL_PILLAR.get()));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return REGISTRY.getEntries().stream().map(RegistryObject::get).filter(block -> !(block instanceof LiquidBlock)).toList();
    }
}
