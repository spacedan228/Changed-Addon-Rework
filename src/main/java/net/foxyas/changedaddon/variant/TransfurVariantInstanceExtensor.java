package net.foxyas.changedaddon.variant;

import net.foxyas.changedaddon.event.UntransfurEvent;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;

public interface TransfurVariantInstanceExtensor {

    AbstractAbility<?> getSecondSelectedAbility();

    boolean getSecondAbilityKeyDown();

    void setSecondSelectedAbility(AbstractAbility<?> ability);

    void setSecondAbilityKeyDown(boolean value);

    int getTicksSinceSecondAbilityActivity();

    void resetTicksSinceSecondAbilityActivity();

    AbstractAbilityInstance getSecondSelectedAbilityInstance();

    boolean getUntransfurImmunity(UntransfurEvent.UntransfurType type);

    boolean isSecondAbilityKeyEffectivelyDown();

    int getSecondAbilityKeyStateFlips();

    void setSecondAbilityKeyStateFlips(int value);
    void addSecondAbilityKeyStateFlips(int value);

    void setUntransfurImmunity(UntransfurEvent.UntransfurType type, boolean value);

}
