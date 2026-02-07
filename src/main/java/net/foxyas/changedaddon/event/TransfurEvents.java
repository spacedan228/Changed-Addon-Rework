package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.entity.advanced.DazedLatexEntity;
import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.foxyas.changedaddon.entity.simple.DarkLatexYufengQueenEntity;
import net.foxyas.changedaddon.init.ChangedAddonGameRules;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants;
import net.foxyas.changedaddon.variant.TransfurVariantInstanceExtensor;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.foxyas.changedaddon.event.TransfurVariantEvents.OverrideSourceTransfurVariantEvent.TransfurType;

@Mod.EventBusSubscriber
public class TransfurEvents {

    @SubscribeEvent
    public static void WhenTransfured(ProcessTransfur.EntityVariantAssigned changedVariantEvent) {
        TransfurVariant<?> variant = changedVariantEvent.originalVariant;
        if (variant == null) return;

        LivingEntity entity = changedVariantEvent.livingEntity;
        if (!entity.level.getLevelData().getGameRules().getBoolean(ChangedAddonGameRules.NEED_PERMISSION_FOR_BOSS_TRANSFUR))
            return;

        if (variant.is(ChangedAddonTransfurVariants.EXPERIMENT_009_BOSS) && !getPlayerVars(entity).Exp009TransfurAllowed) {
            changedVariantEvent.variant = ChangedAddonTransfurVariants.EXPERIMENT_009.get();
        }
        if (variant.is(ChangedAddonTransfurVariants.EXPERIMENT_10_BOSS) && !getPlayerVars(entity).Exp10TransfurAllowed) {
            changedVariantEvent.variant = ChangedAddonTransfurVariants.EXPERIMENT_10.get();
        }
    }


    @SubscribeEvent
    public static void WhenTransfuredByAlpha(ProgressTransfurEvents.NewlyTransfurred changedVariantEvent) {
        TransfurVariantInstance<?> transfurVariantInstance = changedVariantEvent.getTransfurVariantInstance();
        if (transfurVariantInstance != null) {
            TransfurContext transfurContext = transfurVariantInstance.transfurContext;
            IAbstractChangedEntity source = transfurContext.source;
            if (source != null) {
                boolean wantAbsorption = source.wantAbsorption();
                if (wantAbsorption && transfurContext.cause != TransfurCause.GRAB_REPLICATE) {
                    if (source.getEntity() instanceof IAlphaAbleEntity alphaAbleEntity && transfurVariantInstance.getChangedEntity() instanceof IAlphaAbleEntity iAlphaAble) {
                        iAlphaAble.setAlpha(alphaAbleEntity.isAlpha());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void WhenKilledAfterTransfuredByAlpha(TransfurVariantEvents.SpawnAtTransfurredEntityEvent spawnAtTransfurredEntityEvent) {
        LivingEntity toReplace = spawnAtTransfurredEntityEvent.spawnAt;
        ChangedEntity source = spawnAtTransfurredEntityEvent.changedEntity;
        if (resolveChangedEntity(toReplace) instanceof IAlphaAbleEntity toReplaceAlpha) {
            if (source instanceof IAlphaAbleEntity alphaSource) {
                alphaSource.setAlpha(toReplaceAlpha.isAlpha());
            }
        }
    }
//
//    @SubscribeEvent
//    public static void AfterPlayerTransfur(ProgressTransfurEvents.onPostProcessPlayerTransfur onPostProcessPlayerTransfur) {
//        onPostProcessPlayerTransfur.setCanceled(true);
//        onPostProcessPlayerTransfur.callDefault();
//        ChangedEntity changedEntity = onPostProcessPlayerTransfur.getTransfurVariantInstance().getChangedEntity();
//        if (changedEntity instanceof IAlphaAbleEntity iAlphaAbleEntity) {
//            iAlphaAbleEntity.setAlpha(true);
//            iAlphaAbleEntity.setAlphaScale(2);
//        }
//    }


    @SubscribeEvent
    public static void ModifyAbsorptionVariant(TransfurVariantEvents.OverrideSourceTransfurVariantEvent event) {
        TransfurVariant<?> original = event.getOriginal();
        ChangedEntity changedEntity = event.getChangedEntity();
        IAbstractChangedEntity source = event.getSource();

        if (!source.wantAbsorption()) return;

        if (source.getChangedEntity() instanceof DarkLatexYufengQueenEntity latexYufengQueenEntity) {
            TransfurVariant<?> selfVariant = latexYufengQueenEntity.getSelfVariant();
            if (original != selfVariant) {
                event.setVariant(selfVariant);
            }
        } else if (changedEntity instanceof DarkLatexYufengQueenEntity latexYufengQueenEntity) {
            TransfurVariant<?> selfVariant = latexYufengQueenEntity.getSelfVariant();
            if (original != selfVariant) {
                event.setVariant(selfVariant);
            }
        }
    }

    @SubscribeEvent
    public static void makeDazedLatexBuffAfterGrabAssimilation(TransfurVariantEvents.OverrideSourceTransfurVariantEvent event) {
        LivingEntity target = event.getTarget();
        IAbstractChangedEntity source = event.getSource();
        if (!(source.getChangedEntity() instanceof DazedLatexEntity) return;
        
        if (event.getTransfurType() == TransfurType.ABSORPTION) {
            source.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get()).ifPresent((grabEntityAbilityInstance) -> {
                if (grabEntityAbilityInstance.grabbedEntity == target) {
                    event.setVariant(ChangedAddonTransfurVariants.BUFF_DAZED_LATEX.get());
                }
            });
        }
    }

    public static Entity resolveChangedEntity(Entity entity) {
        if (entity instanceof Player player) {
            TransfurVariantInstance<?> transfur = ProcessTransfur.getPlayerTransfurVariant(player);
            if (transfur != null) {
                return transfur.getChangedEntity();
            }
        }
        return entity;
    }


    @SubscribeEvent
    public static void CancelUntransfur(UntransfurEvent untransfurEvent) {
        Player player = untransfurEvent.getPlayer();
        if (ProcessTransfur.getPlayerTransfurVariant(player) instanceof TransfurVariantInstanceExtensor transfurVariantInstanceExtensor) {
            untransfurEvent.setCanceled(transfurVariantInstanceExtensor.getUntransfurImmunity(untransfurEvent.untransfurType));
        }
    }

    public static ChangedAddonVariables.PlayerVariables getPlayerVars(LivingEntity entity) {
        return entity.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ChangedAddonVariables.PlayerVariables());
    }
}
