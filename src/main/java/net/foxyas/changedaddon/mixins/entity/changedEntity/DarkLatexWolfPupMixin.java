package net.foxyas.changedaddon.mixins.entity.changedEntity;

import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractLatexWolf;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfPup;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DarkLatexWolfPup.class, remap = false)
public class DarkLatexWolfPupMixin extends AbstractDarkLatexEntity{

    @Shadow protected int age;

    public DarkLatexWolfPupMixin(EntityType<? extends AbstractLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "applyCustomizeToAged", at = @At("TAIL"))
    private void syncAlphaData(AbstractDarkLatexEntity aged, CallbackInfo ci) {
        DarkLatexWolfPup self = (DarkLatexWolfPup) (Object) this;
        if (self instanceof IAlphaAbleEntity selfAlpha && aged instanceof IAlphaAbleEntity agedAlpha) {
            agedAlpha.setAlpha(selfAlpha.isAlpha());
            agedAlpha.setAlphaScale(selfAlpha.alphaAdditionalScale());
        }
    }

    @Inject(
            method = "mobInteract",
            at = @At("HEAD"),
            cancellable = true,
            remap = true
    )
    private void onWhiteLatexGooUse(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown())
            return;

        if (this.isTameItem(stack)) {
            DarkLatexWolfPup self = (DarkLatexWolfPup) (Object) this;

            if (!self.level.isClientSide) {
                this.age += 5000;

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }

            // Cancela vanilla e retorna sucesso
            cir.setReturnValue(InteractionResult.sidedSuccess(self.level.isClientSide));
        }
    }
}
