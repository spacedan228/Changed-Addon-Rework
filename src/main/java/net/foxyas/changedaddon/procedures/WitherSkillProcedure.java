package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.entity.bosses.Experiment10BossEntity;
import net.foxyas.changedaddon.entity.bosses.Experiment10Entity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class WitherSkillProcedure {
    @SubscribeEvent
    public static void onEntityAttacked(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        if (entity != null) {
            execute(event, entity, event.getSource().getDirectEntity());
        }
    }

    public static void execute(Entity entity, Entity attacker) {
        execute(null, entity, attacker);
    }

    private static void execute(@Nullable Event event, Entity entity, Entity attacker) {
        if (!(entity instanceof LivingEntity target)) return;
        if (attacker == null) return;

        int amplifier = -1;

        if (attacker instanceof Experiment10Entity e10) {
            if (e10.getMainHandItem().isEmpty()) {
                amplifier = e10.isPhase2() ? 2 : 0;
            }
        } else if (attacker instanceof Experiment10BossEntity e10Boss) {
            if (e10Boss.getMainHandItem().isEmpty()) {
                amplifier = e10Boss.isPhase2() ? 2 : 0;
            }
        }

        if (amplifier >= 0 && !target.getLevel().isClientSide()) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 90, amplifier, false, true));
        }

        if (target instanceof Player player) {
            TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
            if (instance != null && instance.getFormId().toString().equals("changed_addon:form_experiment_10")) {
                if (attacker instanceof LivingEntity living && living.getMainHandItem().isEmpty()) {
                    if (!player.getLevel().isClientSide()) {
                        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 90, 0, false, true));
                    }
                }
            }
        }
    }
}
