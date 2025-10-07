package net.foxyas.changedaddon.client.model.clothes;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

/**
 * Interface for accessories or systems that can modify
 * player model visibility properties.
 */
public interface PlayerModelVisibilityModifier {

    /**
     * Adjusts the visibility properties of the given player model.
     *
     * @param player The client player whose model should be modified.
     */
    void setPlayerModelProperties(AbstractClientPlayer player, PlayerModel<AbstractClientPlayer> model);

    /**
     * Returns whether the visibility effect is active.
     * Useful for optimizations or checks before rendering.
     */
    default boolean isVisible() {
        return true;
    }
}
