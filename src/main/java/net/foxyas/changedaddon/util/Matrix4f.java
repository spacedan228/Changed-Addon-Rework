package net.foxyas.changedaddon.util;

import net.minecraft.util.Mth;

//Barebones backport of joml.Matrix4f
public class Matrix4f {

    float m00;
    float m01;
    float m02;
    float m03;
    float m10;
    float m11;
    float m12;
    float m13;
    float m20;
    float m21;
    float m22;
    float m23;
    float m30;
    float m31;
    float m32;
    float m33;
    int properties;

    public float m00() {
        return this.m00;
    }

    public float m01() {
        return this.m01;
    }

    public float m02() {
        return this.m02;
    }

    public float m03() {
        return this.m03;
    }

    public float m10() {
        return this.m10;
    }

    public float m11() {
        return this.m11;
    }

    public float m12() {
        return this.m12;
    }

    public float m13() {
        return this.m13;
    }

    public float m20() {
        return this.m20;
    }

    public float m21() {
        return this.m21;
    }

    public float m22() {
        return this.m22;
    }

    public float m23() {
        return this.m23;
    }

    public float m30() {
        return this.m30;
    }

    public float m31() {
        return this.m31;
    }

    public float m32() {
        return this.m32;
    }

    public float m33() {
        return this.m33;
    }

    Matrix4f _m00(float m00) {
        this.m00 = m00;
        return this;
    }

    Matrix4f _m01(float m01) {
        this.m01 = m01;
        return this;
    }

    Matrix4f _m02(float m02) {
        this.m02 = m02;
        return this;
    }

    Matrix4f _m03(float m03) {
        this.m03 = m03;
        return this;
    }

    Matrix4f _m10(float m10) {
        this.m10 = m10;
        return this;
    }

    Matrix4f _m11(float m11) {
        this.m11 = m11;
        return this;
    }

    Matrix4f _m12(float m12) {
        this.m12 = m12;
        return this;
    }

    Matrix4f _m13(float m13) {
        this.m13 = m13;
        return this;
    }

    Matrix4f _m20(float m20) {
        this.m20 = m20;
        return this;
    }

    Matrix4f _m21(float m21) {
        this.m21 = m21;
        return this;
    }

    Matrix4f _m22(float m22) {
        this.m22 = m22;
        return this;
    }

    Matrix4f _m23(float m23) {
        this.m23 = m23;
        return this;
    }

    Matrix4f _m30(float m30) {
        this.m30 = m30;
        return this;
    }

    Matrix4f _m31(float m31) {
        this.m31 = m31;
        return this;
    }

    Matrix4f _m32(float m32) {
        this.m32 = m32;
        return this;
    }

    Matrix4f _m33(float m33) {
        this.m33 = m33;
        return this;
    }

    Matrix4f _properties(int properties) {
        this.properties = properties;
        return this;
    }

    public Matrix4f identity() {
        return (this.properties & 4) != 0 ? this : this._m00(1.0F)._m01(0.0F)._m02(0.0F)._m03(0.0F)._m10(0.0F)._m11(1.0F)._m12(0.0F)._m13(0.0F)._m20(0.0F)._m21(0.0F)._m22(1.0F)._m23(0.0F)._m30(0.0F)._m31(0.0F)._m32(0.0F)._m33(1.0F)._properties(30);
    }

    public Matrix4f setTranslation(float x, float y, float z) {
        return this._m30(x)._m31(y)._m32(z)._properties(this.properties & -6);
    }

    public Matrix4f rotate(float ang, Vector3f vec) {
        return this.rotate(ang, vec.x, vec.y, vec.z);
    }

    public Matrix4f rotate(float ang, float x, float y, float z) {
        return this.rotate(ang, x, y, z, this);
    }

    public Matrix4f rotate(float ang, float x, float y, float z, Matrix4f dest) {
        if ((this.properties & 4) != 0) {
            return dest.rotation(ang, x, y, z);
        } else if ((this.properties & 8) != 0) {
            return rotateTranslation(ang, x, y, z, dest);
        } else {
            return (this.properties & 2) != 0 ? rotateAffine(ang, x, y, z, dest) : rotateGeneric(ang, x, y, z, dest);
        }
    }

    public Matrix4f rotateAffine(float ang, float x, float y, float z, Matrix4f dest) {
        if (y == 0.0F && z == 0.0F && Math.abs(x) == 1) {
            return this.rotateX(x * ang, dest);
        } else if (x == 0.0F && z == 0.0F && Math.abs(y) == 1) {
            return this.rotateY(y * ang, dest);
        } else {
            return x == 0.0F && y == 0.0F && Math.abs(z) == 1 ? this.rotateZ(z * ang, dest) : this.rotateAffineInternal(ang, x, y, z, dest);
        }
    }

