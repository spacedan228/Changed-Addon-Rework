package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.entity.bosses.Experiment009BossEntity;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Experiment009SpawneggItem extends Item {

    public Experiment009SpawneggItem() {
        super(new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).stacksTo(4).fireResistant().rarity(Rarity.RARE));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemstack) {
        return UseAnim.BLOCK;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack itemstack) {
        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !(context.getLevel() instanceof ServerLevel level)) return InteractionResult.PASS;

        Experiment009BossEntity entityToSpawn = new Experiment009BossEntity(ChangedAddonEntities.EXPERIMENT_009_BOSS.get(), level);
        Random random = level.getRandom();
        Direction direction = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();

        switch (direction){
            case UP -> entityToSpawn.moveTo(x + 0.5, y + 1, z + 0.5, random.nextFloat() * 360F, 0);
            case DOWN -> entityToSpawn.moveTo(x + 0.5, y - 1.5, z + 0.5, random.nextFloat() * 360F, 0);
            case EAST -> entityToSpawn.moveTo(x + 1.5, y, z + 0.5, random.nextFloat() * 360F, 0);
            case WEST -> entityToSpawn.moveTo(x - 0.5, y, z + 0.5, random.nextFloat() * 360F, 0);
            case NORTH -> entityToSpawn.moveTo(x + 0.5, y, z - 0.5, random.nextFloat() * 360F, 0);
            case SOUTH -> entityToSpawn.moveTo(x + 0.5, y, z + 1.5, random.nextFloat() * 360F, 0);
        }

        entityToSpawn.finalizeSpawn(level, level.getCurrentDifficultyAt(entityToSpawn.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
        level.addFreshEntity(entityToSpawn);

        if (!player.isCreative() && !player.isSpectator()) context.getItemInHand().shrink(1);

        level.playSound(null, player, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1, 1);
        return InteractionResult.SUCCESS;
    }
}
