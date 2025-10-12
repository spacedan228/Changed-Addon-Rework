package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.abilities.interfaces.GrabEntityAbilityExtensor;
import net.foxyas.changedaddon.entity.defaults.AbstractBasicOrganicChangedEntity;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.util.ColorUtil;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Objects;

public class Protogen0senia0Entity extends AbstractProtogenEntity {

    public Protogen0senia0Entity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
    }

    public Protogen0senia0Entity(PlayMessages.SpawnEntity ignoredSpawnEntity, Level level) {
        this(ChangedAddonEntities.PROTOGEN_0SENIA0.get(), level);
    }

    public boolean isOrganic() {
        return true;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return ColorUtil.lerpTFColors(this.maybeGetUnderlying(), 1, Color3.parseHex("#4d0ddb"), Color3.parseHex("#4d0ddb"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = ChangedEntity.createLatexAttributes();
        builder = builder.add(ChangedAttributes.TRANSFUR_DAMAGE.get(), 3f);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 24);
        builder = builder.add(Attributes.ARMOR, 2);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        safeSetBaseValue(attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get()), 3);
        safeSetBaseValue(attributes.getInstance(Attributes.MAX_HEALTH), 24);
        safeSetBaseValue(attributes.getInstance(Attributes.FOLLOW_RANGE), 40.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.MOVEMENT_SPEED), 1.1f);
        safeSetBaseValue(attributes.getInstance(ForgeMod.SWIM_SPEED.get()), 0.95f);
        safeSetBaseValue(attributes.getInstance(Attributes.ATTACK_DAMAGE), 3.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.ARMOR), 0);
        safeSetBaseValue(attributes.getInstance(Attributes.ARMOR_TOUGHNESS), 0);
        safeSetBaseValue(attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE), 0);
    }

    protected void safeSetBaseValue(@Nullable AttributeInstance instance, double value) {
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    protected void safeMulBaseValue(@Nullable AttributeInstance instance, double value) {
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() * value);
        }
    }

    public static void init() {
    }
}
