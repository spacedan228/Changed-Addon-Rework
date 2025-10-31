package net.foxyas.changedaddon.configuration;

import net.foxyas.changedaddon.world.gamerules.ChangedEntitySpawnDressedType;
import net.minecraftforge.common.ForgeConfigSpec;

public class ChangedAddonServerConfiguration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ACCEPT_ALL_VARIANTS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DEBUFFS;
    public static final ForgeConfigSpec.ConfigValue<Double> AGE_NEED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CUSTOMRECIPES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALWAYS_INFECT;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DL_COAT_AFFECT_ALL;
    public static final ForgeConfigSpec.ConfigValue<ChangedEntitySpawnDressedType> CHANGED_SPAWN_DRESS_MODE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_SECOND_ABILITY_USE;

    static {
        BUILDER.push("Latex Totem");
        ACCEPT_ALL_VARIANTS = BUILDER.comment("allow latex totem to have all Latex Variants").define("No Latex Totem Limitations", true);
        BUILDER.pop();
        BUILDER.push("Creatures Diets");
        DEBUFFS = BUILDER.comment("Add Debuffs when eat a non good food for your kind").define("When Eat Food Debuffs", false);
        AGE_NEED = BUILDER.comment("Set Amount of Transfur Age is need to not get debuffs when eat a food that is not of your diet").define("Age Need", (double) 15000);
        BUILDER.pop();
        BUILDER.push("Blocks Recipes");
        CUSTOMRECIPES = BUILDER.comment("Allow Catalyzer and Unifuser Use New Recipe System").define("Custom Recipes", true);
        BUILDER.pop();
        BUILDER.push("Latex Infection");
        ALWAYS_INFECT = BUILDER.comment("Always Add Latex Infection").define("Always Cause Infect", false);
        BUILDER.pop();
        BUILDER.push("Beasts Behavior");
        DL_COAT_AFFECT_ALL = BUILDER.comment("When active, the Dark Latex Coat will affect all beasts").define("DL Coat Confuse All Creatures", true);
        BUILDER.push("Changed Entities");
        CHANGED_SPAWN_DRESS_MODE = BUILDER
                .comment("Defines how Changed entities spawn with clothing.",
                        "Options: NONE, NON_LATEX, LATEX, ANY")
                .worldRestart()
                .defineEnum("Changed Entities Dress Mode", ChangedEntitySpawnDressedType.ANY);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Player Handle");
        ALLOW_SECOND_ABILITY_USE = BUILDER.comment("Allow the Player to use the second selected ability (similar to offhand and main hand)").define("Allow Second Ability use", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
