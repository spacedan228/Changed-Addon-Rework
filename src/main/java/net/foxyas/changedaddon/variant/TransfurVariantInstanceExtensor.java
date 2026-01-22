package net.foxyas.changedaddon.variant;

import net.foxyas.changedaddon.event.UntransfurEvent;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.util.KeyStateTracker;

public interface TransfurVariantInstanceExtensor {

    AbstractAbility<?> getSecondSelectedAbility();

    void setSecondSelectedAbility(AbstractAbility<?> ability);

    int getTicksSinceSecondAbilityActivity();

    void resetTicksSinceSecondAbilityActivity();

    void setSecondAbilityKey(KeyStateTracker secondAbilityKey);

    KeyStateTracker getSecondAbilityKey();

    AbstractAbilityInstance getSecondSelectedAbilityInstance();

    boolean getUntransfurImmunity(UntransfurEvent.UntransfurType type);

    void setUntransfurImmunity(UntransfurEvent.UntransfurType type, boolean value);

}
