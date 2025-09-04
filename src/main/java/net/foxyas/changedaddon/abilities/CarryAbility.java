package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbility;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexCentaur;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

public class CarryAbility extends SimpleAbility {
    public CarryAbility() {
        super();
    }

    public static boolean Spectator(Entity entity) {
        return entity instanceof Player player && player.isSpectator();
    }

    public static void SafeRemove(Entity mainEntity) {
        if (mainEntity.getFirstPassenger() != null) {
            mainEntity.getFirstPassenger().stopRiding();
            broadcastPassengers(mainEntity.getFirstPassenger());
        }
    }

    @Override
    public TranslatableComponent getAbilityName(IAbstractChangedEntity entity) {
        return new TranslatableComponent("changed_addon.ability.carry");
    }

    public ResourceLocation getTexture(IAbstractChangedEntity entity) {
        return new ResourceLocation("changed_addon:textures/screens/carry_ability.png");
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 5;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        if (Spectator(entity.getEntity()))
            return false;
        Optional<TransfurVariant<?>> variant = Optional.ofNullable(entity.getTransfurVariantInstance()).map(TransfurVariantInstance::getParent);
        return variant.filter(
                        v -> v.is(ChangedAddonTransfurVariants.Gendered.EXP2.getFemaleVariant()) || v.is(ChangedAddonTransfurVariants.Gendered.EXP2.getMaleVariant()) || v.is(ChangedAddonTransfurVariants.Gendered.ORGANIC_SNOW_LEOPARD.getFemaleVariant())
                                || v.is(ChangedAddonTransfurVariants.Gendered.ORGANIC_SNOW_LEOPARD.getMaleVariant()) || v.is(ChangedAddonTransfurVariants.Gendered.PURO_KIND.getFemaleVariant())
                                || v.is(ChangedAddonTransfurVariants.Gendered.PURO_KIND.getMaleVariant()) || v.is(ChangedAddonTags.TransfurTypes.ABLE_TO_CARRY))
                .isPresent();
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);
        Run(entity.getEntity());
    }

    @Override
    public void tick(IAbstractChangedEntity entity) {
        LivingEntity e = entity.getEntity();
        Level level = e.level;
        if(!e.isVehicle()) return;

        if(e.isEyeInFluid(FluidTags.WATER) && !level.getBlockState(new BlockPos(e.getX(), e.getEyeY(), e.getZ())).is(Blocks.BUBBLE_COLUMN)){
            e.ejectPassengers();
            broadcastPassengers(e);
        }
    }

    @Override
    public void onRemove(IAbstractChangedEntity entity) {
        super.onRemove(entity);
        Run(entity.getEntity());
    }

    public Entity CarryTarget(Player player) {
        return PlayerUtil.getEntityPlayerLookingAt(player, 4);
    }

    public boolean isPossibleToCarry(LivingEntity entity) {
        if (entity instanceof Player player) {
            var variant = ProcessTransfur.getPlayerTransfurVariant(player);
            if (variant != null) {
                var ability = variant.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                if (ability != null
                        && ability.suited
                        && ability.grabbedHasControl) {
                    return false;
                }
            }
        }
        return GrabEntityAbility.getGrabber(entity) == null;
    }

    // === Helpers de rede (broadcast correto) ===
    private static void broadcastPassengers(Entity vehicle) {
        if (!vehicle.level.isClientSide) {
            ServerLevel sl = (ServerLevel) vehicle.level;
            sl.getChunkSource().broadcastAndSend(vehicle, new ClientboundSetPassengersPacket(vehicle));
        }
    }

    private static void broadcastMotion(Entity entity) {
        if (!entity.level.isClientSide) {
            ServerLevel sl = (ServerLevel) entity.level;
            sl.getChunkSource().broadcastAndSend(entity, new ClientboundSetEntityMotionPacket(entity));
        }
    }

    // Lança uma entidade na direção do "launcher" e sincroniza
    private static void launchForward(Entity launcher, Entity target, double speed) {
        target.setDeltaMovement(launcher.getLookAngle().normalize().scale(speed));
        target.hasImpulse = true;
        broadcastMotion(target);
    }

    // Toca o som (opcional)
    private static void soundPlay(Player player) {
        player.level.playSound(null, player.blockPosition(), ChangedSounds.BOW2, SoundSource.PLAYERS, 2.5f, 1.0f);
    }


    private void Run(Entity mainEntity) {
        if (!(mainEntity instanceof Player player) || player.level.isClientSide)
            return;

        // Se já está carregando alguém: solta (e opcionalmente arremessa)
        Entity carried = player.getFirstPassenger();
        if (carried != null) {
            boolean toss = !player.isShiftKeyDown();

            // Ejetar passageiro
            carried.stopRiding();

            // Sync de passengers do veículo (player)
            broadcastPassengers(player);
            soundPlay(player);

            // Se quiser arremessar pra frente:
            if (toss) {
                launchForward(player, carried, 1.05);
            }
            return;
        }

        if (player.isSpectator())
            return;

        // Selecionar alvo para carregar
        Entity target = this.CarryTarget(player);
        if (target == null)
            return;

        if (target instanceof LivingEntity le && !this.isPossibleToCarry(le))
            return;

        // Restrições (centauro, criativo, etc.)
        if (target instanceof WhiteLatexCentaur ||
                (target instanceof Player p && ProcessTransfur.getPlayerTransfurVariant(p) != null &&
                        ProcessTransfur.getPlayerTransfurVariant(p).is(ChangedTransfurVariants.WHITE_LATEX_CENTAUR.get()))) {
            player.displayClientMessage(new TranslatableComponent("changedaddon.warn.cant_carry", target.getDisplayName()), true);
            return;
        }

        if (target instanceof Player carryPlayer && carryPlayer.isCreative() && !player.isCreative())
            return;

        // Só permite tipos permitidos
        if (target.getType().is(ChangedTags.EntityTypes.HUMANOIDS) || target.getType().is(ChangedAddonTags.EntityTypes.CAN_CARRY)) {
            if (target.startRiding(player, true)) {
                // Sync de passengers do veículo (player) para todos
                broadcastPassengers(player);
                soundPlay(player);
            }
        }
    }

}
