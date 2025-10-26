package net.foxyas.changedaddon.mixins.mods.changed;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.network.packets.VariantSecondAbilityActivate;
import net.foxyas.changedaddon.variants.TransfurVariantInstanceExtensor;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.client.gui.AbilityRadialScreen;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.network.VariantAbilityActivate;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.ibm.icu.impl.ValidIdentifiers.Datatype.variant;

@Mixin(value = AbilityRadialScreen.class, remap = false)
public abstract class AbilityRadialScreenMixin {

    @Shadow @Final public List<AbstractAbility<?>> abilities;

    @Shadow @Final public TransfurVariantInstance<?> variant;

    @Shadow @Final public AbilityRadialMenu menu;

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


    @Inject(method = "handleClicked", at = @At("TAIL"), remap = false, cancellable = true)
    public void handleClicked(int section, SingleRunnable close, CallbackInfoReturnable<Boolean> cir) {
        close.run();
        AbstractAbility<?> ability = this.abilities.get(section);
        if (variant instanceof TransfurVariantInstanceExtensor variantInstanceExtensor) {
            variantInstanceExtensor.setSecondSelectedAbility(ability);
            ChangedAddonMod.PACKET_HANDLER.sendToServer(new VariantSecondAbilityActivate(this.menu.player, variantInstanceExtensor.getSecondAbilityKeyState(), ability));
            cir.setReturnValue(false);
        }
    }
}
