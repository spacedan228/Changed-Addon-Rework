package net.foxyas.changedaddon.entity.api;

public interface ExtraConditions {

    interface Climb extends ExtraConditions {
        boolean canClimb();
    }
}
