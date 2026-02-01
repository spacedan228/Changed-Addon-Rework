package net.foxyas.changedaddon.process.features.fogHandle;

public class FogLerpState {

    private float value = 0f;
    private float target = 0f;

    public float[] targetColorRgb = new float[]{0,0,0};

    public void setTarget(boolean active) {
        this.target = active ? 1f : 0f;
    }

    public void tick(float speed) {
        value += (target - value) * speed;

        if (Math.abs(target - value) < 0.001f) {
            value = target;
        }
    }

    public float get() {
        return value;
    }

    public boolean isActive() {
        return value > 0.001f;
    }

    public static float lerp(float a, float b, float partialTick) {
        return a + (b - a) * partialTick;
    }
}
