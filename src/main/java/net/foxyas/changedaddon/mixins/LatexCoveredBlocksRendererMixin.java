package net.foxyas.changedaddon.mixins;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.LatexCoveredBlocksRenderer;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = LatexCoveredBlocksRenderer.class, remap = false)
public abstract class LatexCoveredBlocksRendererMixin {

    @Unique
    private static final ThreadLocal<LatexCoverGetter> threadLocal = ThreadLocal.withInitial(()-> null);

    @Inject(at = @At(value = "HEAD"), method = "getLatexCoverStateGetter", cancellable = true)
    private static void replaceGetter(CallbackInfoReturnable<Optional<LatexCoverGetter>> cir){
        cir.setReturnValue(Optional.ofNullable(threadLocal.get()));
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/ltxprogrammer/changed/client/LatexCoveredBlocksRenderer;latexCoverStateGetter:Lnet/ltxprogrammer/changed/world/LatexCoverGetter;", ordinal = 0, opcode = Opcodes.PUTSTATIC), method = "wrappedTesselate")
    private void setGetter(BlockAndTintGetter level, LatexCoverGetter latexCoverGetter, BlockPos blockPos, VertexConsumer bufferBuilder, BlockState blockState, LatexCoverState coverState, RandomSource random, CallbackInfoReturnable<Boolean> cir){
        threadLocal.set(latexCoverGetter);
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/ltxprogrammer/changed/client/LatexCoveredBlocksRenderer;latexCoverStateGetter:Lnet/ltxprogrammer/changed/world/LatexCoverGetter;", ordinal = 1, opcode = Opcodes.PUTSTATIC), method = "wrappedTesselate")
    private void clearGetter(BlockAndTintGetter level, LatexCoverGetter latexCoverGetter, BlockPos blockPos, VertexConsumer bufferBuilder, BlockState blockState, LatexCoverState coverState, RandomSource random, CallbackInfoReturnable<Boolean> cir){
        threadLocal.remove();
    }
}
