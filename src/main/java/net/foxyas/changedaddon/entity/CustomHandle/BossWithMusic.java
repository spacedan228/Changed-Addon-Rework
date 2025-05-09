package net.foxyas.changedaddon.entity.CustomHandle;

import net.minecraft.sounds.Music;
import org.jetbrains.annotations.NotNull;

public interface BossWithMusic {

    boolean ShouldPlayMusic();

    @NotNull
    BossMusicTheme BossMusicTheme();

    default Music Music(){
        return BossMusicTheme().getAsMusic();
    }

}
