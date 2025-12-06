package net.foxyas.changedaddon.mixins.mods.changed;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.event.UntransfurEvent;
import net.ltxprogrammer.changed.command.CommandTransfur;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CommandTransfur.class, remap = false)
public class CommandTransfurMixin {

//    @Inject(method = "untransfurPlayer", at = @At(value = "INVOKE", target = "Lnet/ltxprogrammer/changed/process/ProcessTransfur;ifPlayerTransfurred(Lnet/minecraft/world/entity/player/Player;Ljava/util/function/Consumer;)Z"), cancellable = true)
//    private static void UntransfurPlayerHook(CommandSourceStack source, ServerPlayer player, CallbackInfoReturnable<Integer> cir) {
//        TransfurVariantInstance<?> transfurVariantInstance = ProcessTransfur.getPlayerTransfurVariant(player);
//        TransfurVariant<?> transfurVariant = null;
//        if (transfurVariantInstance != null) transfurVariant = transfurVariantInstance.getParent();
//        UntransfurEvent untransfurEvent = new UntransfurEvent(player, transfurVariant, UntransfurEvent.UntransfurType.COMMAND);
//        if (ChangedAddonMod.postEvent(untransfurEvent)) {
//            if (untransfurEvent.newVariant != null) {
//                ProcessTransfur.setPlayerTransfurVariant(player, untransfurEvent.newVariant, TransfurContext.hazard(TransfurCause.GRAB_REPLICATE), 1, false);
//                cir.cancel();
//                return;
//            }
//            cir.cancel();
//        }
//    }
}
