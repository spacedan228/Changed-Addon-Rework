package net.foxyas.changedaddon.event;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

public class UntransfurEvent extends Event {

    private final Player player;
    @Nullable
    private final TransfurVariant<?> oldVariant;
    @Nullable
    public TransfurVariant<?> newVariant = null;

    public enum UntransfurType {
        COMMAND,
        SURVIVAL;
    }

    public final UntransfurType untransfurType;

    public UntransfurEvent(Player player, @Nullable TransfurVariant<?> oldVariant, UntransfurType untransfurType) {
        this.player = player;
        this.oldVariant = oldVariant;
        this.untransfurType = untransfurType;
    }

    public Player getPlayer() {
        return player;
    }

    @Nullable
    public TransfurVariant<?> getOldVariant() {
        return oldVariant;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
