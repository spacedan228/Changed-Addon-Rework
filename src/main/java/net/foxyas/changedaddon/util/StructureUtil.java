package net.foxyas.changedaddon.util;

import net.ltxprogrammer.changed.init.ChangedStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class StructureUtil {

    /**
     * Gets the structure start at a specific position in the world using its structure ID.
     *
     * @param level       the server level
     * @param pos         the block position to check
     * @param structureId the ID of the structure (e.g., "minecraft:village")
     * @return the StructureStart if found, or null if not present
     */
    public static StructureStart getStructureAt(ServerLevel level, BlockPos pos, ResourceLocation structureId) {
        ConfiguredStructureFeature<?, ?> structure = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.get(structureId);
        return structure == null ? null : level.structureFeatureManager().getStructureAt(pos, structure);
    }

    /**
     * Gets the facility as a structure start at a specific position in the world using its structure ID.
     *
     * @param level       the server level
     * @param pos         the block position to check
     * @return the StructureStart if found, or null if not present
     */
    public static StructureStart getFacilityAt(ServerLevel level, BlockPos pos) {
        return level.structureFeatureManager().getStructureAt(pos, ChangedStructures.FACILITY.value());
    }

    /**
     * Checks if a structure can generate within a given chunk radius around a position.
     *
     * @param level      the server level
     * @param pos        the position to check
     * @param structure  the ResourceKey of the structure set
     * @param chunkRange the radius (in chunks) to search for the structure
     * @return true if the structure can generate in the area, false otherwise
     */
    public static boolean isStructureNearby(ServerLevel level, BlockPos pos, ResourceKey<StructureSet> structure, int chunkRange) {
        return level.getChunkSource().getGenerator().hasFeatureChunkInRange(
                structure,
                level.getSeed(),
                SectionPos.blockToSectionCoord(pos.getX()),
                SectionPos.blockToSectionCoord(pos.getZ()),
                chunkRange
        );
    }

    /**
     * Checks if a structure can generate within a given chunk radius around a position.
     *
     * @param level       the server level
     * @param pos         the position to check
     * @param structureId the string ID of the structure (e.g., "changed_addon:dazed_meteor")
     * @param chunkRange  the radius (in chunks) to search for the structure
     * @return true if the structure can generate in the area, false otherwise
     */
    public static boolean isStructureNearby(ServerLevel level, BlockPos pos, String structureId, int chunkRange) {
        ResourceKey<StructureSet> structureKey = ResourceKey.create(
                BuiltinRegistries.STRUCTURE_SETS.key(),
                ResourceLocation.parse(structureId)
        );
        return isStructureNearby(level, pos, structureKey, chunkRange);
    }
}
