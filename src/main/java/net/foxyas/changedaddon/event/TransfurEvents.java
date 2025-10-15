package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.init.ChangedAddonGameRules;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TransfurEvents {

    @SubscribeEvent
    public static void WhenTransfured(ProcessTransfur.EntityVariantAssigned changedVariantEvent) {
        LivingEntity entity = changedVariantEvent.livingEntity;
        Level world = changedVariantEvent.livingEntity.getLevel();
        TransfurVariant<?> oldVariant = changedVariantEvent.previousVariant;
        TransfurVariant<?> variant = changedVariantEvent.originalVariant;
        boolean isTransfurred = variant != null;
        if (isTransfurred) {
            String formId = variant.getFormId().toString();
            boolean isForm009Boss = formId.contains("changed_addon:form_experiment009_boss")
                    || variant.is(ChangedAddonTransfurVariants.EXPERIMENT_009_BOSS);

            boolean isForm010Boss = formId.equals("changed_addon:form_experiment_10_boss")
                    || variant.is(ChangedAddonTransfurVariants.EXPERIMENT_10_BOSS);

            if (world.getLevelData().getGameRules().getBoolean(ChangedAddonGameRules.NEED_PERMISSION_FOR_BOSS_TRANSFUR)) {
                if (isForm009Boss && !getPlayerVars(entity).Exp009TransfurAllowed) {
                    changedVariantEvent.variant = ChangedAddonTransfurVariants.EXPERIMENT_009.get();
                }
                if (isForm010Boss && !getPlayerVars(entity).Exp10TransfurAllowed) {
                    changedVariantEvent.variant = ChangedAddonTransfurVariants.EXPERIMENT_10.get();
                }
            }
        }
    }

    public static ChangedAddonVariables.PlayerVariables getPlayerVars(LivingEntity entity) {
        return entity.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ChangedAddonVariables.PlayerVariables());
    }
}
