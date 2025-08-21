package net.foxyas.changedaddon.process.features;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.entity.interfaces.CustomPatReaction;
import net.foxyas.changedaddon.entity.interfaces.SpecialPatLatex;
import net.foxyas.changedaddon.init.ChangedAddonCriteriaTriggers;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexWolf;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Objects;

public class PatFeatureHandle {

    public static boolean canPlayerPat(Player player) {
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if(variant == null) return GrabEntityAbility.getGrabber(player) == null;

        var ability = variant.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (ability != null
                && ability.suited
                && ability.grabbedHasControl) {
            return false;
        }

        return GrabEntityAbility.getGrabber(player) == null;
    }

    public static void run(Level level, Player player) {
        if (player == null || player.isSpectator() || !canPlayerPat(player)) return;

        InteractionHand emptyHand = getEmptyHand(player);
        if(emptyHand == null) return;

        EntityHitResult targetEntityResult = PlayerUtil.getEntityHitLookingAt(player, (float) player.getReachDistance(), true, e -> {
            if (e.isSpectator()) return false;
            if (!(e instanceof LivingEntity le)) return false;
            if (GrabEntityAbility.getGrabber(le) == null) return true;

            LivingEntity livingEntity = Objects.requireNonNull(GrabEntityAbility.getGrabber(le)).getEntity();
            return livingEntity != player;
        });
        if (targetEntityResult == null) return;

        Entity targetEntity = targetEntityResult.getEntity();
        if (!(targetEntity instanceof LivingEntity living)) return;


        if (targetEntity instanceof SpecialPatLatex) {
            handleSpecialEntities(player, emptyHand, living, targetEntityResult);
            return;
        }

        if (targetEntity instanceof ChangedEntity changed) {
            handleLatexEntity(player, emptyHand, changed, targetEntityResult, level);
            return;
        }

        if (targetEntity instanceof Player target) {
            handlePlayerEntity(player, emptyHand, target, targetEntityResult, level);
            return;
        }

        if (targetEntity.getType().is(ChangedAddonTags.EntityTypes.PATABLE)) {
            handlePatableEntity(player, emptyHand, targetEntityResult, level);
        }
    }

    private static void handleSpecialEntities(Player player, InteractionHand emptyHand, LivingEntity target, EntityHitResult entityHitResult) {
        player.swing(emptyHand);
        if(!(target instanceof CustomPatReaction pat)) return;

        pat.WhenPattedReaction(player, emptyHand, entityHitResult.getLocation());
        pat.WhenPattedReaction(player, emptyHand);
        pat.WhenPattedReaction();
    }

    private static void handleLatexEntity(Player player, InteractionHand emptyHand, ChangedEntity target, EntityHitResult entityHitResult, Level level) {
        player.swing(emptyHand);

        ProcessPatFeature.GlobalPatReactionEvent globalPatReactionEvent = new ProcessPatFeature.GlobalPatReactionEvent(level, player, emptyHand, target, entityHitResult.getLocation());
        if (ChangedAddonMod.postEvent(globalPatReactionEvent)) {
            return;
        }

        TransfurVariantInstance<?> selfTF = ProcessTransfur.getPlayerTransfurVariant(player);
        if (selfTF != null && selfTF.getChangedEntity() instanceof CustomPatReaction playerPat) {
            playerPat.WhenPatEvent(player, emptyHand, target);
        }

        if (target instanceof CustomPatReaction e) {
            e.WhenPattedReaction(player, emptyHand, entityHitResult.getLocation());
            e.WhenPattedReaction(player, emptyHand);
            e.WhenPattedReaction();
        }

        if (player instanceof ServerPlayer sp) GiveStealthPatAdvancement(sp, target);
    }

    private static void handlePlayerEntity(Player player, InteractionHand emptyHand, Player target, EntityHitResult entityHitResult, Level level) {
        TransfurVariantInstance<?> selfTF = ProcessTransfur.getPlayerTransfurVariant(player);
        TransfurVariantInstance<?> targetTF = ProcessTransfur.getPlayerTransfurVariant(target);

        if (selfTF == null && targetTF == null) {
            return;
        }//Be Able to Pet if at lest one is Transfur :P

        player.swing(emptyHand);

        if(selfTF != null && selfTF.getChangedEntity() instanceof CustomPatReaction playerPat){
            playerPat.WhenPatEvent(player, emptyHand, target);
        }

        if (targetTF != null && targetTF.getChangedEntity() instanceof CustomPatReaction TargetPat) {
            TargetPat.WhenPattedReaction(player, emptyHand, entityHitResult.getLocation());
            TargetPat.WhenPattedReaction(player, emptyHand);
            TargetPat.WhenPattedReaction();
            //p.displayClientMessage(new TextComponent("pat_message:" + target.getDisplayName().getString()), false);
        }

        ProcessPatFeature.GlobalPatReactionEvent globalPatReactionEvent = new ProcessPatFeature.GlobalPatReactionEvent(level, player, emptyHand, target, entityHitResult.getLocation());
        if (ChangedAddonMod.postEvent(globalPatReactionEvent)) {
            return;
        }

        if(targetTF == null || !(level instanceof ServerLevel)) return;

        //serverLevel.sendParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1, target.getZ(), 7, 0.3, 0.3, 0.3, 1);
        if(player.getRandom().nextFloat() > 0.025f + player.getLuck() * 0.01f) return;

        target.heal(6f);
        if(player instanceof ServerPlayer sPlayer) GivePatAdvancement(sPlayer, target);
    }

    private static void handlePatableEntity(Player player, InteractionHand emptyHand, EntityHitResult entityHitResult, Level level) {
        Entity target = entityHitResult.getEntity();
        player.swing(emptyHand);

        if (level instanceof ServerLevel serverLevel) {
            player.displayClientMessage(new TranslatableComponent("key.changed_addon.pat_message", target.getDisplayName().getString()), true);
            serverLevel.sendParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1, target.getZ(), 7, 0.3, 0.3, 0.3, 1);
        }
    }

    private static InteractionHand getEmptyHand(Player player){
        if(player.getMainHandItem().isEmpty()) return InteractionHand.MAIN_HAND;

        return player.getOffhandItem().isEmpty() ? InteractionHand.OFF_HAND : null;
    }

    public static boolean shouldBeConfused(Player player, ChangedEntity entity) {
        if (entity instanceof AbstractDarkLatexWolf) {
            // Verificando se o jogador usa a armadura correta
            return player.getItemBySlot(EquipmentSlot.HEAD).is(ChangedAddonItems.DARK_LATEX_HEAD_CAP.get())
                    && player.getItemBySlot(EquipmentSlot.CHEST).is(ChangedAddonItems.DARK_LATEX_COAT.get());
        }
        return false;
    }

    public static void GivePatAdvancement(ServerPlayer player, Entity target) {
        ChangedAddonCriteriaTriggers.PAT_ENTITY_TRIGGER.Trigger(player, target, "chance");
    }

    public static void GiveStealthPatAdvancement(ServerPlayer player, Entity target) {
        ChangedAddonCriteriaTriggers.PAT_ENTITY_TRIGGER.Trigger(player, target, "stealth");
    }
}
