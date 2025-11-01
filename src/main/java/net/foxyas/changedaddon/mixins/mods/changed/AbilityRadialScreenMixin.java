package net.foxyas.changedaddon.mixins.mods.changed;

import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.ltxprogrammer.changed.client.gui.AbilityRadialScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = AbilityRadialScreen.class, remap = false)
public abstract class AbilityRadialScreenMixin {

    @Inject(method = "tooltipsFor", at = @At("RETURN"), remap = false, cancellable = true)
    private void toolTipsHook(int par1, CallbackInfoReturnable<List<Component>> cir) {
        if (ChangedAddonServerConfiguration.ALLOW_SECOND_ABILITY_USE.get()) {
            List<Component> toolTips = cir.getReturnValue();
            if (toolTips != null) {
                List<Component> list = new ArrayList<>(toolTips);
                list.add(new TranslatableComponent("changed_addon.gui.abilities_radial_screen.mouse.right_click"));
                cir.setReturnValue(list);
            }
        }
    }
}
