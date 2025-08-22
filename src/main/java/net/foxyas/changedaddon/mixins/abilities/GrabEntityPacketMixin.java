package net.foxyas.changedaddon.mixins.abilities;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GrabEntityPacket.class, remap = false)
public class GrabEntityPacketMixin {

    @ModifyExpressionValue(
            method = "handle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/ltxprogrammer/changed/process/ProcessTransfur;isPlayerNotLatex(Lnet/minecraft/world/entity/player/Player;)Z"
            )
    )
    private boolean changedAddon$overrideIsPlayerNotLatex(boolean original) {
        return false;
    }
}
