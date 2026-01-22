package net.foxyas.changedaddon.mixins.mods.changed;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.foxyas.changedaddon.block.WhiteLatexCoverBlock;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WhiteLatexTransportInterface.EventSubscriber.class, remap = false)
public class WhiteLatexTransportInterfaceMixin {


    @WrapOperation(method = "onPlayerTick", at = @At(value = "INVOKE", target = "Lnet/ltxprogrammer/changed/block/WhiteLatexTransportInterface;isBoundingBoxInWhiteLatex(Lnet/minecraft/world/entity/LivingEntity;)Z"))
    private static boolean cancelAutoTravel(LivingEntity entity, Operation<Boolean> original) {
        boolean call = original.call(entity);
        AABB testHitbox = entity.getBoundingBox().inflate(-0.05);
        if (BlockPos.betweenClosedStream(testHitbox).anyMatch((blockPos) -> {
            BlockState blockState = entity.level.getBlockState(blockPos);
            Block block = blockState.getBlock();
            return block instanceof WhiteLatexCoverBlock;
        })) {
            return false;
        } else return call;
    }
}
