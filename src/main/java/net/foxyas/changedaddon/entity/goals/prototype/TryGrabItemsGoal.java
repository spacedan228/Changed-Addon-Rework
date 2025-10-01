package net.foxyas.changedaddon.entity.goals.prototype;

import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class TryGrabItemsGoal extends Goal {

    private final PrototypeEntity prototype;
    private List<ItemEntity> nearbyItems;
    private int ticksTrying = 0;

    public TryGrabItemsGoal(PrototypeEntity entity) {
        this.prototype = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Only run if there is at least one item nearby to pick up
        List<ItemEntity> nearbyItems = prototype.getLevel().getEntitiesOfClass(ItemEntity.class,
                        prototype.getBoundingBox().inflate(16.0),
                        item -> !item.getItem().isEmpty())
                .stream().filter((itemEntity) -> {
                    ItemStack stack = itemEntity.getItem();
                    return prototype.canTakeItem(stack) && prototype.wantsToPickUp(stack);
                }).toList();
        this.nearbyItems = nearbyItems;
        return !nearbyItems.isEmpty() && !prototype.isInventoryFull();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && ticksTrying <= 120;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true; // We want to check every tick while active
    }

    @Override
    protected int adjustedTickDelay(int pAdjustment) {
        return super.adjustedTickDelay(0);
    }

    @Override
    public void start() {
        super.start();
        if (nearbyItems.isEmpty()) {
            return;
        }

        ItemEntity closestItem = nearbyItems.stream().filter((itemEntity) -> {
                    ItemStack stack = itemEntity.getItem();
                    return prototype.canTakeItem(stack);
                })
                .min((i1, i2) -> Double.compare(i1.distanceToSqr(prototype), i2.distanceToSqr(prototype)))
                .orElse(null);

        if (closestItem != null) {
            prototype.getLevel().playSound(null, prototype.blockPosition(), ChangedAddonSoundEvents.PROTOTYPE_IDEA, SoundSource.MASTER, 1, 1);
            prototype.getNavigation().moveTo(closestItem, 0.25f);
            // Make entity look at a target position
            prototype.getLookControl().setLookAt(
                    closestItem.position().x(), closestItem.position().y(), closestItem.position().z(),
                    30.0F, // yaw change speed (degrees per tick)
                    30.0F  // pitch change speed
            );
            ticksTrying++;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (nearbyItems.isEmpty()) {
            return;
        }

        ItemEntity closestItem = nearbyItems.stream().filter((itemEntity) -> {
                    ItemStack stack = itemEntity.getItem();
                    return prototype.canTakeItem(stack);
                })
                .min((i1, i2) -> Double.compare(i1.distanceToSqr(prototype), i2.distanceToSqr(prototype)))
                .orElse(null);

        if (closestItem != null) {
            if (closestItem.distanceTo(prototype) >= 0.005f) {
                prototype.getNavigation().moveTo(closestItem, 0.25f);
                // Place the crop block at target position
                this.prototype.getLookControl().setLookAt(
                        closestItem.position().x(), closestItem.position().y(), closestItem.position().z(),
                        30.0F, // yaw change speed (degrees per tick)
                        30.0F  // pitch change speed
                );
                ticksTrying++;
            }
        }

//        List<ItemEntity> nearbyItems = prototype.getLevel().getEntitiesOfClass(ItemEntity.class,
//                prototype.getBoundingBox().inflate(16.0),
//                item -> !item.getItem().isEmpty());
//
//        if (nearbyItems.isEmpty()) {
//            return;
//        }

    }

    @Override
    public void stop() {
        super.stop();
        ticksTrying = 0;
    }
}
