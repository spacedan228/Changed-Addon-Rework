package net.foxyas.changedaddon.mixins.entity.variant;

import com.google.common.collect.ImmutableMap;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.entity.customHandle.AttributesHandle;
import net.foxyas.changedaddon.item.armor.DarkLatexCoatItem;
import net.foxyas.changedaddon.item.armor.HazmatSuitItem;
import net.foxyas.changedaddon.variants.TransfurVariantInstanceExtensor;
import net.foxyas.changedaddon.variants.VariantExtraStats;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TransfurVariantInstance.class, remap = false)
public abstract class TransfurVariantInstanceMixin implements TransfurVariantInstanceExtensor {

    @Shadow
    @Final
    protected TransfurVariant<ChangedEntity> parent;
    @Shadow
    @Final
    private Player host;
    @Unique
    private boolean appliedFlySpeed;

    @Shadow
    public abstract TransfurVariant<?> getParent();

    @Shadow
    public abstract boolean shouldApplyAbilities();

    @Shadow
    public abstract ChangedEntity getChangedEntity();

    @Shadow
    public int ticksFlying;

    @Shadow
    protected boolean isTemporaryFromSuit;

    @Shadow
    public abstract boolean isTemporaryFromSuit();

    @Shadow
    public AbstractAbility<?> selectedAbility;

    @Shadow
    @Final
    public ImmutableMap<AbstractAbility<?>, AbstractAbilityInstance> abilityInstances;
    @Shadow
    public boolean abilityKeyState;


    @Shadow
    public abstract void resetTicksSinceLastAbilityActivity();

    @Unique
    public int ticksSinceSecondAbilityActivity;

    @Unique
    public boolean secondAbilityKeyState;

    @Override
    public boolean getSecondAbilityKeyState() {
        return secondAbilityKeyState;
    }

    @Override
    public void setSecondAbilityKeyState(boolean secondAbilityKeyState) {
        this.secondAbilityKeyState = secondAbilityKeyState;
    }

    @Unique
    public AbstractAbility<?> secondSelectedAbility;

    @Override
    public AbstractAbility<?> getSecondSelectedAbility() {
        return secondSelectedAbility;
    }

    @Override
    public void setSecondSelectedAbility(AbstractAbility<?> secondSelectedAbility) {
        if (this.abilityInstances.containsKey(secondSelectedAbility)) {
            this.resetTicksSinceSecondAbilityActivity();
            AbstractAbilityInstance instance = this.abilityInstances.get(secondSelectedAbility);
            if (instance.getUseType() != AbstractAbility.UseType.MENU) {
                if (this.secondSelectedAbility != secondSelectedAbility) {
                    instance.onSelected();
                }

                this.secondSelectedAbility = secondSelectedAbility;
            }
        }
    }

    @Override
    public int getTicksSinceSecondAbilityActivity() {
        return ticksSinceSecondAbilityActivity;
    }

    @Override
    public void resetTicksSinceSecondAbilityActivity() {
        this.ticksSinceSecondAbilityActivity = 0;
    }

    @Override
    public AbstractAbilityInstance getSecondSelectedAbilityInstance() {
        return this.abilityInstances.get(this.secondSelectedAbility);
    }

