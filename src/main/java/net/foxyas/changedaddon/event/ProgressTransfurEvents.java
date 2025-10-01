package net.foxyas.changedaddon.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class ProgressTransfurEvents {


    public static class TickPlayerTransfurProgressEvent extends Event {

        private final Player player;

        public TickPlayerTransfurProgressEvent(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }
}
