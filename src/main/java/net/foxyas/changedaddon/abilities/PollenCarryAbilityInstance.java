package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class PollenCarryAbilityInstance extends AbstractAbilityInstance {

    private int withPollenTicks;

    public PollenCarryAbilityInstance(AbstractAbility<PollenCarryAbilityInstance> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    public boolean canUse() {
        return this.ability.canUse(this.entity);
    }

    public boolean canKeepUsing() {
        return this.ability.canKeepUsing(this.entity);
    }

    public void startUsing() {
        LivingEntity livingEntity = entity.getEntity();
        HitResult entityBlockHitLookingAt = PlayerUtil.getEntityBlockHitLookingAt(livingEntity, livingEntity instanceof Player player ? player.getReachDistance() : 4, 1, false);
        if (entityBlockHitLookingAt.getType() != HitResult.Type.MISS && entityBlockHitLookingAt instanceof BlockHitResult blockHitResult) {
            Level level = livingEntity.getLevel();
            Item item = level.getBlockState(blockHitResult.getBlockPos()).getBlock().asItem();
            ItemStack itemStack = new ItemStack(item);
            if (itemStack.is(ItemTags.FLOWERS)) {
                withPollenTicks = 120;
            } else {
                /// TODO: Add Info About Showing why it don't work
                /// Info Should me in first person. like "i don't thing that i can get pollen from that"
                entity.displayClientMessage(new TranslatableComponent("todo"), true);
            }
        }
    }

    @Override
    public void tickIdle() {
        super.tickIdle();

        if (withPollenTicks > 0) {
            // TODO : Pollen particles should fall from the player
        }
    }

    public void tick() {
        this.ability.tick(this.entity);
    }

    public void stopUsing() {
        this.ability.stopUsing(this.entity);
    }

    public void onRemove() {
        this.ability.onRemove(this.entity);
    }

    @Override
    public void saveData(CompoundTag tag) {
        tag.putInt("withPollenTicks", withPollenTicks);
    }

    @Override
    public void readData(CompoundTag tag) {
        withPollenTicks = tag.getInt("withPollenTicks");
    }
}
