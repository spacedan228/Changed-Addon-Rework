package net.foxyas.changedaddon.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.List;

public class RenderUtil {

    private static final int FULL_LIGHT = LightTexture.FULL_BRIGHT; // Lightmap value for full brightness

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
