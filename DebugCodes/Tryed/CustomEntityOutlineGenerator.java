package net.foxyas.changedaddon.process;

import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.NotNull;

public class CustomEntityOutlineGenerator extends DefaultedVertexConsumer {
    private final VertexConsumer delegate;
    private double x, y, z;
    private float u, v;

    public CustomEntityOutlineGenerator(VertexConsumer delegate, int r, int g, int b, int a) {
        super.defaultColor(r, g, b, a);
        this.delegate = delegate;
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) { }

    @Override
    public void unsetDefaultColor() { }

    @Override
    public @NotNull VertexConsumer vertex(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
        return this;
    }

    @Override
    public @NotNull VertexConsumer color(int r, int g, int b, int a) {
        return this; // ignore, force default color
    }

    @Override
    public @NotNull VertexConsumer uv(float u, float v) {
        this.u = u; this.v = v;
        return this;
    }

    @Override
    public @NotNull VertexConsumer overlayCoords(int u, int v) { return this; }

    @Override
    public @NotNull VertexConsumer uv2(int u, int v) { return this; }

    @Override
    public @NotNull VertexConsumer normal(float x, float y, float z) { return this; }

    @Override
    public void vertex(float x, float y, float z, float r, float g, float b, float a,
                       float texU, float texV, int overlayUV, int lightmapUV,
                       float normalX, float normalY, float normalZ) {
        delegate.vertex(x, y, z)
                .color(defaultR, defaultG, defaultB, defaultA) // força cor
                .uv(texU, texV)
                .overlayCoords(overlayUV, overlayUV) // garante overlay
                .uv2(lightmapUV)                     // garante lightmap
                .normal(normalX, normalY, normalZ)   // garante normal
                .endVertex();
    }

    @Override
    public void endVertex() {
        delegate.vertex(x, y, z)
                .color(defaultR, defaultG, defaultB, defaultA) // força cor
                .uv(u, v)
                .overlayCoords(0, 10) // valor default vanilla
                .uv2(0)               // valor default vanilla
                .normal(0, 1, 0)      // normal padrão
                .endVertex();
    }


    @Override
    public VertexFormat getVertexFormat() {
        return delegate.getVertexFormat();
    }
}
