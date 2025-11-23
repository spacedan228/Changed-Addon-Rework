package net.foxyas.changedaddon.datagen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class LootTableProvider extends net.minecraft.data.loot.LootTableProvider {

    public LootTableProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void validate(@NotNull Map<ResourceLocation, LootTable> map, @NotNull ValidationContext validationtracker) {
        map.forEach((id, lootTable) -> lootTable.validate(validationtracker));
    }

    @Override
    public @NotNull List<SubProviderEntry> getTables() {
        return List.of(
                new SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(EntityLoot::new, LootContextParamSets.ENTITY)
        );
    }
}
