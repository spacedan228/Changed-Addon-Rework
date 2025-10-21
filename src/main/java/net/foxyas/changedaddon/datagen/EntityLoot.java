package net.foxyas.changedaddon.datagen;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.foxyas.changedaddon.init.ChangedAddonEntities.EntitiesWithLoot;
import static net.minecraft.world.level.storage.loot.LootPool.lootPool;

public class EntityLoot extends net.minecraft.data.loot.EntityLoot {

    public EntityLoot() {
        super();
    }

    @Override
    protected void addTables() {
        EntitiesWithLoot.forEach((supplierBuilderPair -> add(supplierBuilderPair.getFirst().get(), supplierBuilderPair.getSecond().get())));
    }


    @Override
    protected @NotNull Iterable<EntityType<?>> getKnownEntities() {
        List<EntityType<?>> list = new ArrayList<>();
        EntitiesWithLoot.forEach((supplierBuilderPair) -> list.add(supplierBuilderPair.getFirst().get()));
        return list;
    }

    /**
     * 🔧 Método utilitário para simplificar a criação dos pools
     */
    private LootPool.Builder pool(ItemLike item, float min, float max, float lootingMin, float lootingMax) {
        return lootPool()
                .setRolls(UniformGenerator.between(1, 1))
                .add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(lootingMin, lootingMax)))
                );
    }
}
