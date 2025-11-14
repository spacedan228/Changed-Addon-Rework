package net.foxyas.changedaddon.mixins.mods.nean;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.animations.vanilla.ElytraAnimation;
import dev.tr7zw.notenoughanimations.versionless.animations.BodyPart;
import net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ElytraAnimation.class, remap = false)
@RequiredMods("notenoughanimations")
public class NEAElytraAnimationMixin {
    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void injectAvaliAnimation(AbstractClientPlayer entity, PlayerData data, PlayerModel<AbstractClientPlayer> model, BodyPart part, float delta, float tickCounter, CallbackInfo ci) {
        var tf = ProcessTransfur.getPlayerTransfurVariant(entity);
        if (tf != null && tf.is(ChangedAddonTransfurVariants.AVALI)) {
            // Aplicamos uma curva ease-in-out para suavizar o início e fim
            float t = Mth.clamp(tickCounter * 0.6662F, 0.0F, 1.0F);
            float flyAmount = smootherStep(t); // Muito mais suave!

            // float old2_flyAmount = t * t * (3.0F - 2.0F * t); // Smoothstep

            // float old_flyAmount = Mth.clamp(ticks * ticks / 100.0F, 0.0F, 1.0F); // Suavização

            float targetY = (float) Math.toRadians(90);
            float targetZ = (float) Math.toRadians(90);

            if (part == BodyPart.LEFT_ARM) {
                model.leftArm.yRot = Mth.lerp(flyAmount, model.leftArm.yRot, -targetY);
                model.leftArm.zRot = Mth.lerp(flyAmount, model.leftArm.zRot, -targetZ);
                ci.cancel();
            } else if (part == BodyPart.RIGHT_ARM) {
                model.rightArm.yRot = Mth.lerp(flyAmount, model.rightArm.yRot, targetY);
                model.rightArm.zRot = Mth.lerp(flyAmount, model.rightArm.zRot, targetZ);
                ci.cancel();
            }
        }
    }


    @Unique
    private float smootherStep(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }
}
