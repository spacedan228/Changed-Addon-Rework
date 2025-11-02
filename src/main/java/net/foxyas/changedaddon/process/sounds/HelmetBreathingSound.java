package net.foxyas.changedaddon.process.sounds;

import net.foxyas.changedaddon.client.gui.HazardSuitHelmetOverlay;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HelmetBreathingSound extends AbstractTickableSoundInstance {
    private final Player player;

    public HelmetBreathingSound(SoundEvent sound, Player player) {
        super(sound, SoundSource.PLAYERS);
        this.player = player;
        this.looping = true;
        this.delay = 20;
        this.volume = 0.6f;
        this.pitch = 0.25f;
    }

    public void forceStop() {
        this.stop();
    }

    protected static Vec3 getMouthPosition(Player player) {
        return player.getEyePosition().subtract(0, 0.25, 0);
    }

    @Override
    public void tick() {
        if (player == null || !player.isAlive()) {
            this.stop();
            return;
        }

        Vec3 mouthPosition = getMouthPosition(player);
        this.x = mouthPosition.x();
        this.y = mouthPosition.y();
        this.z = mouthPosition.z();

        if (!HazardSuitHelmetOverlay.shouldApplyOverlay(player)) {
            this.stop();
        }
    }
}
