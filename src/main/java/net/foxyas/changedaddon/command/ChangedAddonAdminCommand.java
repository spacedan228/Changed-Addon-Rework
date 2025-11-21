package net.foxyas.changedaddon.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.ability.DodgeAbilityInstance;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.stream.Collectors;

public class ChangedAddonAdminCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("changed-addon-admin")
                .requires(s -> s.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("setUltraInstinctDodge")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ChangedAddonAdminCommand::setUltraInstinctDodge)
                                )
                        )
                )
                .then(Commands.literal("showTransfursSlots")
                        .then(Commands.argument("NamespaceFormId", StringArgumentType.greedyString())
                                .suggests((context, builder) -> {
                                    String namespaceFormId = StringArgumentType.getString(context, "NamespaceFormId").toLowerCase();
                                    ArrayList<ResourceLocation> list = TransfurVariant.getPublicTransfurVariants().map(TransfurVariant::getFormId).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
                                    list.add(TransfurVariant.SPECIAL_LATEX);
                                    TreeSet<String> set = list.stream().map(ResourceLocation::getNamespace).collect(Collectors.toCollection(TreeSet::new));
                                    if (namespaceFormId.isBlank()) {
                                        list.stream().map(ResourceLocation::toString).toList().forEach(builder::suggest);
                                    } else if (namespaceFormId.startsWith("$")) {
                                        set.forEach(builder::suggest);
                                    } else {
                                        list.stream().map(ResourceLocation::toString).toList().forEach(builder::suggest);
                                    }

                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("FilterWithSlots", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            List<String> suggestions = List.of("\"\"", "no_slots", "none_slots", "with_slots");
                                            suggestions.forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(ChangedAddonAdminCommand::showTransfursSlots)
                                )
                                .executes(ChangedAddonAdminCommand::showTransfursSlots)
                        )
                )
                .then(Commands.literal("allow_boss_transfur")
                        .then(Commands.literal("Exp9")
                                .then(Commands.literal("get")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(arguments -> {
                                                    Player target = EntityArgument.getPlayer(arguments, "player");

                                                    ChangedAddonVariables.PlayerVariables vars = target.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY).resolve().orElse(null);
                                                    if (vars == null) return 0;

                                                    arguments.getSource().sendSuccess(() -> Component.literal(target.getDisplayName().getString() + (vars.Exp009TransfurAllowed ? " has Exp009Transfur permission" : " has no Exp009Transfur permission")), false);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                                .then(Commands.literal("set")
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .then(Commands.argument("set", BoolArgumentType.bool())
                                                        .executes(arguments -> {
                                                            Player target = EntityArgument.getPlayer(arguments, "target");
                                                            boolean val = BoolArgumentType.getBool(arguments, "set");

                                                            arguments.getSource().sendSuccess(() -> Component.literal(("The Exp009Transfur Perm of the " + target.getDisplayName().getString() + " was set to " + val)), true);

                                                            target.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(capability -> {
                                                                capability.Exp009TransfurAllowed = val;
                                                                capability.syncPlayerVariables(target);
                                                            });

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("Exp10")
                                .then(Commands.literal("get")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(arguments -> {
                                                    Player target = EntityArgument.getPlayer(arguments, "player");

                                                    ChangedAddonVariables.PlayerVariables vars = target.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY).resolve().orElse(null);
                                                    if (vars == null) return 0;

                                                    arguments.getSource().sendSuccess(() -> Component.literal(target.getDisplayName().getString() + (vars.Exp10TransfurAllowed ? " has Exp10Transfur permission" : " has no Exp10Transfur permission")), false);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                                .then(Commands.literal("set")
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .then(Commands.argument("set", BoolArgumentType.bool())
                                                        .executes(arguments -> {
                                                            Player target = EntityArgument.getPlayer(arguments, "target");
                                                            boolean val = BoolArgumentType.getBool(arguments, "set");

                                                            arguments.getSource().sendSuccess(() -> Component.literal(("The Exp10Transfur Perm of the " + target.getDisplayName().getString() + " was set to " + val)), true);

                                                            target.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(capability -> {
                                                                capability.Exp10TransfurAllowed = val;
                                                                capability.syncPlayerVariables(target);
                                                            });

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("SetTransfurProgress")//Add/set progress self
                        .requires(stack -> stack.getEntity() instanceof Player)
                        .then(Commands.argument("Number", DoubleArgumentType.doubleArg())
                                .then(Commands.literal("add")
                                        .executes(arguments ->
                                                setTFProgress(arguments.getSource().getPlayerOrException(), FloatArgumentType.getFloat(arguments, "Number"), true))
                                )
                                .then(Commands.literal("set")
                                        .executes(arguments ->
                                                setTFProgress(arguments.getSource().getPlayerOrException(), FloatArgumentType.getFloat(arguments, "Number"), false))
                                )
                        )
                )
                .then(Commands.literal("SetPlayerTransfurProgress")//Add/set progress other
                        .then(Commands.argument("Target", EntityArgument.player())
                                .then(Commands.argument("Number", FloatArgumentType.floatArg())
                                        .then(Commands.literal("add")
                                                .executes(arguments ->
                                                        setTFProgress(EntityArgument.getPlayer(arguments, "Target"), FloatArgumentType.getFloat(arguments, "Number"), true))
                                        )
                                        .then(Commands.literal("set")
                                                .executes(arguments ->
                                                        setTFProgress(EntityArgument.getPlayer(arguments, "Target"), FloatArgumentType.getFloat(arguments, "Number"), false))
                                        )
                                )
                        )
                )
                .then(Commands.literal("SetMaxTransfurTolerance")//Set tf tolerance other
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("Number", FloatArgumentType.floatArg(.1f))
                                        .executes(arguments ->
                                                setTFTolerance(EntityArgument.getPlayer(arguments, "target"), FloatArgumentType.getFloat(arguments, "Number")))
                                )
                                .then(Commands.literal("Default")
                                        .executes(arguments ->
                                                setTFTolerance(EntityArgument.getPlayer(arguments, "target"), 0))
                                )
                        )
                )
                .then(Commands.literal("GetMaxTransfurTolerance")//Get tf tolerance self
                        .requires(stack -> stack.getEntity() instanceof Player)
                        .executes(arguments -> {
                            ServerPlayer player = arguments.getSource().getPlayerOrException();
                            // !!! this method includes modifiers like armor, items etc
                            player.displayClientMessage(Component.literal("The maximum Transfur Tolerance is §6" + ProcessTransfur.getEntityTransfurTolerance(player)), false);
                            return Command.SINGLE_SUCCESS;
                        })
                )
//                .then(Commands.literal("setBlocksInfectionType")
//                        .then(Commands.argument("minPos", BlockPosArgument.blockPos())
//                                .then(Commands.argument("maxPos", BlockPosArgument.blockPos())
//                                        .then(Commands.literal("white_latex")
//                                                .executes(ctx -> setBlockInfection(ctx, LatexType.WHITE_LATEX))
//                                        ).then(Commands.literal("dark_latex")
//                                                .executes(ctx -> setBlockInfection(ctx, LatexType.DARK_LATEX))
//                                        ).then(Commands.literal("neutral")
//                                                .executes(ctx -> setBlockInfection(ctx, LatexType.NEUTRAL))
//                                        )
//                                )
//                        )
//                )
                // Fixme : tweak this class
        );
    }

    private static int setTFProgress(Player player, float amount, boolean add) {
        if (add) amount += ProcessTransfur.getPlayerTransfurProgress(player);

        ProcessTransfur.setPlayerTransfurProgress(player, amount);

        return Command.SINGLE_SUCCESS;
    }

    private static int setTFTolerance(Player player, float amount) {
        if (amount == 0) amount = (float) ChangedAttributes.TRANSFUR_TOLERANCE.get().getDefaultValue();

        player.getAttributes().getInstance(ChangedAttributes.TRANSFUR_TOLERANCE.get()).setBaseValue(amount);
        player.displayClientMessage(Component.literal("Transfur Tolerance has been set to §6<" + amount + ">"), false);
        ChangedAddonMod.LOGGER.info("Transfur Tolerance of {} has been set to {}", player.getDisplayName().getString(), amount);

        return Command.SINGLE_SUCCESS;
    }

//    private static int setBlockInfection(CommandContext<CommandSourceStack> ctx, LatexType enumValue) {
//        CommandSourceStack source = ctx.getSource();
//        ServerLevel world = source.getLevel();
//
//        BlockPos minPos;
//        BlockPos maxPos;
//        try {
//            minPos = BlockPosArgument.getLoadedBlockPos(ctx, "minPos");
//            maxPos = BlockPosArgument.getLoadedBlockPos(ctx, "maxPos");
//        } catch (CommandSyntaxException e) {
//            source.sendFailure(Component.literal("One or both of the selected position are not loaded!"));
//            return 0;
//        }
//
//        long value = BlockPos.betweenClosedStream(minPos, maxPos).count();
//
//        if (value > Short.MAX_VALUE) {
//            source.sendFailure(Component.literal("Too many blocks selected: " + value + " > " + Short.MAX_VALUE));
//            return 0;
//        }
//
//        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
//            BlockState state = world.getBlockState(pos);
//            if (state.hasProperty(AbstractLatexBlock.COVERED)) {
//                BlockState newState = state.setValue(AbstractLatexBlock.COVERED, enumValue);
//                world.setBlock(pos, newState, 3);
//            }
//        }
//
//        source.sendSuccess(() -> Component.literal("Set Infection of " + value + " blocks to " + enumValue.toString().toLowerCase().replace("_", " ")), true);
//        return 1;
//    }

    private static int setUltraInstinctDodge(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Collection<? extends Entity> entitiesList = EntityArgument.getEntities(context, "targets");
        boolean value = BoolArgumentType.getBool(context, "value");

        List<Component> successMessages = new ArrayList<>();
        List<Component> failureMessages = new ArrayList<>();

        for (Entity entity : entitiesList) {
            DodgeAbilityInstance dodgeAbilityInstance = null;

            if (entity instanceof ChangedEntity changedEntity) {
                dodgeAbilityInstance = changedEntity.getAbilityInstance(ChangedAddonAbilities.DODGE.get());
            } else if (entity instanceof Player player) {
                TransfurVariantInstance<?> transfurVariantInstance = ProcessTransfur.getPlayerTransfurVariant(player);
                if (transfurVariantInstance != null) {
                    dodgeAbilityInstance = transfurVariantInstance.getAbilityInstance(ChangedAddonAbilities.DODGE.get());
                }
            }

            if (dodgeAbilityInstance != null) {
                dodgeAbilityInstance.setUltraInstinct(value);
                if (value) {
                    successMessages.add(Component.translatable("changed_addon.command.ultra_instinct.enabled", entity.getName()));
                } else {
                    successMessages.add(Component.translatable("changed_addon.command.ultra_instinct.disabled", entity.getName()));
                }
            } else {
                failureMessages.add(Component.translatable("changed_addon.command.ultra_instinct.fail", entity.getName()));
            }
        }

        sendCondensedMessage(source, successMessages, true);
        sendCondensedMessage(source, failureMessages, false);

        return 1;
    }

    private static final int MAX_OUTPUT = 20; // max lines to show

    private static int showTransfursSlots(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String namespaceFormId = StringArgumentType.getString(context, "NamespaceFormId").toLowerCase().replace("$", "");
        String filter = "";
        try {
            filter = StringArgumentType.getString(context, "FilterWithSlots").toLowerCase();
        } catch (IllegalArgumentException ignored) {
        }

        List<String> validFilters = List.of("\"\"", "no_slots", "none_slots", "with_slots");
        if (!validFilters.contains(filter)) filter = "";

        String namespace = "";
        String formIdFilter = "";

        String[] parts = namespaceFormId.split(":", 2);
        if (parts.length > 0) namespace = parts[0];
        if (parts.length > 1) formIdFilter = parts[1];

        final String finalNamespace = namespace;
        final String finalFormIdFilter = formIdFilter;

        ServerLevel level = source.getLevel();

        Map<EntityType<?>, TransfurVariant<?>> variantMap = ChangedRegistry.TRANSFUR_VARIANT.get()
                .getValues().stream()
                .filter(variant -> finalNamespace.isEmpty() || variant.getFormId().getNamespace().equalsIgnoreCase(finalNamespace))
                .filter(variant -> finalFormIdFilter.isEmpty() || variant.getFormId().getPath().toLowerCase().contains(finalFormIdFilter))
                .sorted(Comparator.comparing(variant -> variant.getFormId().toString()))
                .collect(Collectors.toMap(
                        TransfurVariant::getEntityType,
                        variant -> variant,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        int count = 0;
        for (Map.Entry<EntityType<?>, TransfurVariant<?>> entry : variantMap.entrySet()) {
            if (count >= MAX_OUTPUT) {
                source.sendSuccess(() -> Component.literal("⚠ Data too large for chat, showing only " + MAX_OUTPUT + " entries.").withStyle(ChatFormatting.YELLOW), false);
                break;
            }

            EntityType<?> entityType = entry.getKey();
            TransfurVariant<?> variant = entry.getValue();

            Entity tempEntity = entityType.create(level);
            if (!(tempEntity instanceof LivingEntity livingEntity)) continue;

            Optional<AccessorySlots> optionalSlots = AccessorySlots.getForEntity(livingEntity);

            Component header = Component.literal("EntityType: ")
                    .append(Component.literal(entityType.toShortString()).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("\nTransfurVariant: "))
                    .append(Component.literal(variant.getFormId().toString()).withStyle(ChatFormatting.GOLD));

            if (optionalSlots.isPresent()) {
                AccessorySlots slots = optionalSlots.get();
                List<String> slotNames = slots.getSlotTypes()
                        .filter(s -> ChangedRegistry.ACCESSORY_SLOTS.get().getKey(s) != null)
                        .map(s -> ChangedRegistry.ACCESSORY_SLOTS.get().getKey(s).toString())
                        .toList();

                if (!slotNames.isEmpty() && (filter.contains("with_slots") || filter.isBlank())) {
                    Component slotText = Component.literal("\nSlots: ")
                            .append(Component.literal(String.join(", ", slotNames)).withStyle(ChatFormatting.GREEN))
                            .append("\n");
                    source.sendSuccess(() -> header.copy().append(slotText), false);
                } else if (slotNames.isEmpty() && (filter.contains("none_slots") || filter.isBlank())) {
                    Component noSlots = Component.literal("\nSlots: None").withStyle(ChatFormatting.DARK_GRAY).append("\n");
                    source.sendSuccess(() -> header.copy().append(noSlots), false);
                }
            } else if (filter.contains("no_slots") || filter.isBlank()) {
                Component noSlotInfo = Component.literal("\nSlots: [No AccessorySlots]").withStyle(ChatFormatting.RED).append("\n---");
                source.sendSuccess(() -> header.copy().append(noSlotInfo), false);
            }

            count++;
        }

        variantMap.clear();
        return 1;
    }

    private static void sendCondensedMessage(CommandSourceStack source, List<Component> messages, boolean success) {
        int maxLines = 6;

        if (messages.isEmpty())
            return;

        if (messages.size() <= maxLines) {
            Component full = ComponentUtils.formatList(messages, Component.literal("\n"));
            if (success)
                source.sendSuccess(() -> full, true);
            else
                source.sendFailure(full);
        } else {
            List<Component> trimmed = messages.subList(0, maxLines);
            int remaining = messages.size() - maxLines;
            trimmed.add(Component.translatable("changed_addon.command.ultra_instinct.more", remaining));
            Component full = ComponentUtils.formatList(trimmed, Component.literal("\n"));
            if (success)
                source.sendSuccess(() -> full, true);
            else
                source.sendFailure(full);
        }
    }
}
