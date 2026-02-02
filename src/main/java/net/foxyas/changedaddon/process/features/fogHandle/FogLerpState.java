package net.foxyas.changedaddon.process.features.fogHandle;

public class FogLerpState {

    private float value = 0f;
    private float color = 0f;

    private float target = 0f;
    private float colorTarget = 0f;

    public float[] targetColorRgb0 = null; // cor base (ex: fog do bioma)
    public float[] targetColorRgb = new float[]{0, 0, 0}; // cor alvo

    /* ===================== TARGETS ===================== */

    public void setTarget(boolean active) {
        this.target = active ? 1f : 0f;
        this.colorTarget = this.target;
    }

    /* ===================== TICK ===================== */

    public void tick(float speed) {
        value += (target - value) * speed;
        color += (colorTarget - color) * speed;

        if (Math.abs(target - value) < 0.001f) {
            value = target;
        }

        if (Math.abs(colorTarget - color) < 0.001f) {
            color = colorTarget;
        }
    }

    /* ===================== GETTERS ===================== */

    public float get() {
        return value;
    }

    public float getColor() {
        return color;
    }

    public boolean isActive() {
        return value > 0.001f || color > 0.001f;
    }

    /* ===================== UTIL ===================== */

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    /* ===================== COLOR ===================== */

    public float[] getLerpColor() {
        if (targetColorRgb0 == null) {
            return targetColorRgb;
        }

        float t = Math.max(0f, Math.min(1f, color));

        return new float[]{
                lerp(targetColorRgb0[0], targetColorRgb[0], t),
                lerp(targetColorRgb0[1], targetColorRgb[1], t),
                lerp(targetColorRgb0[2], targetColorRgb[2], t)
        };
    }

    public void setTargetColor(float[] rgb) {
        if (rgb == null || rgb.length < 3) return;

        if (targetColorRgb != null &&
                targetColorRgb[0] == rgb[0] &&
                targetColorRgb[1] == rgb[1] &&
                targetColorRgb[2] == rgb[2]) {
            return; // mesma cor, não faz nada
        }

        targetColorRgb0 = getLerpColor();

        targetColorRgb = rgb.clone();
        color = 0f;
        colorTarget = 1f;
    }

}
