package net.foxyas.changedaddon.mixins.mods.changed;

import com.mojang.blaze3d.vertex.PoseStack;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.variant.TransfurVariantInstanceExtensor;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.client.gui.AbilityOverlay;
import net.ltxprogrammer.changed.client.gui.AbstractRadialScreen;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.util.Transition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.ltxprogrammer.changed.client.gui.AbilityOverlay.renderBackground;
import static net.ltxprogrammer.changed.client.gui.AbilityOverlay.renderForeground;

@Mixin(value = AbilityOverlay.class, remap = false)
public class AbilityOverlayMixin {

    @Inject(method = "lambda$renderSelectedAbility$0", at = @At("HEAD"))
    private static void renderSelectedAbility(float partialTick, int screenHeight, PoseStack stack, Player player, TransfurVariantInstance<?> variant, CallbackInfo ci) {
        if (variant instanceof TransfurVariantInstanceExtensor extensor) {
            if (!ChangedAddonServerConfiguration.ALLOW_SECOND_ABILITY_USE.get()) return;

            AbstractAbilityInstance ability = extensor.getSecondSelectedAbilityInstance();
            if (ability != null && ability.getUseType() != AbstractAbility.UseType.MENU) {
                if (!variant.isTemporaryFromSuit()) {
                    if (variant.shouldApplyAbilities()) {
                        int offset = (int) (Transition.easeInOutSine(Mth.clamp(Mth.map((float) extensor.getTicksSinceSecondAbilityActivity() + partialTick, 100.0F, 130.0F, 0.0F, 1.0F), 0.0F, 1.0F)) * 40.0F);
                        if (offset < 39) {
                            AbstractRadialScreen.ColorScheme color = AbstractRadialScreen.getColors(variant).setForegroundToBright();
                            renderBackground((10 + 32) - offset, screenHeight - 42 + offset, stack, color, player, variant, ability);
                            renderForeground((15 + 32) - offset, screenHeight - 47 + offset, stack, color, player, variant, ability);
                        }
                    }
                }
            }
        }
    }
}
