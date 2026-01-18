package net.foxyas.changedaddon.util;

import net.minecraft.util.Mth;

public class Vector3f {

    public float x, y, z;

    public Vector3f(){}

    public Vector3f(float x, float y, float z){
        set(x, y, z);
    }

    public Vector3f set(float val){
        x = y = z = val;
        return this;
    }

    public Vector3f set(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3f set(double x, double y, double z){
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
        return this;
    }

    public Vector3f set(Vector3f other){
        return set(other.x, other.y, other.z);
    }

    public Vector3f sub(float x, float y, float z){
        return set(this.x - x, this.y - y, this.z - z);
    }

    public Vector3f sub(Vector3f other, Vector3f dest){
        return dest.set(x - other.x, y - other.y, z - other.z);
    }

    public Vector3f mul(float scalar){
        return set(x * scalar, y * scalar, z * scalar);
    }

    public Vector3f lerp(Vector3f other, float delta, Vector3f dest){
        return dest.set(Mth.lerp(delta, x, other.x), Mth.lerp(delta, y, other.y), Mth.lerp(delta, z, other.z));
    }

    public Vector3f normalize(){
        float f = this.x * this.x + this.y * this.y + this.z * this.z;
        if (f < Float.MIN_NORMAL) { //Forge: Fix MC-239212
            return this;
        }

        return mul(Mth.fastInvSqrt(f));
    }

    public Vector3f normalize(float length){
        float f = this.x * this.x + this.y * this.y + this.z * this.z;
        if (f < Float.MIN_NORMAL) { //Forge: Fix MC-239212
            return this;
        }

        return mul(Mth.fastInvSqrt(f) * length);
    }

    public Vector3f cross(Vector3f other, Vector3f dest){
        float f = this.x;
        float f1 = this.y;
        float f2 = this.z;
        float f3 = other.x;
        float f4 = other.y;
        float f5 = other.z;
        return dest.set(f1 * f5 - f2 * f4, f2 * f3 - f * f5, f * f4 - f1 * f3);
    }

    public float distanceSquared(Vector3f other){
        float dx = x - other.x;
        float dy = y - other.y;
        float dz = z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public boolean isFinite(){
        return Math.abs(x) <= Float.MAX_VALUE && Math.abs(y) <= Float.MAX_VALUE && Math.abs(z) <= Float.MAX_VALUE;
    }

    public Vector3f mulPosition(Matrix4f mat) {
        float x = this.x, y = this.y, z = this.z;
        this.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30())));
        this.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31())));
        this.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32())));
        return this;
    }
}
