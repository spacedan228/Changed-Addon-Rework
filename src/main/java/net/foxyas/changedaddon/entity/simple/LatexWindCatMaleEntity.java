package net.foxyas.changedaddon.entity.simple;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class LatexWindCatMaleEntity extends ChangedEntity implements GenderedEntity {

    public LatexWindCatMaleEntity(EntityType<? extends LatexWindCatMaleEntity> p_19870_, Level level) {
        super(p_19870_, level);
    }

    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.1);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.9);
    }

    @Override
    public Gender getGender() {
        return Gender.MALE;
    }

    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#334752");
    }
}
