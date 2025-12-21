package net.foxyas.changedaddon.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;

public class RenderUtil {

    private static final int FULL_LIGHT = LightTexture.FULL_BRIGHT; // Lightmap value for full brightness

    /**
     * Renders a path line with world-space anchoring and HSV gradient.
     */
    public static void renderPathAsLine(PoseStack poseStack, Vec3 camPos, Path path) {
        poseStack.pushPose();

        // Anchor the rendering to world coordinates 0,0,0
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(5.0f);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < path.getNodeCount(); ++i) {
            Node node = path.getNode(i);

            // Generate a gradient based on the node index
            float f = (float) i / (float) path.getNodeCount() * 0.33F;
            int colorRGB = i == 0 ? 0x00FF00 : Mth.hsvToRgb(f, 0.9F, 0.9F);
            int r = (colorRGB >> 16) & 255;
            int g = (colorRGB >> 8) & 255;
            int b = colorRGB & 255;

            // Render vertex with a small Y offset to prevent Z-fighting with the floor
            bufferbuilder.vertex(poseStack.last().pose(),
                            (float) node.x + 0.5f,
                            (float) node.y + 0.1f,
                            (float) node.z + 0.5f)
                    .color(r, g, b, 255)
                    .endVertex();
        }

        // Restore states to prevent graphical glitches in other parts of the game
        tesselator.end();
        poseStack.popPose();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawCentered(Font font, PoseStack stack, Component comp, float x, float y, int color) {
        int width = font.width(comp);
        font.draw(stack, comp, x - width / 2f, y - 4, color);
    }

    public static void drawCentered(Font font, PoseStack stack, String str, float x, float y, int color) {
        int width = font.width(str);
        font.draw(stack, str, x - width / 2f, y - 4, color);
    }

    public static void fill(PoseStack stack, float minX, float minY, float maxX, float maxY, int color) {
        fill(stack.last().pose(), minX, minY, maxX, maxY, color);
    }

