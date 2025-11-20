package net.foxyas.changedaddon.mixins.mods.changed;

import net.ltxprogrammer.changed.effect.particle.EmoteParticleOption;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EmoteParticleOption.class, remap = false)
public abstract class EmoteParticleOptionMixin {

    @Shadow public abstract Entity getEntity();

    @Inject(method = "writeToNetwork", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeInt(I)Lio/netty/buffer/ByteBuf;", shift = At.Shift.BY), remap = true)
    private void fixingBufferDataInconsistency(FriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeInt(this.getEntity().getId());
    }
}
