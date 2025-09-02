package net.foxyas.changedaddon.entity.goals.phantom;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.foxyas.changedaddon.mixins.entity.PhantomAccessor;
import net.foxyas.changedaddon.network.PacketUtil;
import net.foxyas.changedaddon.network.packets.utils.PacketsUtils;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AvoidCatlikeOrCatGoal extends Goal {
    private final Phantom phantom;
    private int nextSearchTick;

    public AvoidCatlikeOrCatGoal(Phantom phantom) {
        this.phantom = phantom;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = phantom.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (target instanceof Player player && isCatlike(player)) {
            this.hiss(player);
            return true;
        }

        return isCatlike(target) || haveCatLikeNearby();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        phantom.setTarget(null);

        if (phantom instanceof PhantomAccessor accessor) {
            accessor.setAttackPhase(Phantom.AttackPhase.CIRCLE);
        }
    }

    public void hiss(@NotNull LivingEntity cat) {
        if (cat.getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, cat.getX(), cat.getEyeY(), cat.getZ(), SoundEvents.CAT_HISS, SoundSource.AMBIENT, 1.0F, 1f);
        } else {
            cat.getLevel().playLocalSound(cat.getX(), cat.getEyeY(), cat.getZ(), SoundEvents.CAT_HISS, SoundSource.AMBIENT, 1.0F, 1.0f, false);
        }
    }

    private boolean isCatlike(LivingEntity entity) {
        if (entity instanceof Player player) {
            return ProcessTransfur.getPlayerTransfurVariantSafe(player).map(v -> v.getParent().is(ChangedAddonTags.TransfurTypes.CAT_LIKE) ||
                    v.getParent().is(ChangedAddonTags.TransfurTypes.LEOPARD_LIKE)
            ).orElse(false);
        } else if (entity instanceof ChangedEntity changed && changed.getSelfVariant() != null) {
            return changed.getSelfVariant().is(ChangedAddonTags.TransfurTypes.CAT_LIKE) ||
                    changed.getSelfVariant().is(ChangedAddonTags.TransfurTypes.LEOPARD_LIKE);
        }

        return false;
    }

    private boolean haveCatLikeNearby() {
        if (phantom.tickCount < nextSearchTick) {
            return false;
        }
        nextSearchTick = phantom.tickCount + 20;

        List<LivingEntity> catLikeNearby = phantom.level.getEntitiesOfClass(
                LivingEntity.class,
                phantom.getBoundingBox().inflate(32.0D),
                (livingEntity -> EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(livingEntity) && isCatlike(livingEntity))
                //EntitySelector.LIVING_ENTITY_STILL_ALIVE.and((entity) -> entity instanceof LivingEntity living && isCatlike(living))
        );
        catLikeNearby.forEach((this::hiss));
        return !catLikeNearby.isEmpty();
    }
}
