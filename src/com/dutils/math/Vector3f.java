package com.dutils.math;

public class Vector3f {
    public static Vector3f TEMP = new Vector3f();
    public static Vector3f TEMP1 = new Vector3f();

    public static final Vector3f UP = new Vector3f(0f, 1f, 0f);
    public static final Vector3f ZERO = new Vector3f(0f, 0f, 0f);

    public float x;
    public float y;
    public float z;

    public Vector3f() {
        this.set(0f, 0f, 0f);
    }

    public Vector3f(float x, float y, float z) {
        this.set(x, y, z);
    }

    public final void add(Vector3f v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }

    public final void cross(Vector3f v1, Vector3f v2) {
        float x = v1.y * v2.z - v1.z * v2.y;
        float y = v2.x * v1.z - v2.z * v1.x;
        this.z = v1.x * v2.y - v1.y * v2.x;
        this.x = x;
        this.y = y;
    }

    public static float distance(Vector3f v1, Vector3f v2) {
        float dx = v1.x - v2.x;
        float dy = v1.y - v2.y;
        float dz = v1.z - v2.z;
        return ((float) Math.sqrt((dx * dx + dy * dy + dz * dz)));
    }

    public final float dot(Vector3f v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public void normalize() {
        float len = 1f / (((float) Math.sqrt((this.x * this.x + this.y * this.y + this.z * this.z))));
        this.x *= len;
        this.y *= len;
        this.z *= len;
    }

    public final void scale(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }

    public void set(Vector3f v) {
        this.set(v.x, v.y, v.z);
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(float[] a) {
        if (a != null && a.length == 3) {
            this.set(a[0], a[1], a[2]);
        }
    }

    public final void sub(Vector3f v) {
        this.sub(this, v);
    }

    public final void sub(Vector3f v1, Vector3f v2) {
        this.x = v1.x - v2.x;
        this.y = v1.y - v2.y;
        this.z = v1.z - v2.z;
    }

    @Override
    public String toString() {
        return "Vector3f x=" + this.x + ", y=" + this.y + ", z=" + this.z;
    }

    public void zero() {
        this.z = 0f;
        this.y = 0f;
        this.x = 0f;
    }
}
