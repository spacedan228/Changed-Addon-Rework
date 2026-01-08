package net.foxyas.changedaddon.init;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.world.level.GameRules.register;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedAddonGameRules {

    public static final GameRules.Key<GameRules.BooleanValue> FIGHT_TO_KEEP_CONSCIOUSNESS = register("changed_addon:fightToKeepConsciousness", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> DO_LATEX_INFECTION = register("changed_addon:doLatexInfection", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> PAINITE_GENERATION = register("changed_addon:painiteGeneration", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.IntegerValue> CHANGED_ADDON_HARD_MODE_BOSSES = register("changed_addon:bossesDifficultScale", GameRules.Category.MOBS, GameRules.IntegerValue.create(0));
    public static final GameRules.Key<GameRules.BooleanValue> DO_DAZED_LATEX_BURN = register("changed_addon:doDazedLatexBurn", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.IntegerValue> TICKS_TO_DARK_LATEX_MASK_TRANSFUR = register("changed_addon:tickToDarkLatexMaskTransfur", GameRules.Category.PLAYER, GameRules.IntegerValue.create(0));
    public static final GameRules.Key<GameRules.BooleanValue> CHANGED_ADDON_CREATURE_DIETS = register("changed_addon:doCreatureDiets", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> NEED_PERMISSION_FOR_BOSS_TRANSFUR = register("changed_addon:needPermissionForBossTransfur", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> CHANGED_ENTITIES_SPAWN_DRESSED = register("changed_addon:changedEntitiesSpawnDressed", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> DO_ALPHAS_SPAWN = register("doAlphasSpawn", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));

}
