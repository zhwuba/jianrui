package com.dutils.math;

import java.nio.FloatBuffer;

import com.dutils.buffer.IBufferFactory;

public class Matrix4f {
    public float[] m;
    public FloatBuffer matBuffer;

    private static Vector3f tmpF;
    private static Matrix4f tmpMat;
    private static Vector3f tmpS;
    private static Vector3f tmpT;
    private static Vector3f tmpUp;

    static {
        Matrix4f.tmpF = new Vector3f();
        Matrix4f.tmpUp = new Vector3f();
        Matrix4f.tmpS = new Vector3f();
        Matrix4f.tmpT = new Vector3f();
        Matrix4f.tmpMat = new Matrix4f();

        System.loadLibrary("dutils");
    }

    public Matrix4f() {
        super();
        this.matBuffer = IBufferFactory.newFloatBuffer(16);
        this.m = new float[16];
        this.setIdentity();
    }

    public FloatBuffer asFloatBuffer() {
        this.nativeAsFloatBuffer(this.m, this.matBuffer);
        return this.matBuffer;
    }

    public void fillFloatArray(float[] array) {
        FloatBuffer floatBuffer = this.asFloatBuffer();
        floatBuffer.get(array);
        floatBuffer.position(0);
    }

    public final void fillFloatBuffer(FloatBuffer buffer) {
        this.nativeAsFloatBuffer(this.m, buffer);
    }

    public static void gluLookAt(Vector3f eye, Vector3f center, Vector3f up, Matrix4f out) {
        Matrix4f.tmpF.x = center.x - eye.x;
        Matrix4f.tmpF.y = center.y - eye.y;
        Matrix4f.tmpF.z = center.z - eye.z;
        Matrix4f.tmpF.normalize();
        Matrix4f.tmpUp.set(up);
        Matrix4f.tmpUp.normalize();
        Matrix4f.tmpS.cross(Matrix4f.tmpF, Matrix4f.tmpUp);
        Matrix4f.tmpT.cross(Matrix4f.tmpS, Matrix4f.tmpF);
        out.m[0] = Matrix4f.tmpS.x;
        out.m[4] = Matrix4f.tmpT.x;
        out.m[8] = -Matrix4f.tmpF.x;
        out.m[12] = 0f;
        out.m[1] = Matrix4f.tmpS.y;
        out.m[5] = Matrix4f.tmpT.y;
        out.m[9] = -Matrix4f.tmpF.y;
        out.m[13] = 0f;
        out.m[2] = Matrix4f.tmpS.z;
        out.m[6] = Matrix4f.tmpT.z;
        out.m[10] = -Matrix4f.tmpF.z;
        out.m[14] = 0f;
        out.m[3] = 0f;
        out.m[7] = 0f;
        out.m[11] = 0f;
        out.m[15] = 1f;
        Matrix4f.tmpMat.setIdentity();
        Matrix4f.tmpMat.setTranslation(-eye.x, -eye.y, -eye.z);
        out.mul(Matrix4f.tmpMat);
    }

    public static void gluPersective(float fovy, float aspect, float zNear, float zFar, Matrix4f out) {
        float radians = ((float) (((fovy / 2f)) * 3.141593 / 180));
        float deltaZ = zFar - zNear;
        float sine = ((float) Math.sin((radians)));

        if (deltaZ != 0f && sine != 0f && aspect != 0f) {
            float v0 = (((float) Math.cos((radians)))) / sine;
            out.setIdentity();
            out.m[0] = v0 / aspect;
            out.m[5] = v0;
            out.m[10] = -(zFar + zNear) / deltaZ;
            out.m[14] = -1f;
            out.m[11] = -2f * zNear * zFar / deltaZ;
            out.m[15] = 0f;
        }
    }

    public final void mul(Matrix4f m1) {
        this.nativeMul(this.m, m1.m);
    }

