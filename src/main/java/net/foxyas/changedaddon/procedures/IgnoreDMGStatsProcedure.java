package net.foxyas.changedaddon.procedures;

import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class IgnoreDMGStatsProcedure {

    @SubscribeEvent
    public static void onEntityAttacked(LivingAttackEvent event) {
        Entity entity = event.getEntity();
        if(!(entity instanceof Player player)) return;

        DamageSource damagesource = event.getSource();
        TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
        if (instance != null) {
            if (instance.getFormId().toString().startsWith("changed_addon:form_experiment009")) {
                if ((damagesource).getMsgId().equals(DamageSource.LIGHTNING_BOLT.getMsgId())) {
                    event.setCanceled(true);
                }
            } else if (instance.getFormId().toString().startsWith("changed_addon:form_experiment_10")) {
                if ((damagesource).getMsgId().equals(DamageSource.WITHER.getMsgId())) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
