package net.foxyas.changedaddon.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.item.AccessoryItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static net.ltxprogrammer.changed.data.AccessorySlots.getForEntity;

public class AccessoryItemCommands {

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_ACCESSORY_SLOTS =
            (context, builder) -> {

                // 🔹 Todos os slots registrados (fallback padrão)
                List<String> allSlots = ChangedRegistry.ACCESSORY_SLOTS.get()
                        .getValues()
                        .stream()
                        .map(ForgeRegistryEntry::getRegistryName)
                        .filter(Objects::nonNull)
                        .map(ResourceLocation::toString)
                        .toList();

                // 🔹 Tenta resolver entities
                Collection<? extends Entity> entities =
                        EntityArgument.getOptionalEntities(context, "targets");

                // Nenhuma entity ainda → sugere tudo
                if (entities.isEmpty()) {
                    return SharedSuggestionProvider.suggest(allSlots, builder);
                }

                // Múltiplas entities → sugere tudo
                if (entities.size() > 1) {
                    return SharedSuggestionProvider.suggest(allSlots, builder);
                }

                // 🔹 Entidade única
                Entity entity = entities.iterator().next();

                if (!(entity instanceof LivingEntity living)) {
                    return SharedSuggestionProvider.suggest(allSlots, builder);
                }

                // 🔹 AccessorySlots é Optional
                Optional<AccessorySlots> optionalSlots = getForEntity(living);

                // Entity não tem accessory slots → fallback
                if (optionalSlots.isEmpty()) {
                    return SharedSuggestionProvider.suggest(allSlots, builder);
                }

                AccessorySlots slots = optionalSlots.get();

                // 🔹 Slots disponíveis para essa entity
                List<String> entitySlots = slots.getSlotTypes()
                        .map(ForgeRegistryEntry::getRegistryName)
                        .filter(Objects::nonNull)
                        .map(ResourceLocation::toString)
                        .toList();

                // Se por algum motivo estiver vazio, fallback
                if (entitySlots.isEmpty()) {
                    return SharedSuggestionProvider.suggest(allSlots, builder);
                }

                return SharedSuggestionProvider.suggest(entitySlots, builder);
            };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("accessory")
                        .requires(src -> src.hasPermission(2))

