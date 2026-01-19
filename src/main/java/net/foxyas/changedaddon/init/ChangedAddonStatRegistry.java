package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedAddonStatRegistry {

    public static final ResourceLocation PATS_GIVEN = ChangedAddonMod.resourceLoc("pats_given");
    public static final ResourceLocation PATS_RECEIVED = ChangedAddonMod.resourceLoc("pats_received");

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerCustomStats(RegistryEvent.Register<StatType<?>> event) {
        registerCustomStat(PATS_GIVEN, StatFormatter.DEFAULT);
        registerCustomStat(PATS_RECEIVED, StatFormatter.DEFAULT);
    }

    private static ResourceLocation registerCustomStat(ResourceLocation resLoc, StatFormatter statFormatter) {
        Registry.register(Registry.CUSTOM_STAT, resLoc, resLoc);
        Stats.CUSTOM.get(resLoc, statFormatter);
        return resLoc;
    }
}
