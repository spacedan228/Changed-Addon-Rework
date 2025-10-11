package net.foxyas.changedaddon.entity.api;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.client.gui.AbstractRadialScreen;
import net.ltxprogrammer.changed.entity.ChangedEntity;

import java.awt.*;

public interface IDynamicPawColor {

    Color getPawBeansColor();

    default Color getPawColor() {
        if (this instanceof ChangedEntity changedEntity) {
            return new Color(AbstractRadialScreen.getColors(IAbstractChangedEntity.forEntity(changedEntity).getTransfurVariantInstance()).setForegroundToBright().foreground().toInt());
        }
        return Color.WHITE;
    }
}
