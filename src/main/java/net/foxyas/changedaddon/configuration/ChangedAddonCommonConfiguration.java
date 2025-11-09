package net.foxyas.changedaddon.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class ChangedAddonCommonConfiguration {

    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        SPEC = BUILDER.build();
    }
}
