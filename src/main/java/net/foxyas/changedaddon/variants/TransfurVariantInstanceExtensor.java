package net.foxyas.changedaddon.variants;

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

}
