package net.foxyas.changedaddon.ability;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbility;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public class SummonDLPupAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return !entity.isInWaterOrBubble();
    }

    protected Stream<BlockPos> findLandNearby(Level level, BlockPos near) {
        var referenceEntity = ChangedEntities.DARK_LATEX_WOLF_PUP.get().create(level);

        return BlockPos.betweenClosedStream(near.offset(-4, -2, -4), near.offset(4, 2, 4)).filter(
                pos -> {
                    assert referenceEntity != null;
                    return level.getBlockState(pos).entityCanStandOnFace(level, pos, referenceEntity, Direction.UP);
                }
        );
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        var level = entity.getLevel();
        if (level.isClientSide)
            return;

        ChangedSounds.broadcastSound(entity.getEntity(), ChangedSounds.DARK_LATEX_PUP_FORM_PUDDLE, 1.0f, 1.0f);

        var list = findLandNearby(level, entity.getBlockPosition()).toList();
        int attempts = Math.min(list.size(), 1);

        while (attempts > 0) {
            var blockPos = list.get(level.random.nextInt(list.size()));

            var pup = ChangedEntities.DARK_LATEX_WOLF_PUP.get().create(level);
            assert pup != null;
            level.addFreshEntity(pup);

            if (entity.isPlayer()) {
                pup.tame(entity.getChangedEntity().getUnderlyingPlayer());
            }

            pup.setTarget(entity.getEntity().getLastHurtByMob());
            pup.moveTo(blockPos, 0.0f, 0.0f);

            attempts--;
        }
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 40;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 5 * 60 * 20; // 5 Minutes
    }

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability.changed_addon.summon_dl_pup");
    }

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        Collection<Component> description = new ArrayList<>(super.getAbilityDescription(entity));
        description.add(Component.translatable("ability.changed_addon.summon_dl_pup.desc"));
        return description;
    }
}