    public final void mul(Matrix4f m1, Matrix4f m2) {
        if (this == m1 || this == m2) {
            float[] v0 = new float[16];
            this.nativeMul(v0, m1.m, m2.m);
            this.m[0] = v0[0];
            this.m[1] = v0[1];
            this.m[2] = v0[2];
            this.m[3] = v0[3];
            this.m[4] = v0[4];
            this.m[5] = v0[5];
            this.m[6] = v0[6];
            this.m[7] = v0[7];
            this.m[8] = v0[8];
            this.m[9] = v0[9];
            this.m[10] = v0[10];
            this.m[11] = v0[11];
            this.m[12] = v0[12];
            this.m[13] = v0[13];
            this.m[14] = v0[14];
            this.m[15] = v0[15];
        } else {
            this.nativeMul(this.m, m1.m, m2.m);
        }
    }

    public native void nativeAsFloatBuffer(float[] array, FloatBuffer buffer);

    public native void nativeMul(float[] result, float[] m);

    public native void nativeMul(float[] result, float[] m1, float[] m2);

    public native void nativeRotX(float[] m, float angel);

    public native void nativeRotY(float[] m, float angel);

    public native void nativeRotZ(float[] m, float angel);

    public native void nativeRotate(float[] m, float angle, float x, float y, float z);

    public final void rotX(float angle) {
        this.nativeRotX(this.m, angle);
    }

    public final void rotY(float angle) {
        this.nativeRotY(this.m, angle);
    }

    public final void rotZ(float angle) {
        this.nativeRotZ(this.m, angle);
    }

    public void rotate(float angle, float x, float y, float z) {
        this.nativeRotate(this.m, angle, x, y, z);
    }

    public final void set(Matrix4f m1) {
        this.m[0] = m1.m[0];
        this.m[1] = m1.m[1];
        this.m[2] = m1.m[2];
        this.m[3] = m1.m[3];
        this.m[4] = m1.m[4];
        this.m[5] = m1.m[5];
        this.m[6] = m1.m[6];
        this.m[7] = m1.m[7];
        this.m[8] = m1.m[8];
        this.m[9] = m1.m[9];
        this.m[10] = m1.m[10];
        this.m[11] = m1.m[11];
        this.m[12] = m1.m[12];
        this.m[13] = m1.m[13];
        this.m[14] = m1.m[14];
        this.m[15] = m1.m[15];
    }

    public void setIdentity() {
        this.m[0] = 1f;
        this.m[1] = 0f;
        this.m[2] = 0f;
        this.m[3] = 0f;
        this.m[4] = 0f;
        this.m[5] = 1f;
        this.m[6] = 0f;
        this.m[7] = 0f;
        this.m[8] = 0f;
        this.m[9] = 0f;
        this.m[10] = 1f;
        this.m[11] = 0f;
        this.m[12] = 0f;
        this.m[13] = 0f;
        this.m[14] = 0f;
        this.m[15] = 1f;
    }

    public final void setTranslation(float x, float y, float z) {
        this.m[3] = x;
        this.m[7] = y;
        this.m[11] = z;
    }

    public final void setTranslation(Vector3f trans) {
        this.m[3] = trans.x;
        this.m[7] = trans.y;
        this.m[11] = trans.z;
    }

    @Override
    public String toString() {
        return "m[0]=" + this.m[0] + ", m[1]=" + this.m[1] + ", m[2]=" + this.m[2] + ", m[3]=" + this.m[3] + ", m[4]="
                + this.m[4] + ", m[5]=" + this.m[5] + ", m[6]=" + this.m[6] + ", m[7]=" + this.m[7] + ", m[8]="
                + this.m[8] + ", m[9]=" + this.m[9] + ", m[10]=" + this.m[10] + ", m[11]=" + this.m[11] + ", m[12]="
                + this.m[12] + ", m[13]=" + this.m[13] + ", m[14]=" + this.m[14] + ", m[15]=" + this.m[15];
    }
}
