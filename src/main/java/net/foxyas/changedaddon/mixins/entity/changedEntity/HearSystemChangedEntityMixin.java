package net.foxyas.changedaddon.mixins.entity.changedEntity;

import net.foxyas.changedaddon.entity.api.alphas.AlphaVibrationConfig;
import net.foxyas.changedaddon.entity.api.alphas.IHearingSystem;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChangedEntity.class, remap = false)
public abstract class HearSystemChangedEntityMixin extends Monster implements IHearingSystem, VibrationListener.VibrationListenerConfig {

    private VibrationListener soundListener;
    private int heardCooldown;

    protected HearSystemChangedEntityMixin(EntityType<? extends ChangedEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.soundListener = new VibrationListener(
                        new EntityPositionSource(this.getId()),
                        8, // alcance em blocos
                        new AlphaVibrationConfig(this)
                );
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initHook(EntityType<? extends ChangedEntity> type, Level level, CallbackInfo ci) {
        this.soundListener = new VibrationListener(
                new EntityPositionSource(this.getId()),
                8, // alcance em blocos
                new AlphaVibrationConfig(this)
        );
    }

    @Override
    public @Nullable VibrationListener getGameEventListener() {
        return this.soundListener;
    }

    @Override
    public int getHeardCooldown() {
        return this.heardCooldown;
    }

    @Override
    public void setHeardCooldown(int heardCooldown) {
        this.heardCooldown = heardCooldown;
    }
}