                        // /accessory set
                        .then(Commands.literal("set")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("slot", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ACCESSORY_SLOTS)
                                                .then(Commands.argument("item", ItemArgument.item())
                                                        .executes(AccessoryItemCommands::setAccessory)
                                                )
                                        )
                                )
                        )

                        // /accessory get
                        .then(Commands.literal("get")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("slot", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ACCESSORY_SLOTS)
                                                .executes(AccessoryItemCommands::getAccessory)
                                        )
                                )
                        )
        );
    }


    public static int setAccessory(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {

        Collection<? extends Entity> entities =
                EntityArgument.getEntities(ctx, "targets");

        boolean multiple = entities.size() > 1;

        ResourceLocation slotName = ResourceLocationArgument.getId(ctx, "slot");
        ItemStack stack = ItemArgument.getItem(ctx, "item")
                .createItemStack(1, false);

        int changed = 0;

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity living)) continue;

            Optional<AccessorySlots> optionalSlots = getForEntity(living);

            // ❌ Entity has no slots
            if (optionalSlots.isEmpty()) {
                ctx.getSource().sendFailure(
                        new TranslatableComponent(
                                multiple
                                        ? "commands.accessory.no_slots.multiple"
                                        : "commands.accessory.no_slots.single",
                                entity.getDisplayName()
                        )
                );

                if (!multiple) break;
                continue;
            }

            AccessorySlots slots = optionalSlots.get();
            AccessorySlotType slotType =
                    ChangedRegistry.ACCESSORY_SLOTS.get().getValue(slotName);

            // ❌ Slot type does not exist for entity
            if (slotType == null || slots.getSlotTypes().noneMatch(s -> s == slotType)) {
                ctx.getSource().sendFailure(
                        new TranslatableComponent(
                                multiple
                                        ? "commands.accessory.invalid_slot.multiple"
                                        : "commands.accessory.invalid_slot.single",
                                entity.getDisplayName(),
                                slotName.toString()
                        )
                );

                if (!multiple) break;
                continue;
            }

            ItemStack copy = stack.copy();

            /* ❌ Y — Slot não aceita esse item */
            if (!slotType.canHoldItem(copy, living)) {
                ctx.getSource().sendFailure(
                        new TranslatableComponent(
                                "commands.accessory.set.invalid_item",
                                entity.getDisplayName(),
                                copy.getDisplayName(),
                                slotName.toString()
                        )
                );

                if (!multiple) break;
                continue;
            }


            /* ❌ X — The Accessory is not available */
            boolean available = AccessorySlots.isSlotAvailable(living, slotType);

            if (!available) {
                ctx.getSource().sendFailure(
                        new TranslatableComponent(
                                "commands.accessory.set.slot_locked",
                                entity.getDisplayName(),
                                copy.getDisplayName(),
                                slotName.toString()
                        )
                );

                if (!multiple) break;
                continue;
            }

            /* ❌ X — Other Accessory lock the slot */
            boolean locked = canReplaceSlot(living, slotType, copy);
            if (locked) {
                ctx.getSource().sendFailure(
                        new TranslatableComponent(
                                "commands.accessory.set.locked",
                                entity.getDisplayName(),
                                copy.getDisplayName(),
                                slotName.toString()
                        )
                );

                if (!multiple) break;
                continue;
            }

            /* ✅ Success */
            slots.setItem(slotType, copy);
            changed++;

            ctx.getSource().sendSuccess(
                    new TranslatableComponent(
                            "commands.accessory.set.success",
                            entity.getDisplayName(),
                            copy.getDisplayName(),
                            slotName.toString()
                    ),
                    false
            );
        }

        if (changed == 0) {
            throw new SimpleCommandExceptionType(
                    new TextComponent("No valid accessory slots found")
            ).create();
        }

        return changed;
    }


    private static int getAccessory(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {

        Collection<? extends Entity> entities =
                EntityArgument.getEntities(ctx, "targets");

        boolean multiple = entities.size() > 1;
        ResourceLocation slotName = ResourceLocationArgument.getId(ctx, "slot");

        int found = 0;

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity living)) continue;

            Optional<AccessorySlots> optionalSlots = getForEntity(living);

            if (optionalSlots.isEmpty()) {
                ctx.getSource().sendFailure(
                        new net.minecraft.network.chat.TranslatableComponent(
                                multiple
                                        ? "commands.accessory.no_slots.multiple"
                                        : "commands.accessory.no_slots.single",
                                entity.getDisplayName()
                        )
                );

                if (!multiple) break;
                continue;
            }

            AccessorySlots slots = optionalSlots.get();
            AccessorySlotType slotType =
                    ChangedRegistry.ACCESSORY_SLOTS.get().getValue(slotName);

            if (slotType != null) found ++;

            if (slotType == null || slots.getSlotTypes().noneMatch(s -> s == slotType)) {
                ctx.getSource().sendFailure(
                        new net.minecraft.network.chat.TranslatableComponent(
                                multiple
                                        ? "commands.accessory.invalid_slot.multiple"
                                        : "commands.accessory.invalid_slot.single",
                                entity.getDisplayName(),
                                slotName.toString()
                        )
                );

                if (!multiple) break;
                continue;
            }

            Optional<ItemStack> stack = slots.getItem(slotType);

            ctx.getSource().sendSuccess(
                    new net.minecraft.network.chat.TranslatableComponent(
                            "commands.accessory.get.success",
                            entity.getDisplayName(),
                            stack.orElse(ItemStack.EMPTY).getDisplayName(),
                            slotName.toString()
                    ),
                    false
            );
        }

        if (found == 0) {
            throw new SimpleCommandExceptionType(
                    new TextComponent("No valid accessory slots found")
            ).create();
        }

        return found;
    }

    public static boolean canReplaceSlot(LivingEntity livingEntity, AccessorySlotType slot, ItemStack itemStack) {
        Optional<AccessorySlots> optionalSlots = AccessorySlots.getForEntity(livingEntity);

        if (optionalSlots.isEmpty() || !optionalSlots.get().hasSlot(slot)) {
            return false;
        }

        AccessorySlots slots = optionalSlots.get();

        if (itemStack.isEmpty()) {
            return true;
        }

        return slots.getSlotTypes()
                .filter(otherSlot -> otherSlot != slot)
                .allMatch(otherSlot -> {
                    ItemStack otherStack = slots.getItem(otherSlot).orElse(ItemStack.EMPTY);

                    if (otherStack.isEmpty()) {
                        return true;
                    }

                    Item item = itemStack.getItem();
                    if (item instanceof AccessoryItem accessoryItem) {
                        if (!accessoryItem.allowedWith(itemStack, otherStack, livingEntity, slot, otherSlot)) {
                            return false;
                        }

                        if (accessoryItem.shouldDisableSlot(new AccessorySlotContext<>(livingEntity, slot, itemStack), otherSlot
                        )) {
                            return false;
                        }
                    }

                    Item otherItem = otherStack.getItem();
                    if (otherItem instanceof AccessoryItem accessoryItem) {
                        if (!accessoryItem.allowedWith(
                                otherStack, itemStack, livingEntity, otherSlot, slot)) {
                            return false;
                        }

                        if (accessoryItem.shouldDisableSlot(new AccessorySlotContext<>(livingEntity, otherSlot, otherStack), slot)) {
                            return false;
                        }
                    }

                    return true;
                });
    }


}