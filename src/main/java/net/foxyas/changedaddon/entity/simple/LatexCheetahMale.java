package net.foxyas.changedaddon.entity.simple;

import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.init.ChangedMobCategories;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;


@Mod.EventBusSubscriber
public class LatexCheetahMale extends AbstractCheetahEntity {
    private static final Set<ResourceLocation> SPAWN_BIOMES = Set.of(
            Biomes.JUNGLE.location(),
            Biomes.SPARSE_JUNGLE.location(),
            Biomes.SAVANNA.location(),
            Biomes.SAVANNA_PLATEAU.location(),
            Biomes.WINDSWEPT_SAVANNA.location(),
            Biomes.DARK_FOREST.location()
    );


    public LatexCheetahMale(EntityType<? extends LatexCheetahMale> entityType, Level level) {
        super(entityType, level);
    }

    @SubscribeEvent
    public static void addLivingEntityToBiomes(BiomeLoadingEvent event) {
        if (SPAWN_BIOMES.contains(event.getName())) {
            event.getSpawns().getSpawner(ChangedMobCategories.CHANGED)
                    .add(new MobSpawnSettings.SpawnerData(ChangedAddonEntities.LATEX_CHEETAH_MALE.get(), 20, 1, 4));
        }
    }

    public static void init() {
        SpawnPlacements.register(ChangedAddonEntities.LATEX_CHEETAH_MALE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
    }

    @Override
    public Gender getGender() {
        return Gender.MALE;
    }
}