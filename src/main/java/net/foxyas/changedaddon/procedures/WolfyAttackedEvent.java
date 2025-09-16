package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WolfyAttackedEvent {

    @SubscribeEvent
    public static void onEntityAttacked(LivingAttackEvent event) {
        Entity entity = event.getEntity();
        if(!(entity instanceof Player player)) return;

        TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
        if(instance == null || !instance.is(ChangedAddonTransfurVariants.WOLFY)) return;

        DamageSource damagesource = event.getSource();
        if (damagesource instanceof EntityDamageSource _entityDamageSource && _entityDamageSource.isThorns()) {
            event.setCanceled(true);
            return;
        }

        if (damagesource.isFire()) {
            event.setCanceled(true);
            return;
        }

        if (damagesource.isExplosion()) {
            event.setCanceled(true);
            return;
        }

        if (damagesource == DamageSource.LIGHTNING_BOLT) {
            event.setCanceled(true);
        }
    }
}
