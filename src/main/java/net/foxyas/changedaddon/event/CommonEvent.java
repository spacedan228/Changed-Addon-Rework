package net.foxyas.changedaddon.event;

import com.mojang.brigadier.CommandDispatcher;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.command.ChangedAddonAdminCommand;
import net.foxyas.changedaddon.command.ChangedAddonCommandRootCommand;
import net.foxyas.changedaddon.command.TransfurMe;
import net.foxyas.changedaddon.init.ChangedAddonAttributes;
import net.foxyas.changedaddon.init.ChangedAddonGameRules;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.util.TransfurVariantUtils;
import net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID)
public class CommonEvent {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ChangedAddonAdminCommand.register(dispatcher);
        ChangedAddonCommandRootCommand.register(dispatcher);
        TransfurMe.register(dispatcher);
    }

    @SubscribeEvent
    public static void persistAttributes(PlayerEvent.Clone event) {
        Player oldP = event.getOriginal();
        Player newP = (Player) event.getEntity();
        newP.getAttribute(ChangedAddonAttributes.LATEX_RESISTANCE.get()).setBaseValue(oldP.getAttribute(ChangedAddonAttributes.LATEX_RESISTANCE.get()).getBaseValue());
        newP.getAttribute(ChangedAddonAttributes.LATEX_INFECTION.get()).setBaseValue(oldP.getAttribute(ChangedAddonAttributes.LATEX_INFECTION.get()).getBaseValue());
    }

    //Var sync
    @SubscribeEvent
    public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getPlayer();
        if (!player.level.isClientSide())
            ChangedAddonVariables.ofOrDefault(player).syncPlayerVariables(player);
    }

    @SubscribeEvent
    public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!player.level.isClientSide())
            ChangedAddonVariables.ofOrDefault(player).syncPlayerVariables(player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getPlayer();
        if (!player.level.isClientSide())
            ChangedAddonVariables.ofOrDefault(player).syncPlayerVariables(player);
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        Player originalPl = event.getOriginal();
        originalPl.reviveCaps();
        ChangedAddonVariables.PlayerVariables original = ChangedAddonVariables.ofOrDefault(originalPl);
        originalPl.invalidateCaps();

        ChangedAddonVariables.PlayerVariables clone = ChangedAddonVariables.ofOrDefault(event.getPlayer());
        original.copyTo(clone, event.isWasDeath());
    }
    //

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (!player.isAlive()) return;

        maskTransfur(player, player.level);

        tickUntransfur(player);

        triggerSwimRegret(player);
    }

    @SubscribeEvent
    public static void onFarmlandTrampleWhenTransfured(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity() instanceof Player player) {
            TransfurVariantInstance<?> transfurVariant = ProcessTransfur.getPlayerTransfurVariant(player);
            if (transfurVariant != null && transfurVariant.is(ChangedAddonTransfurVariants.PROTOTYPE)) {
                event.setCanceled(true);
            }
            /* Todo?
                maybe make the player only trample the dirt if the entity base of the variant can trample?
            else if (transfurVariant != null) {
                BlockState state = event.getState();
                BlockPos pos = event.getPos();
                float fallDistance = event.getFallDistance();
                event.setCanceled(!(transfurVariant.getChangedEntity().canTrample(state, pos, fallDistance)));
            }*/
        }
    }

    @SubscribeEvent
    public static void onPlayerProgressTransfurTick(ProgressTransfurEvents.TickPlayerTransfurProgressEvent tickPlayerTransfurProgressEvent) {
        tickInfectionAndRes(tickPlayerTransfurProgressEvent);
    }

    private static void maskTransfur(Player player, Level level) {
        int doTransfur = level.getLevelData().getGameRules().getInt(ChangedAddonGameRules.TICKS_TO_DARK_LATEX_MASK_TRANSFUR);
        if (doTransfur <= 0) return;
        if (player.isCreative() || player.isSpectator()) return;

        if (!player.getPersistentData().contains("HoldingDarkLatexMask")) {
            player.getPersistentData().putInt("HoldingDarkLatexMask", 0);
        }

        int maskHeldTimer = player.getPersistentData().getInt("HoldingDarkLatexMask");
        if (ProcessTransfur.isPlayerTransfurred(player)) {
            if (maskHeldTimer > 0) {
                player.getPersistentData().putInt("HoldingDarkLatexMask", maskHeldTimer - 1);
            } else {
                player.getPersistentData().remove("HoldingDarkLatexMask");
            }
            return;
        }

        InteractionHand maskHand = null;
        if (player.getMainHandItem().is(ChangedItems.DARK_LATEX_MASK.get())) maskHand = InteractionHand.MAIN_HAND;
        if (maskHand == null && player.getOffhandItem().is(ChangedItems.DARK_LATEX_MASK.get()))
            maskHand = InteractionHand.OFF_HAND;

        if (maskHand == null) {
            if (maskHeldTimer > 0) {
                player.getPersistentData().putDouble("HoldingDarkLatexMask", maskHeldTimer - 1);
            } else {
                player.getPersistentData().remove("HoldingDarkLatexMask");
            }
            return;
        }

        if (maskHeldTimer < doTransfur) {
            player.getPersistentData().putInt("HoldingDarkLatexMask", maskHeldTimer + 1);
            return;
        }

        ItemStack stack = player.getItemInHand(maskHand);
        stack.shrink(1);
        player.getInventory().setChanged();


        if (ProcessTransfur.progressTransfur(player, (float) ProcessTransfur.getEntityTransfurTolerance(player) * 2, Syringe.getVariant(stack), TransfurContext.hazard(TransfurCause.GRAB_REPLICATE))) {
            TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
            if (instance != null) {
                ChangedSounds.broadcastSound(player, instance.getParent().sound, 1, 1);
            }
        }

        player.getPersistentData().putInt("HoldingDarkLatexMask", 0);
        player.getPersistentData().remove("HoldingDarkLatexMask");
    }

    private static void tickInfectionAndRes(ProgressTransfurEvents.TickPlayerTransfurProgressEvent event) {
        Player player = event.getPlayer();
        if (ProcessTransfur.isPlayerTransfurred(player)) return;

        float progress = ProcessTransfur.getPlayerTransfurProgress(player);
        if (progress < 0) return;
        float newProgress = progress;

        float latexRes = (float) player.getAttributeValue(ChangedAddonAttributes.LATEX_RESISTANCE.get());
        float infection = (float) player.getAttributeValue(ChangedAddonAttributes.LATEX_INFECTION.get());
        float tolerance = (float) ProcessTransfur.getEntityTransfurTolerance(player);

        boolean infectionWins = infection > latexRes;
        boolean resistanceWins = latexRes >= infection;


        // --- Resistance Wins
        if (resistanceWins) {
            newProgress -= 0.5f * latexRes;
        }

        // --- Infection Wins
        else if (infectionWins) {
            newProgress += progress * (infection / 50f);

            // Block the natural Tick
            event.setCanceled(true);
        }

        if (player.tickCount % 20 == 0) { // only process after 1 second
            if (!player.isCreative() && !player.isSpectator()) {

                newProgress = Mth.clamp(newProgress, 0f, tolerance * 0.998f);

                // Apply only if there is chances
                if (newProgress != progress) {
                    ProcessTransfur.setPlayerTransfurProgress(player, newProgress);
                }
            }
        }
    }


    private static void tickUntransfur(Player player) {
        ChangedAddonVariables.PlayerVariables vars = ChangedAddonVariables.of(player);
        if (vars == null) return;

        if (!player.hasEffect(ChangedAddonMobEffects.UNTRANSFUR.get())) {
            if (vars.untransfurProgress > 0) {
                vars.untransfurProgress -= .1f;
                vars.syncPlayerVariables(player);
            }
            return;
        }

        if (!ProcessTransfur.isPlayerTransfurred(player)) return;

        if (vars.untransfurProgress < 0) {
            vars.untransfurProgress = 0;
        } else {
            vars.untransfurProgress += (ProcessTransfur.isPlayerNotLatex(player) ? 0.1 : 0.2);

            if (player.isSleeping()) vars.untransfurProgress += .5f;
        }
        vars.syncPlayerVariables(player);
    }

    private static void triggerSwimRegret(Player player) {
        if (player.level.isClientSide || !ProcessTransfur.isPlayerTransfurred(player)) return;
        CompoundTag playerData = player.getPersistentData();
        if (playerData.contains("TransfurData")) {
            int ticks = playerData.getCompound("TransfurData").getInt("SlowSwimInWaterTicks");
            if (TransfurVariantUtils.GetSwimSpeed(ProcessTransfur.getPlayerTransfurVariant(player).getParent(), player) > 0.95) {
                if (ticks != 0) {
                    playerData.getCompound("TransfurData").putInt("SlowSwimInWaterTicks", 0);
                }
                return;
            }

            if (ticks == -1) return;

            if (player.isSwimming() && player.isInWaterOrBubble()) {
                ticks++;
            }

            if (ticks >= 600) {
                ServerPlayer sPlayer = (ServerPlayer) player;
                Advancement _adv = sPlayer.server.getAdvancements().getAdvancement(ChangedAddonMod.resourceLoc("swim_regret"));
                AdvancementProgress _ap = sPlayer.getAdvancements().getOrStartProgress(_adv);
                if (!_ap.isDone()) {
                    for (String s : _ap.getRemainingCriteria()) sPlayer.getAdvancements().award(_adv, s);
                }
                ticks = -1;
            }

            playerData.getCompound("TransfurData").putInt("SlowSwimInWaterTicks", ticks);
        } else {
            if (TransfurVariantUtils.GetSwimSpeed(ProcessTransfur.getPlayerTransfurVariant(player).getParent(), player) > 0.95) {
                if (playerData.contains("TransfurData")) {
                    playerData.remove("TransfurData");
                }
            } else {
                if (!playerData.contains("TransfurData")) {
                    CompoundTag tag = new CompoundTag();
                    tag.putInt("SlowSwimInWaterTicks", 0);
                    playerData.put("TransfurData", tag);
                }
            }
        }
    }
}
