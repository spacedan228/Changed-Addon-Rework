package net.foxyas.changedaddon.mixins.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.foxyas.changedaddon.entity.advanced.LuminaraFlowerBeastEntity;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Block.class)
public class BlockMixin {

    @WrapOperation(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean cancelFallDamageForLuminara(Entity instance, float pFallDistance, float multiplier, DamageSource damageSource, Operation<Boolean> original,
                                                @Local(argsOnly = true) Level level,
                                                @Local(argsOnly = true) BlockState state,
                                                @Local(argsOnly = true) BlockPos pos) {
        Block self = (Block) (Object) this;

        if (!(self instanceof AirBlock airBlock)) {
            return original.call(instance, pFallDistance, multiplier, damageSource);
        }

        if (!(instance instanceof LivingEntity livingEntity))
            return original.call(instance, pFallDistance, multiplier, damageSource);

        Optional<IAbstractChangedEntity> iAbstractChangedEntity = IAbstractChangedEntity.forEitherSafe(livingEntity);
        if (iAbstractChangedEntity.isPresent()) {
            IAbstractChangedEntity iAbstractChanged = iAbstractChangedEntity.get();
            if (!(iAbstractChanged.getChangedEntity() instanceof LuminaraFlowerBeastEntity luminaraFlowerBeast)) {
                return original.call(instance, pFallDistance, multiplier, damageSource);
            }

            if (!luminaraFlowerBeast.isHyperAwakened() || luminaraFlowerBeast.isShiftKeyDown() || (luminaraFlowerBeast.isFlying() && luminaraFlowerBeast.isFallFlying())) {
                return original.call(instance, pFallDistance, multiplier, damageSource);
            }

            if (level.getBlockState(pos).is(Blocks.VOID_AIR)) {
                return false;
            }
        }
        return original.call(instance, pFallDistance, multiplier, damageSource);
    }

}