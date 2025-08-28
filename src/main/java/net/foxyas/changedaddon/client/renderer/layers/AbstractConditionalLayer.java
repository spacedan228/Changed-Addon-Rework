package net.foxyas.changedaddon.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractConditionalLayer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends RenderLayer<T, M> {

    public AbstractConditionalLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }
}
