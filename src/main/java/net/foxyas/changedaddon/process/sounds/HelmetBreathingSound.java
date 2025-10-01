package net.foxyas.changedaddon.process.sounds;

import net.foxyas.changedaddon.client.gui.HazardSuitHelmetOverlay;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class HelmetBreathingSound extends AbstractTickableSoundInstance {
    private final Player player;

    public HelmetBreathingSound(SoundEvent sound, Player player) {
        super(sound, SoundSource.PLAYERS);
        this.player = player;
        this.looping = true;
        this.delay = 20;
        this.volume = 0.6f;
    }

    public void forceStop() {
        this.stop();
    }

    @Override
    public void tick() {
        if (player == null || !player.isAlive()) {
            this.stop();
            return;
        }

        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();

        if (!HazardSuitHelmetOverlay.shouldApplyOverlay(player)) {
            this.stop();
        }
    }
}
