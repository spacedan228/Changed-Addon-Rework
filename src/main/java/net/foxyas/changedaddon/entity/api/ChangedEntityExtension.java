package net.foxyas.changedaddon.entity.api;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.item.clothes.DyeableClothingItem;
import net.foxyas.changedaddon.item.clothes.DyeableShorts;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.item.AccessoryItem;
import net.minecraft.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public interface ChangedEntityExtension {

    default boolean isPacified() {
        return false;
    }

    default void setPacified(boolean value) {
    }

    static ChangedEntityExtension of(ChangedEntity entity) {
        return (ChangedEntityExtension) entity;
    }

    static boolean isNeutralTo(ChangedEntity entity, LivingEntity target) {
        return of(entity).c_additions$isNeutralTo(target);
    }

    boolean c_additions$isNeutralTo(LivingEntity target);

    default List<Item> getAcceptedSpawnClothes(ChangedEntity changedEntity) {
        List<Item> acceptedSpawnClothes = new ArrayList<>();
        acceptedSpawnClothes.add(ChangedItems.BENIGN_SHORTS.get());
        acceptedSpawnClothes.add(ChangedItems.BLACK_TSHIRT.get());
        if (changedEntity instanceof GenderedEntity gendered) {
            if (gendered.getGender() == Gender.FEMALE) {
                acceptedSpawnClothes.add(ChangedItems.SPORTS_BRA.get());
            }
        } else if (changedEntity.getRandom().nextFloat() <= 0.01) {
            acceptedSpawnClothes.add(ChangedItems.SPORTS_BRA.get()); // Funny Easter egg... may be removed before release
        }
        acceptedSpawnClothes.add(ChangedAddonItems.DYEABLE_SHORTS.get());
        acceptedSpawnClothes.add(ChangedAddonItems.DYEABLE_TSHIRT.get());
        return acceptedSpawnClothes;
    }

    default void setDefaultClothing(ChangedEntity changedEntity) {
        Optional<AccessorySlots> accessorySlots = AccessorySlots.getForEntity(changedEntity);
        accessorySlots.ifPresent((slots) -> {
            for (AccessorySlotType accessorySlotType : ChangedRegistry.ACCESSORY_SLOTS.get()) {
                if (slots.hasSlot(accessorySlotType)) {
                    Optional<ItemStack> item = slots.getItem(accessorySlotType);
                    if (item.isEmpty() || item.get().isEmpty()) {
                        List<Item> acceptedSpawnClothes = this.getAcceptedSpawnClothes(changedEntity);
                        if (acceptedSpawnClothes.isEmpty()) return;

                        List<Item> itemStream = acceptedSpawnClothes.stream().filter((itemType) -> itemType instanceof AccessoryItem accessoryItem).toList();

                        ItemStack stack = getRandomItemFromList(changedEntity, itemStream);
                        if (stack.getItem() instanceof AccessoryItem accessoryItem) {
                            if (accessoryItem instanceof DyeableClothingItem dyeableClothes) {
                                DyeableClothingItem.DefaultColors color = Util.getRandom(DyeableClothingItem.DefaultColors.values(), changedEntity.getRandom());
                                dyeableClothes.setColor(stack, color.getColorToInt());
                            }
                            boolean flag = accessoryItem.allowedInSlot(stack, changedEntity, accessorySlotType);
                            do stack = getRandomItemFromList(changedEntity, itemStream); while (stack.isEmpty());
                            if (flag) slots.setItem(accessorySlotType, stack);

                            if (accessorySlotType == ChangedAccessorySlots.BODY.get() || stack.is(ChangedItems.SPORTS_BRA.get())) {
                                ItemStack randomItemFromList = getRandomItemFromList(changedEntity, acceptedSpawnClothes.stream().filter((item1) -> item1 instanceof AccessoryItem accessory && accessory.allowedInSlot(new ItemStack(item1), changedEntity, ChangedAccessorySlots.LEGS.get())).toList());
                                boolean flag2 = accessoryItem.allowedInSlot(randomItemFromList, changedEntity, ChangedAccessorySlots.LEGS.get());
                                if (flag2) slots.setItem(ChangedAccessorySlots.LEGS.get(), randomItemFromList);
                            } // To Stop the Half Naked Entities To Spawn... if it spawns with only a shorts is less odd than only with a bra...
                        }
                    }
                }
            }

            /*if (slots.hasSlot(ChangedAccessorySlots.BODY.get())) {
                Optional<ItemStack> item = slots.getItem(ChangedAccessorySlots.BODY.get());
                if (item.isEmpty() || item.get().isEmpty()) {
                    ItemStack stack = new ItemStack(ChangedAddonItems.DYEABLE_TSHIRT.get());
                    if (stack.getItem() instanceof DyeableClothingItem dyeableShorts) {
                        boolean flag = dyeableShorts.allowedInSlot(stack, changedEntity, ChangedAccessorySlots.BODY.get());
                        DyeableClothingItem.DefaultColors color = Util.getRandom(DyeableClothingItem.DefaultColors.values(), changedEntity.getRandom());
                        dyeableShorts.setColor(stack, color.getColorToInt());
                        if (flag) slots.setItem(ChangedAccessorySlots.BODY.get(), stack);
                    }
                }
            }
            if (slots.hasSlot(ChangedAccessorySlots.LEGS.get())) {
                Optional<ItemStack> item = slots.getItem(ChangedAccessorySlots.LEGS.get());
                if (item.isEmpty() || item.get().isEmpty()) {
                    ItemStack stack = new ItemStack(ChangedAddonItems.DYEABLE_SHORTS.get());
                    if (stack.getItem() instanceof DyeableShorts dyeableShorts) {
                        boolean flag = dyeableShorts.allowedInSlot(stack, changedEntity, ChangedAccessorySlots.LEGS.get());
                        DyeableClothingItem.DefaultColors color = Util.getRandom(DyeableClothingItem.DefaultColors.values(), changedEntity.getRandom());
                        dyeableShorts.setColor(stack, color.getColorToInt());
                        if (flag) slots.setItem(ChangedAccessorySlots.LEGS.get(), stack);
                    }
                }
            }*/
        });
    }

    private @NotNull ItemStack getRandomItemFromList(ChangedEntity changedEntity, List<Item> itemList) {
        return new ItemStack(Util.getRandom(itemList, changedEntity.getRandom()));
    }
}