    public static void fill(Matrix4f pMatrix, float minX, float minY, float maxX, float maxY, int color) {
        if (minX < maxX) {
            float i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY) {
            float j = minY;
            minY = maxY;
            maxY = j;
        }

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(pMatrix, minX, maxY, 0.0F).color(color).endVertex();
        bufferbuilder.vertex(pMatrix, maxX, maxY, 0.0F).color(color).endVertex();
        bufferbuilder.vertex(pMatrix, maxX, minY, 0.0F).color(color).endVertex();
        bufferbuilder.vertex(pMatrix, minX, minY, 0.0F).color(color).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    /**
     * Renders a single vertex with full light and no overlay.
     *
     * @param builder      The vertex consumer
     * @param poseMatrix   The pose matrix (4x4)
     * @param normalMatrix The normal matrix (3x3)
     * @param x            X position
     * @param y            Y position
     * @param z            Z position
     * @param r            Red (0–255)
     * @param g            Green (0–255)
     * @param b            Blue (0–255)
     * @param u            Texture U coordinate
     * @param v            Texture V coordinate
     */
    public static void vertex(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                              float x, float y, float z, int r, int g, int b, float u, float v) {
        builder.vertex(poseMatrix, x, y, z)
                .color(r, g, b, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_LIGHT)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    /**
     * Same as vertex(), but allows setting alpha.
     */
    public static void vertex(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                              float x, float y, float z, int r, int g, int b, int a, float u, float v) {
        builder.vertex(poseMatrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_LIGHT)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    /**
     * Advanced version allowing control over light and overlay.
     */
    public static void vertex(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                              float x, float y, float z, int r, int g, int b, int a, float u, float v,
                              int overlay, int light) {
        builder.vertex(poseMatrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    /**
     * Draws a flat quad on the XZ plane (horizontal floor) facing upward (Y+).
     *
     * @param origin The bottom-left corner of the quad
     * @param width  Width along the X axis
     * @param depth  Depth along the Z axis
     */
    public static void drawQuadXZ(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                  Vec3 origin, float width, float depth, int r, int g, int b) {
        float x = (float) origin.x;
        float y = (float) origin.y;
        float z = (float) origin.z;

        vertex(builder, poseMatrix, normalMatrix, x, y, z, r, g, b, 0f, 1f);
        vertex(builder, poseMatrix, normalMatrix, x + width, y, z, r, g, b, 1f, 1f);
        vertex(builder, poseMatrix, normalMatrix, x + width, y, z + depth, r, g, b, 1f, 0f);
        vertex(builder, poseMatrix, normalMatrix, x, y, z + depth, r, g, b, 0f, 0f);
    }

    /**
     * Draws a flat quad on the XY plane (vertical wall facing Z direction).
     *
     * @param origin The bottom-left corner of the quad
     * @param width  Width along the X axis
     * @param height Height along the Y axis
     */
    public static void drawQuadXY(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                  Vec3 origin, float width, float height, int r, int g, int b) {
        float x = (float) origin.x;
        float y = (float) origin.y;
        float z = (float) origin.z;

        vertex(builder, poseMatrix, normalMatrix, x, y, z, r, g, b, 0f, 1f);
        vertex(builder, poseMatrix, normalMatrix, x + width, y, z, r, g, b, 1f, 1f);
        vertex(builder, poseMatrix, normalMatrix, x + width, y + height, z, r, g, b, 1f, 0f);
        vertex(builder, poseMatrix, normalMatrix, x, y + height, z, r, g, b, 0f, 0f);
    }

    /**
     * Draws a flat quad on the YZ plane (vertical wall facing X direction).
     *
     * @param origin The bottom-left corner of the quad
     * @param height Height along the Y axis
     * @param depth  Depth along the Z axis
     */
    public static void drawQuadYZ(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                  Vec3 origin, float height, float depth, int r, int g, int b) {
        float x = (float) origin.x;
        float y = (float) origin.y;
        float z = (float) origin.z;

        vertex(builder, poseMatrix, normalMatrix, x, y, z, r, g, b, 0f, 1f);
        vertex(builder, poseMatrix, normalMatrix, x, y, z + depth, r, g, b, 1f, 1f);
        vertex(builder, poseMatrix, normalMatrix, x, y + height, z + depth, r, g, b, 1f, 0f);
        vertex(builder, poseMatrix, normalMatrix, x, y + height, z, r, g, b, 0f, 0f);
    }

    public static void vertex(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                              float x, float y, float z, Color color, float u, float v) {
        builder.vertex(poseMatrix, x, y, z)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_LIGHT)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    /**
     * Draws a flat quad on the XZ plane (horizontal floor) facing upward (Y+).
     */
    public static void drawQuadXZ(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                  Vec3 origin, float width, float depth, List<Vec2> uvs, Color fromColor, Color toColor) {
        float x = (float) origin.x;
        float y = (float) origin.y;
        float z = (float) origin.z;

        vertex(builder, poseMatrix, normalMatrix, x, y, z, fromColor, uvs.get(0).x, uvs.get(0).y);
        vertex(builder, poseMatrix, normalMatrix, x + width, y, z, fromColor, uvs.get(1).x, uvs.get(1).y);
        vertex(builder, poseMatrix, normalMatrix, x + width, y, z + depth, toColor, uvs.get(2).x, uvs.get(2).y);
        vertex(builder, poseMatrix, normalMatrix, x, y, z + depth, toColor, uvs.get(3).x, uvs.get(3).y);
    }

    /**
     * Draws a flat quad on the XY plane (vertical wall facing Z direction).
     */
    public static void drawQuadXY(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                  Vec3 origin, float width, float height, List<Vec2> uvs, Color fromColor, Color toColor) {
        float x = (float) origin.x;
        float y = (float) origin.y;
        float z = (float) origin.z;
        vertex(builder, poseMatrix, normalMatrix, x, y, z, fromColor, uvs.get(0).x, uvs.get(0).y);
        vertex(builder, poseMatrix, normalMatrix, x + width, y, z, fromColor, uvs.get(1).x, uvs.get(1).y);
        vertex(builder, poseMatrix, normalMatrix, x + width, y + height, z, toColor, uvs.get(2).x, uvs.get(2).y);
        vertex(builder, poseMatrix, normalMatrix, x, y + height, z, toColor, uvs.get(3).x, uvs.get(3).y);
    }

    /**
     * Draws a flat quad on the YZ plane (vertical wall facing X direction).
     */
    public static void drawQuadYZ(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                  Vec3 origin, float height, float depth, List<Vec2> uvs, Color fromColor, Color toColor) {
        float x = (float) origin.x;
        float y = (float) origin.y;
        float z = (float) origin.z;

        vertex(builder, poseMatrix, normalMatrix, x, y, z, fromColor, uvs.get(0).x, uvs.get(0).y);
        vertex(builder, poseMatrix, normalMatrix, x, y, z + depth, fromColor, uvs.get(1).x, uvs.get(1).y);
        vertex(builder, poseMatrix, normalMatrix, x, y + height, z + depth, toColor, uvs.get(2).x, uvs.get(2).y);
        vertex(builder, poseMatrix, normalMatrix, x, y + height, z, toColor, uvs.get(3).x, uvs.get(3).y);
    }
}
