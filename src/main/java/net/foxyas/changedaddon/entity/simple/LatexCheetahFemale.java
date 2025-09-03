package net.foxyas.changedaddon.entity.simple;

import net.foxyas.changedaddon.util.ColorUtil;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.AbstractSnowLeopard;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class LatexCheetahFemale extends AbstractSnowLeopard {
    public LatexCheetahFemale(EntityType<? extends LatexCheetahFemale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public static void init() {
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.4F);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.9);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(20.0F);
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        Color3 firstColor = Color3.getColor("#d8b270");
        Color3 secondColor = Color3.getColor("#634927");
        Color3 thirdColor = Color3.getColor("#ecddc1");

        float progress = ColorUtil.getPlayerTransfurProgressSafe(this.getUnderlyingPlayer(), 1);
        progress = Mth.clamp(progress, 0.0f, 1.0f);

        if (progress < 0.5f) {
            // 0.0 → 0.5 → vai de first → second
            float t = progress / 0.5f; // normaliza para 0–1
            return ColorUtil.lerpTFColor(firstColor, secondColor, t);
        } else {
            // 0.5 → 1.0 → vai de second → third
            float t = (progress - 0.5f) / 0.5f; // normaliza para 0–1
            return ColorUtil.lerpTFColor(secondColor, thirdColor, t);
        }
    }


    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public Gender getGender() {
        return null;
    }
}