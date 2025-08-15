package net.foxyas.changedaddon.entity.simple;

import net.foxyas.changedaddon.entity.defaults.AbstractBasicChangedEntity;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.ltxprogrammer.changed.entity.AttributePresets;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class LatexCalicoCatEntity extends AbstractBasicChangedEntity {
    public LatexCalicoCatEntity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
    }

    public LatexCalicoCatEntity(PlayMessages.SpawnEntity ignoredPacket, Level world) {
        this(ChangedAddonEntities.LATEX_CALICO_CAT.get(), world);
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        Color3 firstColor = Color3.parseHex("#d67053");
        Color3 secondColor = Color3.parseHex("#67423f");
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
    protected void setAttributes(AttributeMap attributes) {
        AttributePresets.catLike(attributes);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    public Color3 getDripColor() {
        return this.random.nextBoolean() ? Color3.parseHex("#d67053") : Color3.parseHex("#67423f");
    }
}