    @Inject(method = "tickAbilities", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableCollection;iterator()Lcom/google/common/collect/UnmodifiableIterator;", shift = At.Shift.AFTER))
    private void changedAddon$onTickAbilities(CallbackInfo ci) {
        if (ChangedAddonServerConfiguration.ALLOW_SECOND_ABILITY_USE.get()) {
            if (!this.isTemporaryFromSuit() && this.shouldApplyAbilities()) {
                if (this.secondSelectedAbility != null) {
                    AbstractAbilityInstance instance = this.abilityInstances.get(this.secondSelectedAbility);
                    if (instance != null) {
                        AbstractAbility.Controller controller = instance.getController();
                        boolean oldState = controller.exchangeKeyState(this.secondAbilityKeyState);
                        if (this.secondAbilityKeyState || instance.getController().isCoolingDown()) {
                            this.resetTicksSinceSecondAbilityActivity();
                        }

                        if (this.host.containerMenu == this.host.inventoryMenu && !this.host.isUsingItem() && !instance.getController().isCoolingDown()) {
                            instance.getUseType().check(this.secondAbilityKeyState, oldState, controller);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "saveAbilities", at = @At("TAIL"))
    private void changedAddon$saveAbilities(CallbackInfoReturnable<CompoundTag> cir) {
        if (ChangedAddonServerConfiguration.ALLOW_SECOND_ABILITY_USE.get()) {
            CompoundTag returnValue = cir.getReturnValue();

            if (returnValue != null) {
                ResourceLocation selectedKey = ChangedRegistry.ABILITY.get().getKey(this.secondSelectedAbility);
                if (selectedKey != null) {
                    TagUtil.putResourceLocation(returnValue, "secondSelectedAbility", selectedKey);
                }
            }
        }
    }

    @Inject(method = "loadAbilities", at = @At("TAIL"))
    private void changedAddon$loadAbilities(CompoundTag tagAbilities, CallbackInfo ci) {
        if (ChangedAddonServerConfiguration.ALLOW_SECOND_ABILITY_USE.get()) {
            if (tagAbilities.contains("secondSelectedAbility")) {
                AbstractAbility<?> savedSelected = ChangedRegistry.ABILITY.get().getValue(TagUtil.getResourceLocation(tagAbilities, "secondSelectedAbility"));
                if (this.abilityInstances.containsKey(savedSelected)) {
                    this.secondSelectedAbility = savedSelected;
                }
            }
        }
    }

    @Inject(method = "canWear", at = @At("HEAD"), cancellable = true)
    private void negateArmor(Player player, ItemStack itemStack, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if ((itemStack.getItem() instanceof HazmatSuitItem || itemStack.getItem() instanceof DarkLatexCoatItem) && slot.getType() == EquipmentSlot.Type.ARMOR) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tickFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;getFoodLevel()I", remap = true, ordinal = 0),
            cancellable = true)
    private void negateFly(CallbackInfo cir) {
        if (!this.host.isCreative() && !this.host.isSpectator()) {
            if (getChangedEntity() instanceof VariantExtraStats variantExtraStats) {
                if (!variantExtraStats.getFlyType().canFly()) {
                    if (host.getAbilities().flying || host.getAbilities().mayfly) {
                        host.getAbilities().mayfly = false;
                        host.getAbilities().flying = false;
                        host.onUpdateAbilities();
                    }

                    ticksFlying = 0;
                    cir.cancel();
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void negateFlyInTick(CallbackInfo cir) {
        if (this.parent.canGlide && this.shouldApplyAbilities()) {
            if (!this.host.isCreative() && !this.host.isSpectator()) {
                if (this.getChangedEntity() instanceof VariantExtraStats variantExtraStats) {
                    if (!variantExtraStats.getFlyType().canFly()) {
                        if (this.host.getAbilities().flying || this.host.getAbilities().mayfly) {
                            this.host.getAbilities().mayfly = false;
                            this.host.getAbilities().flying = false;
                            this.host.onUpdateAbilities();
                        }
                    }
                }
            }

            if (!this.host.isSpectator()) { // Spectator Can have multiple fly speeds
                if (getChangedEntity() instanceof VariantExtraStats variantExtraStats) {
                    if (variantExtraStats.getFlySpeed() != 0) {
                        if (variantExtraStats.getFlyType().canFly()) {
                            if (!this.appliedFlySpeed) {
                                this.appliedFlySpeed = true;
                                this.host.getAbilities().setFlyingSpeed(variantExtraStats.getFlySpeed());
                                this.host.onUpdateAbilities();
                            }
                        }
                    }
                }
            }
        }

        if (this.shouldApplyAbilities()) {
            ++this.ticksSinceSecondAbilityActivity;
        }
    }

    @Inject(method = "unhookAll", at = @At("TAIL"), cancellable = true)
    private void injectUnHookALl(Player player, CallbackInfo ci) {
        if (this.getChangedEntity() instanceof VariantExtraStats stats) {
            if (this.appliedFlySpeed) {
                this.appliedFlySpeed = false;
                this.host.getAbilities().setFlyingSpeed(AttributesHandle.DefaultPlayerFlySpeed);
                this.host.onUpdateAbilities();
            }
        }
    }

    @Inject(method = "save", at = @At("RETURN"), cancellable = true)
    private void InjectData(CallbackInfoReturnable<CompoundTag> cir) {
        if (this.getChangedEntity() instanceof VariantExtraStats stats) {
            stats.saveExtraData(cir.getReturnValue());
        }
    }

    @Inject(method = "load", at = @At("RETURN"), cancellable = true)
    private void readInjectedData(CompoundTag tag, CallbackInfo cir) {
        if (this.getChangedEntity() instanceof VariantExtraStats variantExtraStats) {
            variantExtraStats.readExtraData(tag);
        }
    }

    /*@Inject(method = "canWear", at = @At("HEAD"), cancellable = true)
    private void negateArmorForms(Player player, ItemStack itemStack, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir){
        if (this.getParent() == ChangedAddonTransfurVariants.LATEX_SNEP_FERAL_FORM.get() || this.getParent() == ChangedAddonTransfurVariants.LATEX_SNEP.get()){
            cir.setReturnValue(false);
        }
    }*/
}
