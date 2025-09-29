package net.foxyas.changedaddon.entity.customHandle;

import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

public enum BossMusicTheme {
    EXP9("exp9_phase2", ChangedAddonSoundEvents.EXP9_THEME),
    EXP10("exp10", ChangedAddonSoundEvents.EXP10_THEME);

    private final SoundEvent soundEvent;
    private final String ID;

    BossMusicTheme(String name, SoundEvent soundEvent) {
        this.soundEvent = soundEvent;
        this.ID = name;
    }

    public String getIDName() {
        return ID;
    }

    public SoundEvent getAsSoundEvent() {
        return soundEvent;
    }

    public Music getAsMusic() {
        return new Music(this.soundEvent, 1, 1, true);
    }
}