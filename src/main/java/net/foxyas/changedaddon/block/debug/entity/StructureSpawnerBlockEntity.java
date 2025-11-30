package net.foxyas.changedaddon.block.debug.entity;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.block.interfaces.TickableBlockEntity;
import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.NotNull;

public class StructureSpawnerBlockEntity extends TickableBlockEntity {

    private ResourceLocation structureToSpawn;
    private int tickDelay = 40; // 2 segundos antes de spawnar, pode alterar

    public StructureSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedAddonBlockEntities.STRUCTURE_SPAWNER.get(), pos, state);
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState state) {
        super.tick(level, blockPos, state);
        if (level instanceof ServerLevel serverLevel) {
            serverTick(serverLevel, blockPos, state);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // -------------------
    //  MAIN SERVER TICK
    // -------------------
    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {

        if (this.structureToSpawn == null) return;

        if (this.tickDelay > 0) {
            this.tickDelay--;
            return;
        }

        // Condição extra (exemplo): só spawn à noite
        if (!level.isNight()) {
            return;
        }

        this.placeStructure(level, pos);

        // opcional: remover o bloco após o spawn
        level.removeBlock(pos, false);
    }


    // -------------------
    //   PLACE STRUCTURE
    // -------------------
    public void placeStructure(ServerLevel level, BlockPos pos) {
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

        // Para cada chunk dentro da estrutura
        ChunkPos.rangeClosed(cmin, cmax).forEach(cp -> {
            start.placeInChunk(
                    level,
                    level.structureManager(),
                    generator,
                    level.getRandom(),
                    new BoundingBox(
                            cp.getMinBlockX(), level.getMinBuildHeight(),
                            cp.getMinBlockZ(), cp.getMaxBlockX(), level.getMaxBuildHeight(), cp.getMaxBlockZ()
                    ),
                    cp
            );
        });

    }


    // -------------------
    //   SAVE NBT
    // -------------------
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (structureToSpawn != null) {
            tag.putString("Structure", structureToSpawn.toString());
        }

        tag.putInt("Delay", tickDelay);
    }

    // -------------------
    //   LOAD NBT
    // -------------------
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("Structure")) {
            structureToSpawn = ResourceLocation.parse(tag.getString("Structure"));
        }

        tickDelay = tag.getInt("Delay");
    }

    public void setStructure(ResourceLocation id) {
        this.structureToSpawn = id;
        this.tickDelay = 40; // reset
        setChanged();
    }

}
