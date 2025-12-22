package net.foxyas.changedaddon.mixins.world;

import net.foxyas.changedaddon.init.ChangedAddonCriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.advancements.critereon.ConsumeItemTrigger.class)
public class ConsumeItemTrigger {

    @Inject(method = "trigger", at = @At("RETURN"))
    private void usedItemHook(ServerPlayer player, ItemStack itemStack, CallbackInfo ci) {
        ChangedAddonCriteriaTriggers.USED_ITEM_AMOUNT_TRIGGER.trigger(player, itemStack);
    }
}
