package net.foxyas.changedaddon.client.renderer.renderTypes;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

import static net.minecraft.client.renderer.RenderType.OutlineProperty.IS_OUTLINE;

// BlakeBr0 Code
// https://github.com/BlakeBr0/Cucumber/blob/1.18/src/main/java/com/blakebr0/cucumber/client/ModRenderTypes.java
public final class ChangedAddonRenderTypes extends RenderType {

    public static final RenderType QUADS_WITH_TRANSPARENCY = RenderType.create(
            ChangedAddonMod.resourceLocString("quads"),
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            2097152,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_SOLID_SHADER)
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .createCompositeState(true)
    );

    public static final RenderType QUADS_WITH_TRANSPARENCY_NO_CULL = RenderType.create(
            ChangedAddonMod.resourceLocString("quads_no_cull"),
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            2097152,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_SOLID_SHADER)
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .createCompositeState(true)
    );

    public static final RenderType QUADS = RenderType.create(
            ChangedAddonMod.resourceLocString("quads"),
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            2097152,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_SOLID_SHADER)
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .createCompositeState(true)
    );

    public static final RenderType QUADS_NO_CULL = RenderType.create(
            ChangedAddonMod.resourceLocString("quads_no_cull"),
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            2097152,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_SOLID_SHADER)
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .createCompositeState(true)
    );

    private static final TransparencyStateShard GHOST_TRANSPARENCY = new TransparencyStateShard("ghost_transparency",
            () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
                GL14.glBlendColor(1.0F, 1.0F, 1.0F, 0.25F);
            },
            () -> {
                GL14.glBlendColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            });

    public static final RenderType GHOST = RenderType.create(
            ChangedAddonMod.resourceLocString("ghost"),
            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_SOLID_SHADER)
                    .setTextureState(BLOCK_SHEET)
                    .setTransparencyState(GHOST_TRANSPARENCY)
                    .createCompositeState(false)
    );

    private static final TransparencyStateShard HOLOGRAM_TRANSPARENCY = new TransparencyStateShard("hologram_transparency",
            () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
                GL14.glBlendColor(1.0F, 1.0F, 1.0F, 0.5F);
            },
            () -> {
                GL14.glBlendColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            });

    private static final BiFunction<ResourceLocation, Boolean, RenderType> QUADS_NO_CULL_WITH_TEXTURE = Util.memoize((resourceLocation, transparency) -> {
        CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setLightmapState(LIGHTMAP)
                .setShaderState(RENDERTYPE_SOLID_SHADER)
                .setTransparencyState(transparency ? RenderStateShard.TRANSLUCENT_TRANSPARENCY : RenderStateShard.NO_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setTextureState(resourceLocation == null ? BLOCK_SHEET_MIPPED : new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .createCompositeState(true);
        return create(ChangedAddonMod.resourceLocString("quads_no_cull_with_texture"),
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                256,
                true,
                false,
                rendertype$compositestate);
    });

    private static final BiFunction<ResourceLocation, Boolean, RenderType> HOLOGRAM = Util.memoize((resourceLocation, outline) -> {
        CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(HOLOGRAM_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(outline);
        return create(ChangedAddonMod.resourceLocString("hologram"),
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                true,
                true,
                rendertype$compositestate);
    });

    private static final BiFunction<ResourceLocation, Boolean, RenderType> HOLOGRAM_CULL = Util.memoize((resourceLocation, outline) -> {
        CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(HOLOGRAM_TRANSPARENCY)
                .setCullState(CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(outline);
        return create(ChangedAddonMod.resourceLocString("hologram_cull"),
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                true,
                true,
                rendertype$compositestate);
    });

    public static final BiFunction<ResourceLocation, RenderStateShard.CullStateShard, RenderType> OUTLINE_WITH_DEPTH = Util.memoize((resourceLocation, cullStateShard) ->
            create(ChangedAddonMod.resourceLocString("outline_with_deep_test"),
                    DefaultVertexFormat.POSITION_COLOR_TEX,
                    VertexFormat.Mode.QUADS,
                    256,
                    false,
                    false,
                    RenderType.CompositeState.builder()
                            .setShaderState(RENDERTYPE_OUTLINE_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                            .setCullState(cullStateShard)
                            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                            .setOutputState(MAIN_TARGET)
                            .createCompositeState(IS_OUTLINE)));

    public static final CullStateShard OUTLINE_CULL_STATE = new RenderStateShard.CullStateShard(true) { // culling invertido
        @Override
        public void setupRenderState() {
            RenderSystem.enableCull();
            GL11.glCullFace(GL11.GL_FRONT);
        }

        @Override
        public void clearRenderState() {
            GL11.glCullFace(GL11.GL_BACK);
        }
    };

    // unused, just needed to extend RenderType for protected constants
    private ChangedAddonRenderTypes(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    public static RenderType QuadsNoCullTexture(@Nullable ResourceLocation resourceLocation, boolean transparency) {
        return QUADS_NO_CULL_WITH_TEXTURE.apply(resourceLocation, transparency);
    }

    public static RenderType hologram(@NotNull ResourceLocation resourceLocation, boolean outline) {
        return HOLOGRAM.apply(resourceLocation, outline);
    }

    public static RenderType hologramCull(@NotNull ResourceLocation resourceLocation, boolean outline) {
        return HOLOGRAM_CULL.apply(resourceLocation, outline);
    }

    public static RenderType outlineWithDepth(ResourceLocation location) {
        return OUTLINE_WITH_DEPTH.apply(location, OUTLINE_CULL_STATE);
    }

    public static RenderType outlineWithDepthFull(ResourceLocation location) {
        return OUTLINE_WITH_DEPTH.apply(location, NO_CULL);
    }
}