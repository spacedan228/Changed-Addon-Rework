package net.foxyas.changedaddon.mixins.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.foxyas.changedaddon.ability.CarryAbility;
import net.foxyas.changedaddon.configuration.ChangedAddonClientConfiguration;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.client.FormRenderHandler.renderHand;

@Mixin(value = FormRenderHandler.class, remap = false)
public abstract class RenderHandMixin {

    @Inject(method = "maybeRenderHand", at = @At("RETURN"), cancellable = true)
    private static void renderBothHandsMixin(PlayerRenderer playerRenderer, PoseStack stack, MultiBufferSource buffer, int light,
                                             AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfoReturnable<Boolean> cir) {

        if (cir.getReturnValue() == true && shouldRenderBothHands(player, ProcessTransfur.getPlayerTransfurVariant(player))) {
            float partialTick = Minecraft.getInstance().getDeltaFrameTime();

            // Render both arms
            var rightPose = playerRenderer.getModel().rightArm.storePose();
            var leftPose = playerRenderer.getModel().leftArm.storePose();

            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                if (arm == playerRenderer.getModel().leftArm) {
                    stack.pushPose();
                    stack.translate(0.4f, -0.45f, 1f);
                    stack.mulPose(new Quaternion(30, 10, -50f, true));
                    renderHand(variant.getChangedEntity(), HumanoidArm.RIGHT, leftPose, stack, buffer, light, partialTick);
                    stack.popPose();
                } else {
                    stack.pushPose();
                    stack.translate(-0.3, -0.4, -0.7);
                    stack.mulPose(new Quaternion(30, 10, 30, true));
                    renderHand(variant.getChangedEntity(), HumanoidArm.LEFT, rightPose, stack, buffer, light, partialTick);
                    stack.popPose();
                }

            });
        }


        //cir.cancel(); // Prevent the original method from running
    }


    /**
     * Determine if both hands should be rendered based on the player's state and abilities.
     */
    @Unique
    private static boolean shouldRenderBothHands(Player player, TransfurVariantInstance<?> variantInstance) {
        if (variantInstance == null) {
            return false;
        }

        if (!ChangedAddonClientConfiguration.SHOW_EXTRA_HAND.get()) {
            return false;
        }

        // Check if the player is gliding with a variant that can glide and has sufficient speed
        if (variantInstance.getParent().canGlide && player.isFallFlying()) {
            double speed = player.getDeltaMovement().length(); // Velocidade do jogador
            if (speed > 1.5 || player.getFallFlyingTicks() >= 5) {
                return true;
            }
        }

        // Check if the player has the Carry ability and is carrying a valid entity
        if (variantInstance.hasAbility(ChangedAddonAbilities.CARRY.get())
                && variantInstance.selectedAbility == ChangedAddonAbilities.CARRY.get()) {
            CarryAbility carryAbility = (CarryAbility) variantInstance.getAbilityInstance(ChangedAddonAbilities.CARRY.get()).ability;
            Entity carryTarget = carryAbility.CarryTarget(player);

            if (carryTarget instanceof LivingEntity) {
                boolean isHumanoidOrTagValid = carryTarget.getType().is(ChangedTags.EntityTypes.HUMANOIDS) ||
                        carryTarget.getType().is(ChangedAddonTags.EntityTypes.CAN_CARRY);

                return isHumanoidOrTagValid && (player.getFirstPassenger() == null || player.getFirstPassenger() != carryTarget);
            }
        }

        return false;
    }
}
