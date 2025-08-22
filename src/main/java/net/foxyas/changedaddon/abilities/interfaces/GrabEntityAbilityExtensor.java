package net.foxyas.changedaddon.abilities.interfaces;

public interface GrabEntityAbilityExtensor {

    void setSafeMode(boolean safeMode);

    boolean isSafeMode();

    default void runHug() {

    }

    default void runTightHug() {

    }
}
