package net.foxyas.changedaddon.mixins.client.renderer;

import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemInHandRenderer.class)
public interface ItemInHandRendererAccessor {

    @Accessor("oMainHandHeight")
    float getoMainHandHeight();

    @Accessor("mainHandHeight")
    float getMainHandHeight();
}
