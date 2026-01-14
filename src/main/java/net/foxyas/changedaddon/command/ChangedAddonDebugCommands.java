package net.foxyas.changedaddon.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.foxyas.changedaddon.mixins.mods.changed.FacilitySinglePieceInstanceAccessor;
import net.foxyas.changedaddon.util.StructureUtil;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilitySinglePiece;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class ChangedAddonDebugCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("changed-addon-admin")
                        .then(Commands.literal("debug")
                                .requires(src -> src.hasPermission(Commands.LEVEL_ADMINS)) // admin only
                                .then(
                                        Commands.literal("facility")
                                                .then(
                                                        Commands.literal("hasPiece")
                                                                .then(
                                                                        Commands.argument("template", ResourceLocationArgument.id())
                                                                                .executes(ChangedAddonDebugCommands::hasFacilityPiece)
                                                                )
                                                )
                                )
                        )
        );
    }

    private static int hasFacilityPiece(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player;

        try {
            player = source.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            source.sendFailure(new TextComponent("This command must be executed by a player."));
            return 0;
        }

        ResourceLocation resourceId = ResourceLocationArgument.getId(ctx, "template");

        StructureStart structureAt = StructureUtil.getFacilityAt(
                player.getLevel(),
                player.getOnPos()
        );

        if (structureAt == null || !structureAt.isValid()) {
            source.sendFailure(new TextComponent("No facility found at your position."));
            return 0;
        }

        for (StructurePiece piece : structureAt.getPieces()) {
            if (piece instanceof FacilitySinglePiece.StructureInstance facilityPieceInstance) {

                FacilitySinglePieceInstanceAccessor accessor =
                        (FacilitySinglePieceInstanceAccessor) facilityPieceInstance;

                ResourceLocation templateName = accessor.getTemplateName();

                if (templateName.equals(resourceId)) {
                    BlockPos genPos = accessor.getGenerationPosition();
                    BlockPos center = facilityPieceInstance.getBoundingBox().getCenter();

                    MutableComponent pMessage = new TextComponent("Facility HAS piece: ").withStyle(style -> style
                            .withColor((TextColor) null)
                    ).append(new TextComponent(resourceId.toString()).withStyle(style -> style
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/changed-addon-admin debug facility hasPiece %s", resourceId)))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to copy the facility piece id")))
                                    .withColor(ChatFormatting.GREEN)
                            )
                    );
                    MutableComponent generationPos = new TextComponent("Generation Pos: ").withStyle(style -> style
                            .withColor((TextColor) null)
                    ).append(new TextComponent(genPos.toString())
                            .withStyle(style -> style
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/tp %d %d %d", genPos.getX(), genPos.getY(), genPos.getZ())))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to copy the teleport command")))
                                    .withColor(ChatFormatting.GREEN)
                            )
                    );
                    MutableComponent generationCenterPos = new TextComponent("Center: ").append(new TextComponent(center.toString())
                            .withStyle(style -> style
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/tp %d %d %d", center.getX(), center.getY(), center.getZ())))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to copy the teleport command")))
                                    .withColor(ChatFormatting.GREEN)
                            )
                    );

                    //Facility HAS piece: changed_addon:facilities/facility_rooms/alpha_cave_snow_leopard\nGeneration Pos: BlockPos{x=-2133, y=38, z=43549}\nCenter: BlockPos{x=-2150, y=52, z=43571}
//                    source.sendSuccess(
//                            pMessage,
//                            false
//                    );
//                    source.sendSuccess(
//                            generationPos,
//                            false
//                    );
//                    source.sendSuccess(
//                            generationCenterPos,
//                            false
//                    );
                    source.sendSuccess(
                            pMessage.append(generationPos).append(generationPos),
                            false
                    );
                    return 1;
                }
            }
        }

        source.sendFailure(
                new TextComponent("Facility does NOT contain piece: " + resourceId)
        );
        return 0;
    }
}