    public Matrix4f rotateX(float ang, Matrix4f dest) {
        if ((this.properties & 4) != 0) {
            return dest.rotationX(ang);
        } else if ((this.properties & 8) != 0) {
            float x = this.m30();
            float y = this.m31();
            float z = this.m32();
            return dest.rotationX(ang).setTranslation(x, y, z);
        } else {
            return this.rotateXInternal(ang, dest);
        }
    }

    private Matrix4f rotateXInternal(float ang, Matrix4f dest) {
        float sin = (float) Math.sin(ang);
        float cos = cosFromSin(sin, ang);
        float lm10 = this.m10();
        float lm11 = this.m11();
        float lm12 = this.m12();
        float lm13 = this.m13();
        float lm20 = this.m20();
        float lm21 = this.m21();
        float lm22 = this.m22();
        float lm23 = this.m23();
        return dest._m20(Math.fma(lm10, -sin, lm20 * cos))._m21(Math.fma(lm11, -sin, lm21 * cos))._m22(Math.fma(lm12, -sin, lm22 * cos))._m23(Math.fma(lm13, -sin, lm23 * cos))._m10(Math.fma(lm10, cos, lm20 * sin))._m11(Math.fma(lm11, cos, lm21 * sin))._m12(Math.fma(lm12, cos, lm22 * sin))._m13(Math.fma(lm13, cos, lm23 * sin))._m00(this.m00())._m01(this.m01())._m02(this.m02())._m03(this.m03())._m30(this.m30())._m31(this.m31())._m32(this.m32())._m33(this.m33())._properties(this.properties & -14);
    }

    public Matrix4f rotateY(float ang, Matrix4f dest) {
        if ((this.properties & 4) != 0) {
            return dest.rotationY(ang);
        } else if ((this.properties & 8) != 0) {
            float x = this.m30();
            float y = this.m31();
            float z = this.m32();
            return dest.rotationY(ang).setTranslation(x, y, z);
        } else {
            return this.rotateYInternal(ang, dest);
        }
    }

    private Matrix4f rotateYInternal(float ang, Matrix4f dest) {
        float sin = (float) Math.sin(ang);
        float cos = cosFromSin(sin, ang);
        float nm00 = Math.fma(this.m00(), cos, this.m20() * -sin);
        float nm01 = Math.fma(this.m01(), cos, this.m21() * -sin);
        float nm02 = Math.fma(this.m02(), cos, this.m22() * -sin);
        float nm03 = Math.fma(this.m03(), cos, this.m23() * -sin);
        return dest._m20(Math.fma(this.m00(), sin, this.m20() * cos))._m21(Math.fma(this.m01(), sin, this.m21() * cos))._m22(Math.fma(this.m02(), sin, this.m22() * cos))._m23(Math.fma(this.m03(), sin, this.m23() * cos))._m00(nm00)._m01(nm01)._m02(nm02)._m03(nm03)._m10(this.m10())._m11(this.m11())._m12(this.m12())._m13(this.m13())._m30(this.m30())._m31(this.m31())._m32(this.m32())._m33(this.m33())._properties(this.properties & -14);
    }

    public Matrix4f rotateZ(float ang, Matrix4f dest) {
        if ((this.properties & 4) != 0) {
            return dest.rotationZ(ang);
        } else if ((this.properties & 8) != 0) {
            float x = this.m30();
            float y = this.m31();
            float z = this.m32();
            return dest.rotationZ(ang).setTranslation(x, y, z);
        } else {
            return this.rotateZInternal(ang, dest);
        }
    }

    private Matrix4f rotateZInternal(float ang, Matrix4f dest) {
        float sin = (float) Math.sin(ang);
        float cos = cosFromSin(sin, ang);
        return this.rotateTowardsXY(sin, cos, dest);
    }

    public Matrix4f rotateTowardsXY(float dirX, float dirY, Matrix4f dest) {
        if ((this.properties & 4) != 0) {
            return dest.rotationTowardsXY(dirX, dirY);
        } else {
            float nm00 = Math.fma(this.m00(), dirY, this.m10() * dirX);
            float nm01 = Math.fma(this.m01(), dirY, this.m11() * dirX);
            float nm02 = Math.fma(this.m02(), dirY, this.m12() * dirX);
            float nm03 = Math.fma(this.m03(), dirY, this.m13() * dirX);
            return dest._m10(Math.fma(this.m00(), -dirX, this.m10() * dirY))._m11(Math.fma(this.m01(), -dirX, this.m11() * dirY))._m12(Math.fma(this.m02(), -dirX, this.m12() * dirY))._m13(Math.fma(this.m03(), -dirX, this.m13() * dirY))._m00(nm00)._m01(nm01)._m02(nm02)._m03(nm03)._m20(this.m20())._m21(this.m21())._m22(this.m22())._m23(this.m23())._m30(this.m30())._m31(this.m31())._m32(this.m32())._m33(this.m33())._properties(this.properties & -14);
        }
    }

