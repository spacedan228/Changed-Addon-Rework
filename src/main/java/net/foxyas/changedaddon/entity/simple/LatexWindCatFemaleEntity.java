package net.foxyas.changedaddon.entity.simple;

import net.foxyas.changedaddon.util.ColorUtil;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class LatexWindCatFemaleEntity extends ChangedEntity implements GenderedEntity {

    public LatexWindCatFemaleEntity(EntityType<? extends LatexWindCatFemaleEntity> p_19870_, Level level) {
        super(p_19870_, level);
    }

    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.1);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.9);
    }

    public static void init() {
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }

    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        Color3 firstColor = Color3.parseHex("#dfe6ec");
        Color3 secondColor = Color3.parseHex("#87a5d4");
        if (firstColor != null && secondColor != null) {
            return ColorUtil.lerpTFColor(firstColor, secondColor, getUnderlyingPlayer());
        }


        return firstColor;
    }
}
