package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.entity.defaults.AbstractBasicOrganicChangedEntity;
import net.foxyas.changedaddon.entity.interfaces.CustomPatReaction;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.util.ColorUtil;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.foxyas.changedaddon.variants.VariantExtraStats;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAttributes;
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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PlayMessages;

import java.util.Objects;
import java.util.UUID;

public class LuminaraFlowerBeastEntity extends AbstractBasicOrganicChangedEntity implements VariantExtraStats, CustomPatReaction {

    private static final EntityDataAccessor<Boolean> AWAKENED = SynchedEntityData.defineId(LuminaraFlowerBeastEntity.class, EntityDataSerializers.BOOLEAN);

    public LuminaraFlowerBeastEntity(PlayMessages.SpawnEntity ignoredPacket, Level world) {
        this(ChangedAddonEntities.LUMINARA_FLOWER_BEAST.get(), world);
    }

    public LuminaraFlowerBeastEntity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        Objects.requireNonNull(attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get())).setBaseValue((1));
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue((22));
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(40.0f);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1f);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1f);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(2.0f);
        attributes.getInstance(Attributes.ARMOR).setBaseValue(0);
        attributes.getInstance(Attributes.ARMOR_TOUGHNESS).setBaseValue(0);
        attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0);
    }

    public void setAttributesAwakened(AttributeMap attributes) {
        attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get()).setBaseValue((5f));
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue((40.0f));
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(40.0f);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.25f);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.25f);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(5.0f);
        attributes.getInstance(Attributes.ARMOR).setBaseValue(0);
        attributes.getInstance(Attributes.ARMOR_TOUGHNESS).setBaseValue(0);
        attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0);
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

    // Fixed UUIDs for each modifier so we can remove them reliably later
    private static final UUID MAX_HEALTH_ID = UUID.fromString("bbab1111-dddd-aaaa-cccc-777777777777");
    private static final UUID SPEED_BOOST_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID DAMAGE_BOOST_ID = UUID.fromString("66666666-7777-8888-9999-000000000000");
    private static final UUID TRANSFUR_DAMAGE_ID = UUID.fromString("aaaa1111-bbbb-2222-cccc-333333333333");
    private static final UUID SWIM_SPEED_ID = UUID.fromString("dddd4444-eeee-5555-ffff-666666666666");

    /**
     * Apply awakened buffs (stronger, faster on land, but weaker in water).
     */

    public void applyAwakenedBuffs() {
        Player player = this.getUnderlyingPlayer();
        if (player == null) {
            return;
        }

        // Movement speed boost on land
        addModifier(player, Attributes.MAX_HEALTH,
                new AttributeModifier(MAX_HEALTH_ID, "Awakened Health Boost", 18, AttributeModifier.Operation.ADDITION));

        addModifier(player, Attributes.MOVEMENT_SPEED,
                new AttributeModifier(SPEED_BOOST_ID, "Awakened Speed Boost", 0.25, AttributeModifier.Operation.MULTIPLY_TOTAL));

        // Extra attack damage
        addModifier(player, Attributes.ATTACK_DAMAGE,
                new AttributeModifier(DAMAGE_BOOST_ID, "Awakened Damage Boost", 3.0, AttributeModifier.Operation.ADDITION));

        // Extra transfur damage (custom attribute)
        addModifier(player, ChangedAttributes.TRANSFUR_DAMAGE.get(),
                new AttributeModifier(TRANSFUR_DAMAGE_ID, "Awakened Transfur Damage Boost", 4.0, AttributeModifier.Operation.ADDITION));

        // Reduced swim speed to make entity slower in water
        addModifier(player, ForgeMod.SWIM_SPEED.get(),
                new AttributeModifier(SWIM_SPEED_ID, "Awakened Swim Speed Reduction", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    /**
     * Remove awakened buffs, returning entity to neutral state.
     */
    public void removeAwakenedBuffs() {
        Player player = this.getUnderlyingPlayer();
        if (player == null) {
            return;
        }
        removeModifier(player, Attributes.MOVEMENT_SPEED, SPEED_BOOST_ID);
        removeModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_BOOST_ID);
        removeModifier(player, ChangedAttributes.TRANSFUR_DAMAGE.get(), TRANSFUR_DAMAGE_ID);
        removeModifier(player, ForgeMod.SWIM_SPEED.get(), SWIM_SPEED_ID);
    }

    /**
     * Remove awakened buffs, returning entity to neutral state.
     */
    public static void removeAwakenedBuffs(Player player) {
        if (player == null) {
            return;
        }
        removeModifier(player, Attributes.MOVEMENT_SPEED, SPEED_BOOST_ID);
        removeModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_BOOST_ID);
        removeModifier(player, ChangedAttributes.TRANSFUR_DAMAGE.get(), TRANSFUR_DAMAGE_ID);
        removeModifier(player, ForgeMod.SWIM_SPEED.get(), SWIM_SPEED_ID);
    }

    // ===== Helper methods =====

    private static void addModifier(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute attribute, AttributeModifier modifier) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && instance.getModifier(modifier.getId()) == null) {
            instance.addTransientModifier(modifier); // transient = does not persist on save/load
        }
    }

    private static void removeModifier(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute attribute, UUID id) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && instance.getModifier(id) != null) {
            instance.removeModifier(id); // cleanly removes only the modifier with that UUID
        }
    }

    @Mod.EventBusSubscriber
    public static class TransfurEventHandle {

        @SubscribeEvent
        public static void WhenUnTransfured(ProcessTransfur.EntityVariantAssigned changedVariant) {
            final TransfurVariant<?> oldVariant = changedVariant.previousVariant;
            final TransfurVariant<?> newVariant = changedVariant.originalVariant;
            LivingEntity living = changedVariant.livingEntity;
            if (living instanceof Player player && oldVariant != null) {
                if (oldVariant.is(ChangedAddonTransfurVariants.LUMINARA_FLOWER_BEAST.get())) {
                    if (newVariant == null) {
                        TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
                        if (instance != null && instance.getChangedEntity() instanceof LuminaraFlowerBeastEntity luminaraFlowerBeastEntity) {
                            if (luminaraFlowerBeastEntity.isAwakened()) {
                                removeAwakenedBuffs(player);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void variantTick(Level level) {
        super.variantTick(level);
        Player player = this.getUnderlyingPlayer();
        if (player != null) {
            if (this.isAwakened()) {
                applyAwakenedBuffs();
            } else {
                TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
                if (instance != null) {
                    if (instance.ageAsVariant >= 1000) {
                        this.setAwakened(true);
                    }
                }
                removeAwakenedBuffs();
            }
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.isAwakened()) {
            this.setAttributesAwakened(this.getAttributes());
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
