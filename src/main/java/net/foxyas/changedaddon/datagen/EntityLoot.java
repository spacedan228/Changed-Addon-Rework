package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.storage.loot.LootPool.lootPool;

public class EntityLoot extends net.minecraft.data.loot.EntityLoot {

    public EntityLoot() {
        super();
    }

    @Override
    protected void addTables() {

    }

    @Override
    protected @NotNull Iterable<EntityType<?>> getKnownEntities() {
        return ChangedAddonEntities.REGISTRY.getEntries().stream().<EntityType<?>>map(RegistryObject::get).toList();
    }

    /** 🔧 Método utilitário para simplificar a criação dos pools */
    private LootPool.Builder pool(ItemLike item, float min, float max, float lootingMin, float lootingMax) {
        return lootPool()
                .setRolls(UniformGenerator.between(1, 1))
                .add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(lootingMin, lootingMax)))
                );
    }
}
