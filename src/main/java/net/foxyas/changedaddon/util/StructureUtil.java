package net.foxyas.changedaddon.util;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class StructureUtil {

    /**
     * Verifica se uma estrutura pode gerar dentro de um determinado raio de chunks.
     *
     * @param level        o ServerLevel
     * @param pos          a posição a ser verificada
     * @param structureKey ResourceKey da estrutura
     * @return true se a estrutura pode gerar na área, false caso contrário.
     */
    public static boolean isStructureAt(ServerLevel level, BlockPos pos, ResourceKey<Structure> structureKey) {
        return level.structureManager().getStructureAt(pos, level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(structureKey).get()).isValid();
    }


    /**
     * Simple Util to Avoid Reusing the same code over and over again
     *
     * @param level        o ServerLevel
     * @param pos          a posição a ser verificada
     * @param structureKey ResourceKey da estrutura
     * @return An StructureStart from a positions based on it key
     */
    public static StructureStart getStructureAt(ServerLevel level, BlockPos pos, ResourceKey<Structure> structureKey) {
        return level.structureManager().getStructureAt(pos, level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(structureKey).get());
    }

    /**
     * Gets the facility as a structure start at a specific position in the world using its structure ID.
     *
     * @param level the server level
     * @param pos   the block position to check
     * @return the StructureStart if found, or null if not present
     */
    public static StructureStart getFacilityAt(ServerLevel level, BlockPos pos) {
        ResourceKey<Structure> structureKey = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.parse("changed:facility"));
        return getStructureAt(level, pos, structureKey);
    }

    /**
     * Verifica se uma estrutura pode gerar dentro de um determinado raio de chunks.
     *
     * @param level       o ServerLevel
     * @param pos         a posição a ser verificada
     * @param structureId o ID da estrutura desejada (ex.: "changed_additions:dazed_meteor")
     * @param chunkRange  o raio de chunks a ser verificado
     * @return true se a estrutura pode gerar na área, false caso contrário.
     */
    public static boolean isStructureNearby(ServerLevel level, BlockPos pos, String structureId, int chunkRange) {
        ResourceKey<Structure> structureKey = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.parse(structureId));
        return isStructureAt(level, pos, structureKey);
    }


    public static int placeStructure(ServerLevel serverLevel, Holder.Reference<Structure> pStructure, BlockPos pPos) {
        Structure structure = pStructure.value();
        ChunkGenerator chunkgenerator = serverLevel.getChunkSource().getGenerator();
        StructureStart structurestart = structure.generate(serverLevel.registryAccess(), chunkgenerator, chunkgenerator.getBiomeSource(), serverLevel.getChunkSource().randomState(), serverLevel.getStructureManager(), serverLevel.getSeed(), new ChunkPos(pPos), 0, serverLevel, (biomeHolder) -> true);
        if (!structurestart.isValid()) {
            return 0;
        } else {
            BoundingBox boundingbox = structurestart.getBoundingBox();
            ChunkPos chunkpos = new ChunkPos(SectionPos.blockToSectionCoord(boundingbox.minX()), SectionPos.blockToSectionCoord(boundingbox.minZ()));
            ChunkPos chunkpos1 = new ChunkPos(SectionPos.blockToSectionCoord(boundingbox.maxX()), SectionPos.blockToSectionCoord(boundingbox.maxZ()));
            if (!checkLoaded(serverLevel, chunkpos, chunkpos1)) {
                return 0;
            }

            ChunkPos.rangeClosed(chunkpos, chunkpos1).forEach((chunkPos) -> {
                structurestart.placeInChunk(serverLevel, serverLevel.structureManager(), chunkgenerator, serverLevel.getRandom(), new BoundingBox(chunkPos.getMinBlockX(), serverLevel.getMinBuildHeight(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), serverLevel.getMaxBuildHeight(), chunkPos.getMaxBlockZ()), chunkPos);
            });

            //String s = pStructure.key().location().toString();
            //pSource.sendSuccess(() -> Component.translatable("commands.place.structure.success", s, pPos.getX(), pPos.getY(), pPos.getZ()), true);
            return 1;
        }
    }

    // -------------------
    //   PLACE STRUCTURE
    // -------------------
    public static void placeStructure(ServerLevel level, ResourceLocation structureToSpawn, BlockPos pos, boolean forceLoadChunk) {
        Holder.Reference<Structure> ref = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getHolder(ResourceKey.create(Registries.STRUCTURE, structureToSpawn))
                .orElse(null);

        if (ref == null) {
            ChangedAddonMod.LOGGER.error("Structure '{}' not found!", structureToSpawn);
            return;
        }

        Structure structure = ref.value();

        ChunkGenerator generator = level.getChunkSource().getGenerator();

        StructureStart start = structure.generate(
                level.registryAccess(),
                generator,
                generator.getBiomeSource(),
                level.getChunkSource().randomState(),
                level.getStructureManager(),
                level.getSeed(),
                new ChunkPos(pos),
                0,
                level,
                (p) -> true
        );

        if (!start.isValid()) {
            ChangedAddonMod.LOGGER.warn("Structure '{}' failed to generate at {}", structureToSpawn, pos);
            return;
        }

        BoundingBox box = start.getBoundingBox();

        ChunkPos cmin = new ChunkPos(
                SectionPos.blockToSectionCoord(box.minX()),
                SectionPos.blockToSectionCoord(box.minZ())
        );

        ChunkPos cmax = new ChunkPos(
                SectionPos.blockToSectionCoord(box.maxX()),
                SectionPos.blockToSectionCoord(box.maxZ())
        );

        if (!checkLoaded(level, cmin, cmax) && !forceLoadChunk) {
            return;
        }

        // Para cada chunk dentro da estrutura
        ChunkPos.rangeClosed(cmin, cmax).forEach(cp -> start.placeInChunk(
                level,
                level.structureManager(),
                generator,
                level.getRandom(),
                new BoundingBox(
                        cp.getMinBlockX(), level.getMinBuildHeight(),
                        cp.getMinBlockZ(), cp.getMaxBlockX(), level.getMaxBuildHeight(), cp.getMaxBlockZ()
                ),
                cp
        ));

    }

    private static boolean checkLoaded(ServerLevel pLevel, ChunkPos pStart, ChunkPos pEnd) {
        return (ChunkPos.rangeClosed(pStart, pEnd).anyMatch((pos) -> !pLevel.isLoaded(pos.getWorldPosition())));
    }
}