    public Matrix4f rotationTowardsXY(float dirX, float dirY) {
        if ((this.properties & 4) == 0) {
            identity();
        }

        return this._m00(dirY)._m01(dirX)._m10(-dirX)._m11(dirY)._properties(18);
    }

    private Matrix4f rotateGeneric(float ang, float x, float y, float z, Matrix4f dest) {
        if (y == 0.0F && z == 0.0F && Math.abs(x) == 1) {
            return this.rotateX(x * ang, dest);
        } else if (x == 0.0F && z == 0.0F && Math.abs(y) == 1) {
            return this.rotateY(y * ang, dest);
        } else {
            return x == 0.0F && y == 0.0F && Math.abs(z) == 1 ? this.rotateZ(z * ang, dest) : this.rotateGenericInternal(ang, x, y, z, dest);
        }
    }

    private Matrix4f rotateGenericInternal(float ang, float x, float y, float z, Matrix4f dest) {
        float s = (float) Math.sin(ang);
        float c = cosFromSin(s, ang);
        float C = 1.0F - c;
        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float yy = y * y;
        float yz = y * z;
        float zz = z * z;
        float rm00 = xx * C + c;
        float rm01 = xy * C + z * s;
        float rm02 = xz * C - y * s;
        float rm10 = xy * C - z * s;
        float rm11 = yy * C + c;
        float rm12 = yz * C + x * s;
        float rm20 = xz * C + y * s;
        float rm21 = yz * C - x * s;
        float rm22 = zz * C + c;
        float nm00 = this.m00() * rm00 + this.m10() * rm01 + this.m20() * rm02;
        float nm01 = this.m01() * rm00 + this.m11() * rm01 + this.m21() * rm02;
        float nm02 = this.m02() * rm00 + this.m12() * rm01 + this.m22() * rm02;
        float nm03 = this.m03() * rm00 + this.m13() * rm01 + this.m23() * rm02;
        float nm10 = this.m00() * rm10 + this.m10() * rm11 + this.m20() * rm12;
        float nm11 = this.m01() * rm10 + this.m11() * rm11 + this.m21() * rm12;
        float nm12 = this.m02() * rm10 + this.m12() * rm11 + this.m22() * rm12;
        float nm13 = this.m03() * rm10 + this.m13() * rm11 + this.m23() * rm12;
        return dest._m20(this.m00() * rm20 + this.m10() * rm21 + this.m20() * rm22)._m21(this.m01() * rm20 + this.m11() * rm21 + this.m21() * rm22)._m22(this.m02() * rm20 + this.m12() * rm21 + this.m22() * rm22)._m23(this.m03() * rm20 + this.m13() * rm21 + this.m23() * rm22)._m00(nm00)._m01(nm01)._m02(nm02)._m03(nm03)._m10(nm10)._m11(nm11)._m12(nm12)._m13(nm13)._m30(this.m30())._m31(this.m31())._m32(this.m32())._m33(this.m33())._properties(this.properties & -14);
    }

    private Matrix4f rotateAffineInternal(float ang, float x, float y, float z, Matrix4f dest) {
        float s = (float) Math.sin(ang);
        float c = cosFromSin(s, ang);
        float C = 1.0F - c;
        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float yy = y * y;
        float yz = y * z;
        float zz = z * z;
        float rm00 = xx * C + c;
        float rm01 = xy * C + z * s;
        float rm02 = xz * C - y * s;
        float rm10 = xy * C - z * s;
        float rm11 = yy * C + c;
        float rm12 = yz * C + x * s;
        float rm20 = xz * C + y * s;
        float rm21 = yz * C - x * s;
        float rm22 = zz * C + c;
        float nm00 = this.m00() * rm00 + this.m10() * rm01 + this.m20() * rm02;
        float nm01 = this.m01() * rm00 + this.m11() * rm01 + this.m21() * rm02;
        float nm02 = this.m02() * rm00 + this.m12() * rm01 + this.m22() * rm02;
        float nm10 = this.m00() * rm10 + this.m10() * rm11 + this.m20() * rm12;
        float nm11 = this.m01() * rm10 + this.m11() * rm11 + this.m21() * rm12;
        float nm12 = this.m02() * rm10 + this.m12() * rm11 + this.m22() * rm12;
        return dest._m20(this.m00() * rm20 + this.m10() * rm21 + this.m20() * rm22)._m21(this.m01() * rm20 + this.m11() * rm21 + this.m21() * rm22)._m22(this.m02() * rm20 + this.m12() * rm21 + this.m22() * rm22)._m23(0.0F)._m00(nm00)._m01(nm01)._m02(nm02)._m03(0.0F)._m10(nm10)._m11(nm11)._m12(nm12)._m13(0.0F)._m30(this.m30())._m31(this.m31())._m32(this.m32())._m33(1.0F)._properties(this.properties & -14);
    }

