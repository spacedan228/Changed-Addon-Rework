package net.foxyas.changedaddon.mixins.mods.changed;

import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.client.gui.AbilityRadialScreen;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = AbilityRadialScreen.class, remap = false)
public abstract class AbilityRadialScreenMixin {

    //    @Inject(method = "tooltipsFor", at = @At("RETURN"), remap = false, cancellable = true)
//    private void toolTipsHook(int section, CallbackInfoReturnable<List<Component>> cir) {
//        if (ChangedAddonServerConfiguration.ALLOW_SECOND_ABILITY_USE.get()) {
//            List<Component> toolTips = cir.getReturnValue();
//            if (toolTips != null) {
//                List<Component> list = new ArrayList<>(toolTips);
//                int index = -1;
//                AbilityRadialMenu menu = getSelf().getMenu();
//                for (Component component : list) {
//                    AbstractAbilityInstance abilityInstance = menu.variant.getAbilityInstance(getSelf().abilities.get(section));
//                    if (abilityInstance != null) {
//                        ResourceLocation registryName = abilityInstance.getAbility().getRegistryName();
//                        if (registryName != null) {
//                            if (component.toString().contains(registryName.toString())) {
//
//                            }
//                        }
//                    }
//                }
//
//                list.add(Component.translatable("changed_addon.gui.abilities_radial_screen.mouse.right_click"));
//                cir.setReturnValue(list);
//            }
//        }
//    }
//

    @Inject(method = "tooltipsFor", at = @At("RETURN"), remap = false, cancellable = true)
    private void toolTipsHook(int section, CallbackInfoReturnable<List<Component>> cir) {
        if (ChangedAddonServerConfiguration.ALLOW_SECOND_ABILITY_USE.get()) {
            List<Component> toolTips = cir.getReturnValue();
            if (toolTips != null) {
                List<Component> list = new ArrayList<>(toolTips);
                AbilityRadialMenu menu = getSelf().getMenu();


                boolean itAdded = false;
                for (int i = 0; i < list.size(); i++) {
                    Component component = list.get(i);
                    AbstractAbilityInstance abilityInstance = menu.variant.getAbilityInstance(getSelf().abilities.get(section));

                    if (abilityInstance != null) {
                        ResourceLocation registryName = ChangedRegistry.ABILITY.getKey(abilityInstance.getAbility());
                        if (registryName == null) {
                            list.add(Component.translatable("changed_addon.gui.abilities_radial_screen.mouse.right_click"));
                            break;
                        }
                        
                        boolean contains = component.toString().contains(registryName.toString());
                        if (contains) {
                            // BEFORE ID
                            list.add(i, Component.translatable("changed_addon.gui.abilities_radial_screen.mouse.right_click"));
                            itAdded = true;
                            break;
                        }
                    }
                }

                if (!itAdded) list.add(Component.translatable("changed_addon.gui.abilities_radial_screen.mouse.right_click"));

                cir.setReturnValue(list);
            }
        }
    }

    @Unique
    private AbilityRadialScreen getSelf() {
        return ((AbilityRadialScreen) (Object) this);
    }

//    @ModifyVariable(method = "tooltipsFor",
//            at = @At(value = "INVOKE",
//                    target = "Lcom/google/common/collect/ImmutableList$Builder;build()Lcom/google/common/collect/ImmutableList;",
//                    remap = true,
//                    shift = At.Shift.BY),
//            remap = false)
//    private ImmutableList.Builder<Component> addRightClickTip(ImmutableList.Builder<Component> builder, int section) {
//        if (ChangedAddonServerConfiguration.ALLOW_SECOND_ABILITY_USE.get()) {
//            builder.add(Component.translatable("changed_addon.gui.abilities_radial_screen.mouse.right_click"));
//                    //.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
//        }
//        return builder;
//    }
}
