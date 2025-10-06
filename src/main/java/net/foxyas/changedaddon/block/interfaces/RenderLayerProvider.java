package net.foxyas.changedaddon.block.interfaces;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface RenderLayerProvider {

    @OnlyIn(Dist.CLIENT)
    default void registerRenderLayer(){}
}
