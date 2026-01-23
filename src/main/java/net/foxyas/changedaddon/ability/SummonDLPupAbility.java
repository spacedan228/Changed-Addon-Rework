package net.foxyas.changedaddon.ability;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbility;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexEntity;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class SummonDLPupAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        if (entity.getEntity() instanceof Player player && player.getFoodData().getFoodLevel() < 8.0) {
            return false;
        }

        return !entity.isInWaterOrBubble();
    }

    protected Stream<BlockPos> findLandNearby(Level level, BlockPos near) {
        var referenceEntity = ChangedEntities.DARK_LATEX_WOLF_PUP.get().create(level);
        assert referenceEntity != null;

        return BlockPos.betweenClosedStream(near.offset(-4, -2, -4), near.offset(4, 2, 4)).map(BlockPos::immutable).filter(
                pos -> level.getBlockState(pos.offset(0, -1, 0)).entityCanStandOnFace(level, pos, referenceEntity, Direction.UP) && level.getBlockState(pos).isAir()
        );
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        var level = entity.getLevel();
        if (level.isClientSide)
            return;

        var livingEntity = entity.getEntity();

        List<AbstractDarkLatexEntity> nearbyDarkLatexEntities = level.getEntitiesOfClass(
                AbstractDarkLatexEntity.class,
                livingEntity.getBoundingBox().inflate(70.0),
                e -> e != livingEntity
        );

        if (nearbyDarkLatexEntities.size() > 3)
            return; // no army for you

        var possibleSpawnPositions = findLandNearby(level, entity.getBlockPosition()).toList();
        int attempts = Math.min(possibleSpawnPositions.size(), 1);

        while (attempts > 0) {
            var blockPos = possibleSpawnPositions.get(level.random.nextInt(possibleSpawnPositions.size()));

            var pup = ChangedEntities.DARK_LATEX_WOLF_PUP.get().create(level);
            assert pup != null;

            if (entity.getEntity() instanceof Player player) {
                pup.tame(player);
            }

            pup.setTarget(livingEntity.getLastHurtByMob());
            pup.moveTo(blockPos, 0.0F, 0.0F);
            level.addFreshEntity(pup);

            attempts--;
        }

        if (entity.getEntity() instanceof Player player) {
            player.causeFoodExhaustion((float)30.0);
        }

        ChangedSounds.broadcastSound(livingEntity, ChangedSounds.POISON, 1.0f, 1.0f);
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
        return 4 * 60 * 20; // 4 Minutes
    }

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return new TranslatableComponent("ability.changed_addon.summon_dl_pup");
    }

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        Collection<Component> description = new ArrayList<>(super.getAbilityDescription(entity));
        description.add(new TranslatableComponent("ability.changed_addon.summon_dl_pup.desc"));
        return description;
    }
}
