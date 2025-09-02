package net.foxyas.changedaddon.entity.simple;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.AbstractSnowLeopard;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexWhiteSnowLeopardFemale extends AbstractSnowLeopard {
    public LatexWhiteSnowLeopardFemale(EntityType<? extends LatexWhiteSnowLeopardFemale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public static void init() {
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