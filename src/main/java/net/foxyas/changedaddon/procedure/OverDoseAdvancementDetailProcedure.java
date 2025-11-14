package net.foxyas.changedaddon.procedure;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class OverDoseAdvancementDetailProcedure {

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        Player player = event.getPlayer();
        Level level = player.level;
        Advancement advancement = event.getAdvancement();

        if (advancement == null) return;
        if (level.getServer() != null && level.getServer().getAdvancements().getAdvancement(ChangedAddonMod.resourceLoc("over_dose")).equals(advancement)) {
            player.hurt(new DamageSource("OverDose"), 10);
        }
    }
}
