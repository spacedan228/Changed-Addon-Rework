package net.foxyas.changedaddon.mixins.mods.changed;

import net.ltxprogrammer.changed.world.features.structures.facility.FacilitySinglePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FacilitySinglePiece.StructureInstance.class, remap = false)
public interface FacilitySinglePieceInstanceAccessor {

    @Accessor("templateName")
    ResourceLocation getTemplateName();

    @Accessor("generationPosition")
    BlockPos getGenerationPosition();
}
