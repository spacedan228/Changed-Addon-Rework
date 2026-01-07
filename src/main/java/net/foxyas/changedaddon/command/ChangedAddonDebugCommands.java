package net.foxyas.changedaddon.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.foxyas.changedaddon.mixins.mods.changed.FacilitySinglePieceInstanceAccessor;
import net.foxyas.changedaddon.util.StructureUtil;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilitySinglePiece;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
            source.sendFailure(Component.literal("This command must be executed by a player."));
            return 0;
        }

        ResourceLocation resourceId = ResourceLocationArgument.getId(ctx, "template");

        StructureStart structureAt = StructureUtil.getFacilityAt(
                player.serverLevel(),
                player.getOnPos()
        );

        if (structureAt == null || !structureAt.isValid()) {
            source.sendFailure(Component.literal("No facility found at your position."));
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

                    source.sendSuccess(() ->
                                    Component.literal(
                                            "Facility HAS piece: " + resourceId + "\n" +
                                                    "Generation Pos: " + genPos + "\n" +
                                                    "Center: " + center
                                    ),
                            false
                    );
                    return 1;
                }
            }
        }

        source.sendFailure(
                Component.literal("Facility does NOT contain piece: " + resourceId)
        );
        return 0;
    }
}
