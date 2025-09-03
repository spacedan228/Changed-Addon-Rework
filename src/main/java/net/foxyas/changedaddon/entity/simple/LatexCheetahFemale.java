package net.foxyas.changedaddon.entity.simple;

import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.util.ColorUtil;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.AbstractSnowLeopard;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedMobCategories;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;


@Mod.EventBusSubscriber
public class LatexCheetahFemale extends AbstractSnowLeopard {
    private static final Set<ResourceLocation> SPAWN_BIOMES = Set.of(
            Biomes.JUNGLE.location(),
            Biomes.SPARSE_JUNGLE.location(),
            Biomes.SAVANNA.location(),
            Biomes.SAVANNA_PLATEAU.location(),
            Biomes.WINDSWEPT_SAVANNA.location(),
            Biomes.DARK_FOREST.location()
    );


    public LatexCheetahFemale(EntityType<? extends LatexCheetahFemale> entityType, Level level) {
        super(entityType, level);
    }

    @SubscribeEvent
    public static void addLivingEntityToBiomes(BiomeLoadingEvent event) {
        if (SPAWN_BIOMES.contains(event.getName())) {
            event.getSpawns().getSpawner(ChangedMobCategories.CHANGED)
                    .add(new MobSpawnSettings.SpawnerData(ChangedAddonEntities.LATEX_CHEETAH_FEMALE.get(), 20, 1, 4));
        }
    }

    public static void init() {
        SpawnPlacements.register(ChangedAddonEntities.LATEX_CHEETAH_FEMALE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.4F);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.9);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(20.0F);
        attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get()).setBaseValue(2.5);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        Color3 firstColor = Color3.getColor("#d8b270");
        Color3 secondColor = Color3.getColor("#634927");
        Color3 thirdColor = Color3.getColor("#ecddc1");

        float progress = ColorUtil.getPlayerTransfurProgressSafe(this.getUnderlyingPlayer(), 1);
        progress = Mth.clamp(progress, 0.0f, 1.0f);

        if (progress < 0.5f) {
            // 0.0 → 0.5 → vai de first → second
            float t = progress / 0.5f; // normaliza para 0–1
            return ColorUtil.lerpTFColor(firstColor, secondColor, t);
        } else {
            // 0.5 → 1.0 → vai de second → third
            float t = (progress - 0.5f) / 0.5f; // normaliza para 0–1
            return ColorUtil.lerpTFColor(secondColor, thirdColor, t);
        }
    }


    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public Gender getGender() {
        return null;
    }
}