package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.world.features.processors.OffSetSpawnProcessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedAddonProcessors {

    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PROCESSOR.key(), ChangedAddonMod.MODID);

    public static final RegistryObject<StructureProcessorType<OffSetSpawnProcessor>> OFFSET_SPAWN = PROCESSORS.register("offset_spawn", () -> () -> OffSetSpawnProcessor.CODEC);
}
