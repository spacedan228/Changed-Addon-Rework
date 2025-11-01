package net.foxyas.changedaddon.mixins;

import net.foxyas.changedaddon.block.DarkLatexPuddleBlock;
import net.foxyas.changedaddon.block.entity.DarkLatexPuddleBlockEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public Entity cameraEntity;

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    public void isEntityMovingOnWhiteLatex(Entity entity, CallbackInfoReturnable<Boolean> callback) {
        if (!(entity instanceof LivingEntity livingEntity))
            return;
        if (this.cameraEntity == null)
            return;
        if (LatexType.getEntityLatexType(this.cameraEntity) != LatexType.DARK_LATEX)
            return;
        if (LatexType.getEntityLatexType(livingEntity) != null && LatexType.getEntityLatexType(livingEntity) == LatexType.DARK_LATEX)
            return;
        BlockState feetBlockState = livingEntity.getFeetBlockState();
        if (feetBlockState.isAir())
            return;
        if (feetBlockState.getBlock() instanceof DarkLatexPuddleBlock) {
            BlockEntity blockEntity = livingEntity.getLevel().getBlockEntity(livingEntity.blockPosition());
            if (blockEntity instanceof DarkLatexPuddleBlockEntity darkLatexPuddleBlockEntity) {
                callback.setReturnValue(darkLatexPuddleBlockEntity.cooldown <= 0 && livingEntity.distanceTo(this.cameraEntity) <= 15);
            }
        }
    }
}