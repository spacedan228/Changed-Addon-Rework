package net.foxyas.changedaddon.mixins.entity.player;

import net.foxyas.changedaddon.entity.api.LivingEntityDataExtensor;
import net.foxyas.changedaddon.entity.api.SyncTrackMotion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements SyncTrackMotion, LivingEntityDataExtensor {

    @Unique
    public boolean isMoving = false;

    @Unique
    public Vec3 lastKnownMotion = null;

    @Override
    public boolean isMoving() {
        return isMoving;
    }

    @Override
    public void setIsMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    @Unique
    public Vec3 getLastKnownMotion() {
        return lastKnownMotion;
    }

    @Override
    public void setLastKnownMotion(@Nullable Vec3 vec3) {
        this.lastKnownMotion = vec3;
    }

    @Unique
    private ServerPlayer getSelf() {
        return (ServerPlayer) (Object) this;
    }

    @Inject(method = "tick", at = @At("TAIL"), cancellable = true)
    private void cleanKnownMotion(CallbackInfo ci) {
        if (getSelf().tickCount % 40 == 0) {
            if (getLastKnownMotion() != null) {
                setLastKnownMotion(null);
            }
            if (isMoving) {
                this.isMoving = false;
            }
        }
    }
}
