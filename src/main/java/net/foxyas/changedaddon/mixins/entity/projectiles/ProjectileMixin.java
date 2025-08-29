package net.foxyas.changedaddon.mixins.entity.projectiles;

import net.foxyas.changedaddon.abilities.DodgeAbilityInstance;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {

    @Shadow
    @Nullable
    public abstract Entity getOwner();

    @Inject(method = "canHitEntity", at = @At("RETURN"), cancellable = true)
    private void ignoreDodgingEntities(Entity pTarget, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) if (!pTarget.getLevel().isClientSide()) {
            Entity owner = this.getOwner();
            Entity attacker;
            attacker = Objects.requireNonNullElseGet(owner, () -> (Projectile) (Object) this);
            if (pTarget instanceof ChangedEntity changedEntity) {
                DodgeAbilityInstance dodgeAbilityInstance = changedEntity.getAbilityInstance(ChangedAddonAbilities.DODGE.get());
                DodgeAbilityInstance teleportDodgeAbilityInstance = changedEntity.getAbilityInstance(ChangedAddonAbilities.TELEPORT_DODGE.get());
                if (dodgeAbilityInstance != null
                        && dodgeAbilityInstance.canUse()
                        && dodgeAbilityInstance.canKeepUsing()
                        && dodgeAbilityInstance.isDodgeActive()) {
                    if (changedEntity.invulnerableTime <= 0) {
                        dodgeAbilityInstance.executeDodgeEffects(changedEntity, attacker);
                        dodgeAbilityInstance.executeDodgeHandle(changedEntity, attacker);
                        cir.setReturnValue(false);
                    }
                } else if (teleportDodgeAbilityInstance != null
                        && teleportDodgeAbilityInstance.canUse()
                        && teleportDodgeAbilityInstance.canKeepUsing()
                        && teleportDodgeAbilityInstance.isDodgeActive())
                    if (changedEntity.invulnerableTime <= 0) {
                        teleportDodgeAbilityInstance.executeDodgeEffects(changedEntity, attacker);
                        teleportDodgeAbilityInstance.executeDodgeHandle(changedEntity, attacker);
                        cir.setReturnValue(false);
                    }

            }
            if (pTarget instanceof Player player) {
                TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
                if (instance != null) {
                    DodgeAbilityInstance dodgeAbilityInstance = instance.getAbilityInstance(ChangedAddonAbilities.DODGE.get());
                    DodgeAbilityInstance teleportDodgeAbilityInstance = instance.getAbilityInstance(ChangedAddonAbilities.TELEPORT_DODGE.get());
                    if (dodgeAbilityInstance != null
                            && dodgeAbilityInstance.canUse()
                            && dodgeAbilityInstance.canKeepUsing()
                            && dodgeAbilityInstance.isDodgeActive()) {
                        if (player.invulnerableTime <= 0) {
                            dodgeAbilityInstance.executeDodgeEffects(player, attacker);
                            dodgeAbilityInstance.executeDodgeHandle(player, attacker);
                            cir.setReturnValue(false);
                        }
                    } else if (teleportDodgeAbilityInstance != null
                            && teleportDodgeAbilityInstance.canUse()
                            && teleportDodgeAbilityInstance.canKeepUsing()
                            && teleportDodgeAbilityInstance.isDodgeActive())
                        if (player.invulnerableTime <= 0) {
                            teleportDodgeAbilityInstance.executeDodgeEffects(player, attacker);
                            teleportDodgeAbilityInstance.executeDodgeHandle(player, attacker);
                            cir.setReturnValue(false);
                        }
                }
            }
        }
    }
}
