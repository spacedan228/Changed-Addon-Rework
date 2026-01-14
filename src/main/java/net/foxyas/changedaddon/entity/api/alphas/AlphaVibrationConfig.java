package net.foxyas.changedaddon.entity.api.alphas;

import net.foxyas.changedaddon.entity.api.alphas.IHearingSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlphaVibrationConfig implements VibrationListener.VibrationListenerConfig {

    private final PathfinderMob alpha;

    public AlphaVibrationConfig(PathfinderMob alpha) {
        this.alpha = alpha;
    }

    @Override
    public boolean shouldListen(@NotNull Level level, @NotNull GameEventListener listener, @NotNull BlockPos blockPos, @NotNull GameEvent gameEvent, @Nullable Entity pEntity) {

        // Só escuta se estiver dormindo
        if (!alpha.isSleeping()) return false;

        // Eventos que acordam
        return gameEvent == GameEvent.STEP
            || gameEvent == GameEvent.HIT_GROUND
            || gameEvent == GameEvent.ENTITY_DAMAGED;
    }

    @Override
    public void onSignalReceive(@NotNull Level pLevel, @NotNull GameEventListener pListener, @NotNull GameEvent pGameEvent, int pDistance) {
        if (alpha instanceof IHearingSystem iHearSystem) {
            iHearSystem.setHeardCooldown(20);
        }
    }
}
