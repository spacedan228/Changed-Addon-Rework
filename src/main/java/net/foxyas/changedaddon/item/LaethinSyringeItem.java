package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.init.ChangedAddonSounds;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.foxyas.changedaddon.network.ChangedAddonModVariables;
import net.foxyas.changedaddon.procedures.SummonDripParticlesProcedure;
import net.foxyas.changedaddon.util.DelayedTask;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class LaethinSyringeItem extends AbstractSyringeItem implements SpecializedAnimations {

    public LaethinSyringeItem() {
        super(new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).stacksTo(64)
                .rarity(Rarity.RARE)
        );
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemstack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemstack) {
        return super.getUseDuration(itemstack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull LivingEntity entity) {
        applyEffects(world, entity);
        return onUse(itemstack, ChangedItems.SYRINGE.get().getDefaultInstance(), entity);
    }

    @Nullable
    public SpecializedAnimations.AnimationHandler getAnimationHandler() {
        return new Syringe.SyringeAnimation(this);
    }

    protected void applyEffects(Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        var playerVars = ChangedAddonModVariables.PlayerVariables.ofOrDefault(player);

        if (!ProcessTransfur.isPlayerTransfurred(player)) {
            if (playerVars.showWarns && !player.level.isClientSide())
                player.displayClientMessage(new TranslatableComponent("changedaddon.untransfur.no_effect"), true);
            return;
        }

        if (ProcessTransfur.isPlayerNotLatex(player)) {
            applyMobEffect(player, ChangedAddonMobEffects.UNTRANSFUR.get(), 1000);
            if (playerVars.showWarns && !player.level.isClientSide())
                player.displayClientMessage(new TranslatableComponent("changedaddon.untransfur.slow_effect"), true);
            return;
        }

        // Visual feedback
        SummonDripParticlesProcedure.execute(player);
        PlayerUtil.UnTransfurPlayer(player);

        // Optional: Reset advancement
        if (playerVars.resetTransfurAdvancements && player instanceof ServerPlayer sp) {
            resetAdvancement(sp, "minecraft:changed/transfur");
        }

        // Apply blindness/confusion if in survival or adventure
        if (!level.isClientSide && !player.isCreative()) {
            applyMobEffect(player, MobEffects.BLINDNESS, 40);
            applyMobEffect(player, MobEffects.CONFUSION, 60);
        }

        // Grant untransfur advancement if not already
        if (player instanceof ServerPlayer serverPlayer) {
            grantAdvancementIfNotDone(serverPlayer, "changed_addon:untransfur_advancement_2");
        }

        // Play sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(), ChangedAddonSounds.UNTRANSFUR, SoundSource.NEUTRAL, 1, 1);
    }

    protected void applyMobEffect(Player entity, MobEffect effect, int duration) {
        entity.addEffect(new MobEffectInstance(effect, duration, 0, false, false));
    }

    private void resetAdvancement(ServerPlayer player, String id) {
        Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(id));
        if (adv == null) return;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
        for (String criteria : progress.getCompletedCriteria()) {
            player.getAdvancements().revoke(adv, criteria);
        }
    }

    protected void grantAdvancementIfNotDone(ServerPlayer player, String advancementId) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(new ResourceLocation(advancementId));
        if (advancement == null) return;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(advancement, criterion);
            }
        }
    }
}
