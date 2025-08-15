package net.foxyas.changedaddon.entity.simple;

import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.variants.ExtraVariantStats;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class LynxEntity extends ChangedEntity implements PowderSnowWalkable, ExtraVariantStats {
    public LynxEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ChangedAddonEntities.LYNX.get(), world);
    }

    public LynxEntity(EntityType<LynxEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setPersistenceRequired();
        setAttributes(this.getAttributes());
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder.add(ChangedAttributes.TRANSFUR_DAMAGE.get(), 0);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 24);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.catLike(attributes);
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public Color3 getDripColor() {
        return this.random.nextBoolean() ? Color3.getColor("#ebd182") : Color3.getColor("#eace7a");
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        Color3 firstColor = Color3.getColor("#ebd182");
        Color3 secondColor = Color3.getColor("#eace7a");
        if (firstColor != null && secondColor != null) {
            return lerpColors(firstColor, secondColor);
        }

        return firstColor;
    }

    public Color3 lerpColors(Color3 start, Color3 end) {
        int startColorInt = start.toInt();
        int endColorInt = end.toInt();

        if (this.getUnderlyingPlayer() != null) {
            TransfurVariantInstance<?> transfurVariantInstance = ProcessTransfur.getPlayerTransfurVariant(this.getUnderlyingPlayer());
            if (transfurVariantInstance != null) {
                float lerpValue = Mth.lerp(transfurVariantInstance.getTransfurProgression(1), startColorInt, endColorInt);
                return Color3.fromInt(((int) lerpValue));
            }
        }
        return start;
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public double getMyRidingOffset() {
        return super.getMyRidingOffset();
    }

    @Override
    public @NotNull SoundEvent getHurtSound(@NotNull DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    @Override
    public @NotNull SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    @Override
    public float extraBlockBreakSpeed() {
        return 0.35f;
    }
}
