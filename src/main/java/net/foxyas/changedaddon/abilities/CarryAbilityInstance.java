package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexCentaur;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

public class CarryAbilityInstance extends AbstractAbilityInstance {

    boolean isCarrying = false;

    public CarryAbilityInstance(CarryAbility ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public boolean canUse() {
        return ability.canUse(entity);
    }

    @Override
    public boolean canKeepUsing() {
        return ability.canKeepUsing(entity);
    }

    @Override
    public void startUsing() {
        if(isCarrying){
            onRemove();
            return;
        }

        LivingEntity living = entity.getEntity();
        if(living.level.isClientSide || !(living instanceof Player holder) || holder.isSpectator()) return;

        Entity target = CarryAbility.CarryTarget(holder);
        if(!(target instanceof LivingEntity livingTarget) || !CarryAbility.isPossibleToCarry(livingTarget)) return;

        // Restrições (centauro, criativo, etc.)
        if (target instanceof WhiteLatexCentaur ||
                (target instanceof Player p && ProcessTransfur.getPlayerTransfurVariant(p) != null &&
                        ProcessTransfur.getPlayerTransfurVariant(p).is(ChangedTransfurVariants.WHITE_LATEX_CENTAUR.get()))) {
            holder.displayClientMessage(new TranslatableComponent("changedaddon.warn.cant_carry", target.getDisplayName()), true);
            return;
        }

        if (target instanceof Player carryPlayer && carryPlayer.isCreative() && !holder.isCreative())
            return;

        // Só permite tipos permitidos
        if (target.getType().is(ChangedTags.EntityTypes.HUMANOIDS) || target.getType().is(ChangedAddonTags.EntityTypes.CAN_CARRY)) {
            if (target.startRiding(holder, true)) {
                // Sync de passengers do veículo (player) para todos
                CarryAbility.broadcastPassengers(holder);
                soundPlay(holder);
                isCarrying = true;
            }
        }
    }

    @Override
    public void tick() {
        LivingEntity holder = entity.getEntity();
        if(isCarrying
                && (!holder.isVehicle() || (holder.isEyeInFluid(FluidTags.WATER) && !holder.level.getBlockState(new BlockPos(holder.getX(), holder.getEyeY(), holder.getZ())).is(Blocks.BUBBLE_COLUMN)))) {
            isCarrying = false;
            CarryAbility.broadcastPassengers(holder);
        }
    }

    @Override
    public void tickIdle() {
        tick();
    }

    @Override
    public void stopUsing() {}

    @Override
    public void onRemove() {
        if(!isCarrying) return;

        isCarrying = false;
        LivingEntity holder = entity.getEntity();
        if(holder.level.isClientSide) return;

        Entity carried = entity.getEntity().getFirstPassenger();
        if(carried == null) {
            CarryAbility.broadcastPassengers(holder);
            return;
        }

        // Ejetar passageiro
        carried.stopRiding();

        // Sync de passengers do veículo (player)
        CarryAbility.broadcastPassengers(holder);
        if(holder instanceof Player player) soundPlay(player);

        // Se quiser arremessar pra frente:
        boolean toss = !holder.isShiftKeyDown();
        if (toss) {
            launchForward(holder, carried, 1.05);
        }
    }

    // Toca o som (opcional)
    private void soundPlay(Player player) {
        player.level.playSound(null, player.blockPosition(), ChangedSounds.BOW2, SoundSource.PLAYERS, 2.5f, 1.0f);
    }

    // Lança uma entidade na direção do "launcher" e sincroniza
    private void launchForward(Entity launcher, Entity target, double speed) {
        target.setDeltaMovement(launcher.getLookAngle().normalize().scale(speed));
        target.hasImpulse = true;
        broadcastMotion(target);
    }

    private void broadcastMotion(Entity entity) {
        if (!entity.level.isClientSide) {
            ServerLevel sl = (ServerLevel) entity.level;
            sl.getChunkSource().broadcastAndSend(entity, new ClientboundSetEntityMotionPacket(entity));
        }
    }
}
