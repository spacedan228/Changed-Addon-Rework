package net.foxyas.changedaddon.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.foxyas.changedaddon.entity.goals.abilities.MayDropGrabbedEntityGoal;
import net.foxyas.changedaddon.entity.goals.abilities.MayGrabTargetGoal;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.ltxprogrammer.changed.ability.*;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChangedEntity.class, remap = false)
public abstract class ChangedEntityGrabHandleMixin extends Monster implements IGrabberEntity {

    protected GrabEntityAbilityInstance grabEntityAbilityInstance = null;
    protected int grabCooldown = 0;
    protected boolean ableToGrab;

    protected ChangedEntityGrabHandleMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.ableToGrab = level.getRandom().nextFloat() <= 0.15f; // Just for fail-safe
    }

    @Inject(at = @At("TAIL"), method = "<init>", cancellable = true)
    private void initHook(EntityType<? extends Monster> type, Level level, CallbackInfo ci) {
        this.ableToGrab = level.getRandom().nextFloat() <= 0.15f;
        if (ChangedAddon$canEntityGrab(type)) {
            this.grabEntityAbilityInstance = this.createGrabAbility();
        }
    }

    @Override
    public @Nullable GrabEntityAbilityInstance getGrabAbilityInstance() {
        return this.grabEntityAbilityInstance;
    }

    @Override
    public LivingEntity getGrabTarget() {
        return grabEntityAbilityInstance != null ? grabEntityAbilityInstance.grabbedEntity : null;
    }

    @Override
    public PathfinderMob asMob() {
        return this;
    }

    @Inject(at = @At("TAIL"), method = "registerGoals", remap = true, cancellable = true)
    private void goalsHook(CallbackInfo ci) {
        if (canEntityGrab(this.getType(), level)) {
            this.goalSelector.addGoal(10, new MayDropGrabbedEntityGoal(this));
            this.goalSelector.addGoal(10, new MayGrabTargetGoal(this));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (canEntityGrab(this.getType(), level)) {
            if (grabEntityAbilityInstance != null && grabEntityAbilityInstance.grabbedEntity == null) {
                if (grabCooldown > 0) this.grabCooldown--;
            }
            this.mayTickGrabAbility();
        }
    }

    @Override
    public int getGrabCooldown() {
        return grabCooldown;
    }

    @Override
    public void setGrabCooldown(int grabCooldown) {
        this.grabCooldown = grabCooldown;
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount) {
        if (canEntityGrab(this.getType(), level)) {
            mayDropGrabbedEntity(pDamageSource, pDamageAmount);
        }
        super.actuallyHurt(pDamageSource, pDamageAmount);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (canEntityGrab(this.getType(), level)) {
            this.saveGrabAbilityInTag(tag);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (canEntityGrab(this.getType(), level)) {
            this.readGrabAbilityInTag(tag);
        }
    }

    @Unique
    private boolean ChangedAddon$canEntityGrab(EntityType<?> type) {
        return type.is(ChangedAddonTags.EntityTypes.CAN_GRAB) || ableToGrab;
    }

    @Unique
    private boolean ChangedAddon$canEntityGrab() {
        return ChangedAddon$canEntityGrab(this.getType());
    }

    @Override
    public boolean canEntityGrab(EntityType<?> type, Level level) {
        return ChangedAddon$canEntityGrab(type);
    }

    @ModifyReturnValue(method = "getAbilityInstance", at = @At("RETURN"))
    private <A extends AbstractAbilityInstance> A getAbilityInstanceHook(A original, AbstractAbility<A> ability) {
        if (canEntityGrab(this.getType(), level)) return (A) (this.grabEntityAbilityInstance != null && ability == this.grabEntityAbilityInstance.ability ? this.grabEntityAbilityInstance : original);
        return original;
    }
}