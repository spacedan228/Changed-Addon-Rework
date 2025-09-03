package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.entity.advanced.handle.VoidTransformationHandler;
import net.foxyas.changedaddon.entity.defaults.AbstractBasicOrganicChangedEntity;
import net.foxyas.changedaddon.entity.interfaces.CustomPatReaction;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.util.ColorUtil;
import net.foxyas.changedaddon.util.FoxyasUtils;
import net.foxyas.changedaddon.variants.VariantExtraStats;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.PowderSnowWalkable;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PlayMessages;

import java.util.List;

public class LuminaraFlowerBeastEntity extends AbstractBasicOrganicChangedEntity implements VariantExtraStats, CustomPatReaction, PowderSnowWalkable {

    private static final EntityDataAccessor<Boolean> AWAKENED = SynchedEntityData.defineId(LuminaraFlowerBeastEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean attributesApplied;
    private boolean attributesAppliedEntity;

    public LuminaraFlowerBeastEntity(PlayMessages.SpawnEntity ignoredPacket, Level world) {
        this(ChangedAddonEntities.LUMINARA_FLOWER_BEAST.get(), world);
    }

    public LuminaraFlowerBeastEntity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        safeSetBaseValue(attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get()), 1.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.MAX_HEALTH), 22.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.FOLLOW_RANGE), 40.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.MOVEMENT_SPEED), 1.0f);
        safeSetBaseValue(attributes.getInstance(ForgeMod.SWIM_SPEED.get()), 1.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.ATTACK_DAMAGE), 2.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.ARMOR), 0.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.ARMOR_TOUGHNESS), 0.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE), 0.0f);
    }

    public void setAttributesAwakened(AttributeMap attributes) {
        safeSetBaseValue(attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get()), 5.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.MAX_HEALTH), 40.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.FOLLOW_RANGE), 126.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.MOVEMENT_SPEED), 1.25f);
        safeSetBaseValue(attributes.getInstance(ForgeMod.SWIM_SPEED.get()), 1.25f);
        safeSetBaseValue(attributes.getInstance(Attributes.ATTACK_DAMAGE), 5.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.ARMOR), 0.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.ARMOR_TOUGHNESS), 0.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE), 0.0f);
    }

    @Override
    public void WhenPatEvent(LivingEntity patter, InteractionHand hand, LivingEntity patTarget) {
        patTarget.addEffect(new MobEffectInstance(ChangedAddonMobEffects.PACIFIED.get(), 600));
    }

    @Override
    public void WhenPattedReaction(Player patter, InteractionHand hand) {
        patter.addEffect(new MobEffectInstance(ChangedAddonMobEffects.PACIFIED.get(), 600));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AWAKENED, false);
    }

    public boolean isAwakened() {
        return this.entityData.get(AWAKENED);
    }

    public void setAwakened(boolean value) {
        this.entityData.set(AWAKENED, value);
    }

    @Override
    public float extraBlockBreakSpeed() {
        return 0;
    }

    /**
     * Apply awakened buffs (stronger, faster on land, but weaker in water).
     */

    public void applyAwakenedBuffs() {
        setAttributesAwakened(getAttributes());
        attributesApplied = true;
        var instance = IAbstractChangedEntity.forEitherSafe(this.maybeGetUnderlying()).map(IAbstractChangedEntity::getTransfurVariantInstance).orElse(null);
        if (instance != null) {
            instance.refreshAttributes();
        }
    }

    /**
     * Remove awakened buffs, returning entity to neutral state.
     */
    public void removeAwakenedBuffs() {
        setAttributes(getAttributes());
        attributesApplied = false;
        var instance = IAbstractChangedEntity.forEitherSafe(this.maybeGetUnderlying()).map(IAbstractChangedEntity::getTransfurVariantInstance).orElse(null);
        if (instance != null) {
            instance.refreshAttributes();
        }
    }

    @Mod.EventBusSubscriber
    public static class TransfurEvolveEventsHandle {

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                VoidTransformationHandler.handlePlayerVoid(event.player);
            }
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            VoidTransformationHandler.handleVoidDamage(event);
        }

    }

    @Override
    public void variantTick(Level level) {
        super.variantTick(level);
        Player player = this.getUnderlyingPlayer();
        if (player != null) {
            if (this.isAwakened()) {
                tryToPacifyNearbyEntities(128);
            }
            if (this.isAwakened() && !attributesApplied) {
                applyAwakenedBuffs();
            } else if (!this.isAwakened() && attributesApplied) {
                removeAwakenedBuffs();
            }
        } else {
            AttributeInstance attributeInstance = this.getAttribute(Attributes.FOLLOW_RANGE);
            double range = 32;
            if (attributeInstance != null) {
                range = attributeInstance.getValue();
            }
            tryToPacifyNearbyEntities(range);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();

        AttributeInstance attributeInstance = this.getAttribute(Attributes.FOLLOW_RANGE);
        double range = 32;
        if (attributeInstance != null) {
            range = attributeInstance.getValue();
        }
        tryToPacifyNearbyEntities(range);

        if (this.isAwakened() && !attributesAppliedEntity) {
            this.setAttributesAwakened(this.getAttributes());
            this.attributesAppliedEntity = true;
        } else if (!this.isAwakened() && attributesAppliedEntity) {
            this.setAttributes(this.getAttributes());
            this.attributesAppliedEntity = false;
        }

    }

    public void tryToPacifyNearbyEntities(double range) {
        List<LivingEntity> nearChangedBeasts = this.getLevel().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(range), (entity) -> FoxyasUtils.canEntitySeeOtherIgnoreGlass(entity, this));
        for (LivingEntity livingEntity : nearChangedBeasts) {
            if (livingEntity instanceof ChangedEntity changedEntity) {
                if (changedEntity instanceof LuminaraFlowerBeastEntity) {
                    continue;
                }

                if (changedEntity.getType().is(ChangedTags.EntityTypes.LATEX)) {
                    if (!changedEntity.hasEffect(ChangedAddonMobEffects.PACIFIED.get())) {
                        changedEntity.addEffect(new MobEffectInstance(ChangedAddonMobEffects.PACIFIED.get(), 60 * 20, 0, true, false, true));
                    }
                }
            } else if (livingEntity instanceof Player player) {
                TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
                if (instance != null) {
                    if ((instance.getChangedEntity() instanceof LuminaraFlowerBeastEntity)) {
                        continue;
                    }

                    if (instance.getParent().getEntityType().is(ChangedTags.EntityTypes.LATEX)) {
                        if (!player.hasEffect(ChangedAddonMobEffects.PACIFIED.get())) {
                            player.addEffect(new MobEffectInstance(ChangedAddonMobEffects.PACIFIED.get(), 60 * 20, 0, true, false, true));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.saveExtraData(tag);
    }

    public void saveExtraData(CompoundTag tag) {
        tag.putBoolean("Awakened", this.isAwakened());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readExtraData(tag);
    }

    public void readExtraData(CompoundTag tag) {
        if (tag.contains("Awakened")) this.setAwakened(tag.getBoolean("Awakened"));
    }

    @Override
    public FlyType getFlyType() {
        return this.isAwakened() ? FlyType.BOTH : FlyType.NONE;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        Color3 firstColor = Color3.getColor("#f5d4ef");
        Color3 secondColor = Color3.getColor("#241942");
        return ColorUtil.lerpTFColor(firstColor, secondColor, getUnderlyingPlayer());
    }
}
