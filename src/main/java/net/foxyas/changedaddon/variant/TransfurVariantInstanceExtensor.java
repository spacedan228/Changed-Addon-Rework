package net.foxyas.changedaddon.variant;

import net.foxyas.changedaddon.event.UntransfurEvent;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;

public interface TransfurVariantInstanceExtensor {

    AbstractAbility<?> getSecondSelectedAbility();

    boolean getSecondAbilityKeyState();

    void setSecondSelectedAbility(AbstractAbility<?> ability);

    void setSecondAbilityKeyState(boolean value);

    int getTicksSinceSecondAbilityActivity();

    void resetTicksSinceSecondAbilityActivity();

    AbstractAbilityInstance getSecondSelectedAbilityInstance();

    boolean getUntransfurImmunity(UntransfurEvent.UntransfurType type);

    void setUntransfurImmunity(UntransfurEvent.UntransfurType type, boolean value);

}
