package net.foxyas.changedaddon.mixins.entity;


import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.foxyas.changedaddon.entity.goals.abilities.MayDropGrabbedEntityGoal;
import net.foxyas.changedaddon.entity.goals.abilities.MayGrabTargetGoal;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.beast.AbstractAquaticEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractLatexSquidDog;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractLatexSquidDog.class, remap = false)
public abstract class LatexSquidDogMixin extends AbstractAquaticEntity implements GenderedEntity, IGrabberEntity {

    protected @Nullable GrabEntityAbilityInstance grabEntityAbilityInstance = null;

    public LatexSquidDogMixin(EntityType<? extends AbstractLatexSquidDog> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initHook(EntityType<? extends AbstractLatexSquidDog> p_19870_, Level p_19871_, CallbackInfo ci) {
        this.grabEntityAbilityInstance = this.createGrabAbility();
    }

    @Override
    public PathfinderMob asMob() {
        return this;
    }

    @Override
    public LivingEntity getGrabTarget() {
        return this.getTarget();
    }

    @Override
    public GrabEntityAbilityInstance getGrabAbilityInstance() {
        return this.grabEntityAbilityInstance;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        //this.goalSelector.addGoal(1, new GrabTargetGoal(this, 0.4f, false));
        this.goalSelector.addGoal(10, new MayDropGrabbedEntityGoal(this));
        this.goalSelector.addGoal(10, new MayGrabTargetGoal(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.saveGrabAbilityInTag(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readGrabAbilityInTag(tag);
    }

    @Override
    protected void actuallyHurt(DamageSource pDamageSource, float pDamageAmount) {
        mayDropGrabbedEntity(pDamageSource, pDamageAmount);
        super.actuallyHurt(pDamageSource, pDamageAmount);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.mayTickGrabAbility();
    }

    @Override
    public <A extends AbstractAbilityInstance> A getAbilityInstance(AbstractAbility<A> ability) {
        return (A)(this.grabEntityAbilityInstance != null && ability == this.grabEntityAbilityInstance.ability ? this.grabEntityAbilityInstance : super.getAbilityInstance(ability));
    }
}
