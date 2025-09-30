package net.foxyas.changedaddon.mixins.mods.changed;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.event.ProgressTransfurEvents;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProcessTransfur.class, remap = false)
public class ProcessTransfurMixin {

    @Inject(method = "tickPlayerTransfurProgress", at = @At("HEAD"), cancellable = true)
    private static void InjectTick(Player player, CallbackInfo ci) {
        ProgressTransfurEvents.TickPlayerTransfurProgressEvent event = new ProgressTransfurEvents.TickPlayerTransfurProgressEvent(player);
        if (ChangedAddonMod.postEvent(event)) {
            ci.cancel();
        }
    }

}
