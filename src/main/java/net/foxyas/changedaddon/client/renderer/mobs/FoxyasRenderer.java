package net.foxyas.changedaddon.client.renderer.mobs;

import net.foxyas.changedaddon.client.model.ModelFoxyasModel;
import net.foxyas.changedaddon.entity.mobs.FoxyasEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FoxyasRenderer extends MobRenderer<FoxyasEntity, ModelFoxyasModel<FoxyasEntity>> {
    public FoxyasRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelFoxyasModel(context.bakeLayer(ModelFoxyasModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FoxyasEntity entity) {
        return ResourceLocation.parse("changed_addon:textures/entities/foxyas_texture.png");
    }
}
