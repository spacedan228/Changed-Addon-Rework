package net.foxyas.changedaddon.client.model.clothes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HazardBodySuitLayers {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation PLAYER = ChangedAddonMod.layerLocation("hazard_body_suit", "main");
	public static final ModelLayerLocation PLAYER_SLIM = ChangedAddonMod.layerLocation("hazard_body_suit_slim", "main");
}