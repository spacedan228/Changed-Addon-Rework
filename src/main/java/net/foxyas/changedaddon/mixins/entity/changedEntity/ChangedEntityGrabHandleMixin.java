package net.foxyas.changedaddon.mixins.entity.changedEntity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.foxyas.changedaddon.entity.goals.abilities.MayCauseGrabDamageGoal;
import net.foxyas.changedaddon.entity.goals.abilities.MayDropGrabbedEntityGoal;
import net.foxyas.changedaddon.entity.goals.abilities.MayGrabTargetGoal;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChangedEntity.class, remap = false)
public abstract class ChangedEntityGrabHandleMixin extends Monster implements IGrabberEntity, IAlphaAbleEntity {

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
        if (canEntityGrab(type, level)) {
            this.grabEntityAbilityInstance = this.createGrabAbility();
        }
    }

    @Override
    public @Nullable GrabEntityAbilityInstance getGrabAbilityInstance() {
        return this.grabEntityAbilityInstance;
    }

    @Override
    public LivingEntity getGrabbedEntity() {
        return this.grabEntityAbilityInstance != null ? this.grabEntityAbilityInstance.grabbedEntity : null;
    }

    @Override
    public PathfinderMob asMob() {
        return this;
    }

    @Inject(at = @At("TAIL"), method = "registerGoals", remap = true, cancellable = true)
    private void goalsHook(CallbackInfo ci) {
        this.goalSelector.addGoal(10, new MayDropGrabbedEntityGoal(this));
        this.goalSelector.addGoal(10, new MayGrabTargetGoal(this));
        this.goalSelector.addGoal(10, new MayCauseGrabDamageGoal(this));
    }

    @Override
    public boolean canEntityGrab(EntityType<?> type, Level level) {
        return type.is(ChangedAddonTags.EntityTypes.CAN_GRAB) || isAbleToGrab() || isAlpha();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickHook(CallbackInfo ci) {
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (canEntityGrab(this.getType(), level)) {
            if (grabEntityAbilityInstance == null) {
                this.grabEntityAbilityInstance = createGrabAbility(); // fail-safe
                return;
            }
            if (grabEntityAbilityInstance.grabbedEntity == null) {
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
        tag.putBoolean("isAlpha", isAlpha());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (canEntityGrab(this.getType(), level)) {
            this.readGrabAbilityInTag(tag);
        }
        if (tag.contains("isAlpha")) setAlpha(tag.getBoolean("isAlpha"));
    }

    @Override
    public boolean isAbleToGrab() {
        return ableToGrab || isAlpha();
    }

    @ModifyReturnValue(method = "getAbilityInstance", at = @At("RETURN"))
    private <A extends AbstractAbilityInstance> A getAbilityInstanceHook(A original, AbstractAbility<A> ability) {
        if (canEntityGrab(this.getType(), level)) return (A) (this.grabEntityAbilityInstance != null && ability == this.grabEntityAbilityInstance.ability ? this.grabEntityAbilityInstance : original);
        return original;
    }

    @Override
    public void setAlpha(boolean alpha) {
        ChangedEntity self = (ChangedEntity) (Object) this;
        if (this.isAlpha() != alpha) {
            self.getEntityData().set(IS_ALPHA, alpha);
            this.refreshDimensions();
        }
    }

    @Override
    public boolean isAlpha() {
        ChangedEntity self = (ChangedEntity) (Object) this;
        return self.getEntityData().get(IS_ALPHA);
    }

    @Inject(method = "savePlayerVariantData", at = @At("HEAD"), cancellable = true)
    private void savePlayerVariantDataHook(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        if (tag == null) tag = new CompoundTag();//temporary fix so it doesnt crash
        tag.putBoolean("isAlpha", isAlpha());
    }

    @Inject(method = "readPlayerVariantData", at = @At("HEAD"), cancellable = true)
    private void readPlayerVariantDataHook(CompoundTag tag, CallbackInfo ci) {
        if (tag == null) return;
        if (tag.contains("isAlpha")) setAlpha(tag.getBoolean("isAlpha"));
    }

    @Inject(method = "defineSynchedData", at = @At("HEAD"), remap = true, cancellable = true)
    private void defineSynchedDataHook(CallbackInfo ci) {
        ChangedEntity self = (ChangedEntity) (Object) this;
        self.getEntityData().define(IS_ALPHA, false);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (pKey == IS_ALPHA) {
            this.refreshDimensions();
        }
    }
}