package net.foxyas.changedaddon.procedure;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Exp9StatsProcedure {

    @SubscribeEvent
    public static void onEntityAttacked(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        execute(event, event.getSource(), entity, event.getAmount());
    }

    public static void execute(DamageSource source, LivingEntity entity, double amount) {
        execute(null, source, entity, amount);
    }

    private static void execute(Event event, DamageSource source, LivingEntity entity, double amount) {
        if (!hasKetExperiment009Form(entity)) return;

        boolean reduceDamage =
                source.isBypassInvul() ||
                        source.is(DamageTypeTags.IS_FIRE) ||
                        (source.is(DamageTypeTags.IS_FIRE) && entity.isOnFire());

        if (reduceDamage && event instanceof LivingHurtEvent hurtEvent) {
            hurtEvent.setAmount(Math.round(amount / 2f));
        }
    }

    private static boolean hasKetExperiment009Form(Entity entity) {
        return entity instanceof Player player && ProcessTransfur.getPlayerTransfurVariant(player) != null && ProcessTransfur.getPlayerTransfurVariant(player).getFormId().toString().startsWith("changed_addon:form_experiment009");
    }
}
