package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedAddonGameRules {

    public static final GameRules.Key<GameRules.BooleanValue> FIGHT_TO_KEEP_CONSCIOUSNESS = register("fightToKeepConsciousness", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> DO_LATEX_INFECTION = register("doLatexInfection", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> PAINITE_GENERATION = register("painiteGeneration", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.IntegerValue> CHANGED_ADDON_HARD_MODE_BOSSES = register("bossesDifficultScale", GameRules.Category.MOBS, GameRules.IntegerValue.create(0));
    public static final GameRules.Key<GameRules.BooleanValue> DO_DAZED_LATEX_BURN = register("doDazedLatexBurn", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.IntegerValue> TICKS_TO_DARK_LATEX_MASK_TRANSFUR = register("tickToDarkLatexMaskTransfur", GameRules.Category.PLAYER, GameRules.IntegerValue.create(0));
    public static final GameRules.Key<GameRules.BooleanValue> CHANGED_ADDON_CREATURE_DIETS = register("doCreatureDiets", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> NEED_PERMISSION_FOR_BOSS_TRANSFUR = register("needPermissionForBossTransfur", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> NEED_FULL_SOURCE_TO_SPREAD = register("blocksNeedFullSourceToSpread", GameRules.Category.MISC, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> CHANGED_ENTITIES_SPAWN_DRESSED = register("changedEntitiesSpawnDressed", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));

    private static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> value){
        return GameRules.register(ChangedAddonMod.resourceLocStringStyle(name), category, value);
    }
}
