package net.foxyas.changedaddon.entity.simple;

import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbilityInstance;
import net.ltxprogrammer.changed.entity.AttributePresets;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexEntity;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import java.util.Objects;

public class DarkLatexYufengQueenEntity extends AbstractDarkLatexEntity {
    protected final SimpleAbilityInstance summonPups;

    public DarkLatexYufengQueenEntity(EntityType<? extends DarkLatexYufengQueenEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        summonPups = registerAbility(ability -> this.wantToSummon(), new SimpleAbilityInstance(ChangedAddonAbilities.SUMMON_DL_PUP.get(), IAbstractChangedEntity.forEntity(this)));
    }

    public static AttributeSupplier.Builder createLatexAttributes() {
        return ChangedEntity.createLatexAttributes().add(ForgeMod.ENTITY_REACH.get()).add(ForgeMod.BLOCK_REACH.get());
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.dragonLike(attributes);
        Objects.requireNonNull(attributes.getInstance(Attributes.MOVEMENT_SPEED)).setBaseValue(1.15f);
        Objects.requireNonNull(attributes.getInstance(ForgeMod.ENTITY_REACH.get())).setBaseValue(3.5F); // oh my LARD!!! it was a pain in the ass to figure out how to modify that attribute
        Objects.requireNonNull(attributes.getInstance(ForgeMod.BLOCK_REACH.get())).setBaseValue(5F);
        Objects.requireNonNull(attributes.getInstance(Attributes.ATTACK_DAMAGE)).setBaseValue(5F);
        Objects.requireNonNull(attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get())).setBaseValue(4.5F);
        Objects.requireNonNull(attributes.getInstance(ChangedAttributes.JUMP_STRENGTH.get())).setBaseValue(1.5F);
        Objects.requireNonNull(attributes.getInstance(Attributes.ATTACK_KNOCKBACK)).setBaseValue(1.5F);
        Objects.requireNonNull(attributes.getInstance(Attributes.MAX_HEALTH)).setBaseValue(40F);
        Objects.requireNonNull(attributes.getInstance(Attributes.ARMOR)).setBaseValue(8F);
        Objects.requireNonNull(attributes.getInstance(Attributes.ARMOR_TOUGHNESS)).setBaseValue(2F);
        Objects.requireNonNull(attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(0.6F);
    }

    @Override
    protected boolean targetSelectorTest(LivingEntity livingEntity) {
        if (LatexType.getEntityLatexType(livingEntity) == ChangedLatexTypes.WHITE_LATEX.get()) {
            return false;
        } else {
            return super.targetSelectorTest(livingEntity);
        }
    }

    public boolean wantToSummon() {
        return getTarget() != null;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.fromInt(0x3d3d3d);
    }

    @Override
    public TransfurVariant<?> getSelfVariant() {
        return ChangedAddonTransfurVariants.DARK_LATEX_YUFENG_QUEEN.get();
    }

    @Override
    public TransfurVariant<?> getTransfurVariant() {
        return ChangedTransfurVariants.DARK_LATEX_YUFENG.get();
    }

    @Override
    public boolean tryFuseWithTarget(LivingEntity entity, IAbstractChangedEntity source, float amount) {
        if (TransfurVariant.getEntityVariant(entity) == ChangedTransfurVariants.DARK_LATEX_YUFENG.get())
            return false;

        return super.tryFuseWithTarget(entity, source, amount);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }
}
