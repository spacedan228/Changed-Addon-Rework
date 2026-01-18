package net.foxyas.changedaddon.mixins.entity;

import net.foxyas.changedaddon.event.TransfurEvents;
import net.foxyas.changedaddon.util.FoxyasUtils;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexWolfMale;
import net.ltxprogrammer.changed.entity.beast.boss.Behemoth;
import net.ltxprogrammer.changed.init.ChangedEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LightningBolt.class)
public abstract class LightingBoltMixin extends Entity {

    @Shadow
    protected abstract BlockPos getStrikePosition();

    public LightingBoltMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "spawnFire",
            at = @At("HEAD"),
            cancellable = true
    )
    private void latexCoverIsStruckByLighting(CallbackInfo ci) {
        BlockPos strikePos = this.getStrikePosition();
        Level level = this.level;

        if (!(level instanceof ServerLevel serverLevel))
            return;

        BlockState strikeState = level.getBlockState(strikePos);

        if (AbstractLatexBlock.isLatexed(strikeState)
                && AbstractLatexBlock.getLatexed(strikeState) == LatexType.WHITE_LATEX) {
            onWhiteLatexStruck(serverLevel, strikePos, (LightningBolt) (Object) this);
        }
    }

    @Unique
    private void onWhiteLatexStruck(ServerLevel level, BlockPos strikePos, LightningBolt bolt) {
        int radius = 32;

        AABB area = new AABB(strikePos).inflate(radius);

        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                living -> {
                    if (living instanceof ChangedEntity changedEntity) {
                        Entity entity = TransfurEvents.resolveChangedEntity(changedEntity.maybeGetUnderlying());
                        if (entity instanceof WhiteLatexWolfMale) {
                            return entity.isAlive();
                        } else if (entity instanceof Behemoth) {
                            return entity.isAlive();
                        }
                    }

                    return living.isAlive();
                }
        );

        for (LivingEntity entity : entities) {
            if (isWhiteLatexEntityConnected(level, strikePos, entity)) {
                entity.addEffect(new MobEffectInstance(
                        ChangedEffects.SHOCK,
                        100, // 5 segundos
                        0,
                        false,
                        true
                ));
            }
        }
    }

    @Unique
    private boolean isWhiteLatexEntityConnected(
            ServerLevel level,
            BlockPos strikePos,
            LivingEntity entity
    ) {
        AABB box = entity.getBoundingBox();

        BlockPos min = new BlockPos(
                Mth.floor(box.minX),
                Mth.floor(box.minY),
                Mth.floor(box.minZ)
        );

        BlockPos max = new BlockPos(
                Mth.floor(box.maxX),
                Mth.floor(box.maxY),
                Mth.floor(box.maxZ)
        );

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);

            if (AbstractLatexBlock.isLatexed(state)
                    && AbstractLatexBlock.getLatexed(state) == LatexType.WHITE_LATEX) {

                if (FoxyasUtils.isConnectedByLatex(
                        level,
                        strikePos,
                        pos,
                        LatexType.WHITE_LATEX,
                        64
                )) {
                    return true;
                }
            }
        }

        return false;
    }


}