    public Matrix4f rotateTranslation(float ang, float x, float y, float z, Matrix4f dest) {
        float tx = this.m30();
        float ty = this.m31();
        float tz = this.m32();
        if (y == 0.0F && z == 0.0F && Math.abs(x) == 1) {
            return dest.rotationX(x * ang).setTranslation(tx, ty, tz);
        } else if (x == 0.0F && z == 0.0F && Math.abs(y) == 1) {
            return dest.rotationY(y * ang).setTranslation(tx, ty, tz);
        } else {
            return x == 0.0F && y == 0.0F && Math.abs(z) == 1 ? dest.rotationZ(z * ang).setTranslation(tx, ty, tz) : this.rotateTranslationInternal(ang, x, y, z, dest);
        }
    }

    private Matrix4f rotateTranslationInternal(float ang, float x, float y, float z, Matrix4f dest) {
        float s = (float) Math.sin(ang);
        float c = cosFromSin(s, ang);
        float C = 1.0F - c;
        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float yy = y * y;
        float yz = y * z;
        float zz = z * z;
        float rm00 = xx * C + c;
        float rm01 = xy * C + z * s;
        float rm02 = xz * C - y * s;
        float rm10 = xy * C - z * s;
        float rm11 = yy * C + c;
        float rm12 = yz * C + x * s;
        float rm20 = xz * C + y * s;
        float rm21 = yz * C - x * s;
        float rm22 = zz * C + c;
        return dest._m20(rm20)._m21(rm21)._m22(rm22)._m23(0.0F)._m00(rm00)._m01(rm01)._m02(rm02)._m03(0.0F)._m10(rm10)._m11(rm11)._m12(rm12)._m13(0.0F)._m30(this.m30())._m31(this.m31())._m32(this.m32())._m33(1.0F)._properties(this.properties & -14);
    }

    public Matrix4f rotation(float angle, float x, float y, float z) {
        if (y == 0.0F && z == 0.0F && Math.abs(x) == 1) {
            return this.rotationX(x * angle);
        } else if (x == 0.0F && z == 0.0F && Math.abs(y) == 1) {
            return this.rotationY(y * angle);
        } else {
            return x == 0.0F && y == 0.0F && Math.abs(z) == 1 ? this.rotationZ(z * angle) : this.rotationInternal(angle, x, y, z);
        }
    }

    public Matrix4f rotationZ(float ang) {
        float sin = (float) Math.sin(ang);
        float cos = cosFromSin(sin, ang);
        if ((this.properties & 4) == 0) {
            identity();
        }

        return this._m00(cos)._m01(sin)._m10(-sin)._m11(cos)._properties(18);
    }

    private Matrix4f rotationInternal(float angle, float x, float y, float z) {
        float sin = (float) Math.sin(angle);
        float cos = cosFromSin(sin, angle);
        float C = 1.0F - cos;
        float xy = x * y;
        float xz = x * z;
        float yz = y * z;
        if ((this.properties & 4) == 0) {
            identity();
        }

        return this._m00(cos + x * x * C)._m10(xy * C - z * sin)._m20(xz * C + y * sin)._m01(xy * C + z * sin)._m11(cos + y * y * C)._m21(yz * C - x * sin)._m02(xz * C - y * sin)._m12(yz * C + x * sin)._m22(cos + z * z * C)._properties(18);
    }

    public Matrix4f rotationX(float ang) {
        float sin = (float) Math.sin(ang);
        float cos = cosFromSin(sin, ang);
        if ((this.properties & 4) == 0) {
            identity();
        }

        this._m11(cos)._m12(sin)._m21(-sin)._m22(cos)._properties(18);
        return this;
    }

    public Matrix4f rotationY(float ang) {
        float sin = (float) Math.sin(ang);
        float cos = cosFromSin(sin, ang);
        if ((this.properties & 4) == 0) {
            identity();
        }

        this._m00(cos)._m02(-sin)._m20(sin)._m22(cos)._properties(18);
        return this;
    }

    private static float cosFromSin(float sin, float angle) {
        // sin(x)^2 + cos(x)^2 = 1
        float cos = Mth.sqrt(1.0f - sin * sin);
        float a = angle + Mth.HALF_PI;
        float b = a - (int)(a / Mth.TWO_PI) * Mth.TWO_PI;
        if (b < 0.0)
            b = Mth.TWO_PI + b;
        if (b >= Mth.PI)
            return -cos;
        return cos;
    }
}
