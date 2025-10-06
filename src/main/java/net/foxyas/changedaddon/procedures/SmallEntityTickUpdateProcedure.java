package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.entity.advanced.DazedLatexEntity;
import net.foxyas.changedaddon.init.ChangedAddonGameRules;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SmallEntityTickUpdateProcedure {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Level level = entity.level;

        if(!level.getLevelData().getGameRules().getBoolean(ChangedAddonGameRules.DO_DAZED_LATEX_BURN)) return;

        if (entity instanceof DazedLatexEntity livEnt) {
            if (level.canSeeSkyFromBelowWater(entity.blockPosition()) && level.isDay() && !entity.isInWaterRainOrBubble()) {
                if (livEnt.getHealth() / livEnt.getMaxHealth() >= 0.4) {
                    if (livEnt.getItemBySlot(EquipmentSlot.HEAD).getItem() == Blocks.AIR.asItem()) {
                        if (!livEnt.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                            entity.setSecondsOnFire(2);
                        }
                    }
                }
            }
            return;
        }

        if (entity instanceof Player player) {
            if (ProcessTransfur.getPlayerTransfurVariant(player) != null && ProcessTransfur.getPlayerTransfurVariant(player).getFormId().toString().equals("changed_addon:form_dazed_latex")) {
                if (level.canSeeSkyFromBelowWater(entity.blockPosition()) && level.isDay() && !entity.isInWaterRainOrBubble()) {
                    if (player.getHealth() / player.getMaxHealth() >= 0.25) {
                        if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == Blocks.AIR.asItem()) {
                            if (!player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                                entity.setSecondsOnFire(2);
                            }
                        }
                    }
                }
            }
        }
    }
}
