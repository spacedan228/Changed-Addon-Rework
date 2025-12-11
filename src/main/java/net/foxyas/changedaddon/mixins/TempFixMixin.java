//package net.foxyas.changedaddon.mixins;
//
//import net.ltxprogrammer.changed.init.ChangedLatexTypes;
//import net.ltxprogrammer.changed.world.LatexCoverState;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
//import net.minecraft.client.renderer.chunk.RenderChunkRegion;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
////TODO remove when changedmc fixes this (also accesstransformer)
////@Mixin(value = {ChunkRenderDispatcher.RenderChunk.RebuildTask.class}, priority = 900)
//public abstract class TempFixMixin {
//
////    @Unique
////    public LatexCoverState getLatexCoverState(RenderChunkRegion region, BlockPos blockPos) {
////        Level level = Minecraft.getInstance().level;
////        if(level == null) return ChangedLatexTypes.NONE.get().defaultCoverState();
////        return LatexCoverState.getAt(level, blockPos);
////    }
//}
