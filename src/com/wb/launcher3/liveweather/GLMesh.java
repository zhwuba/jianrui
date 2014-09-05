package com.wb.launcher3.liveweather;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.math.Matrix4f;
import com.dutils.math.Vector3f;

public class GLMesh {
    protected static final int MESH_POSITION_DATA_LEN = 12;
    protected static final int MESH_POSITION_DIM = 3;
    protected static final int MESH_TEXCOORD_DATA_LEN = 8;
    protected static final int MESH_TEXCOORD_DIM = 2;
    protected static final int MESH_VERTEX_NUM = 4;

    protected float mDestAlpha;
    protected float mSrcAlpha;

    protected Vector3f mDspeed;
    protected Vector3f mPosition;
    protected Vector3f mRotation;
    protected Vector3f mRspeed;
    protected Vector3f mTransition;

    protected static final float[] mTexCoordData = new float[] { 0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f };
    protected int mTextureId;

    protected FloatBuffer mVertexPositionBuf;
    protected FloatBuffer mVertexTexCoordBuf;

    protected float mWidth;
    protected float mHeight;

    protected Matrix4f matModel;
    protected Matrix4f matRots;
    protected Matrix4f matTrans;

    public GLMesh() {
        this.mSrcAlpha = 1f;
        this.mDestAlpha = 1f;
        this.mDspeed = new Vector3f();
        this.mRspeed = new Vector3f();
        this.mPosition = new Vector3f();
        this.mRotation = new Vector3f();
        this.mTransition = new Vector3f();
        this.matRots = new Matrix4f();
        this.matTrans = new Matrix4f();
        this.matModel = new Matrix4f();
    }

    public void buildMesh() {
        float w = this.mWidth / 2f;
        float h = this.mHeight / 2f;

        this.mVertexPositionBuf.position(0);
        this.mVertexPositionBuf.put(-w);
        this.mVertexPositionBuf.put(h);
        this.mVertexPositionBuf.put(0f);

        this.mVertexPositionBuf.put(w);
        this.mVertexPositionBuf.put(h);
        this.mVertexPositionBuf.put(0f);

        this.mVertexPositionBuf.put(-w);
        this.mVertexPositionBuf.put(-h);
        this.mVertexPositionBuf.put(0f);

        this.mVertexPositionBuf.put(w);
        this.mVertexPositionBuf.put(-h);
        this.mVertexPositionBuf.put(0f);
        this.mVertexPositionBuf.position(0);

        this.mVertexTexCoordBuf.position(0);
        this.mVertexTexCoordBuf.put(GLMesh.mTexCoordData);
        this.mVertexTexCoordBuf.position(0);
    }

    public float getAlpha() {
        return this.mSrcAlpha;
    }

    public Vector3f getDspeed() {
        return this.mDspeed;
    }

    public float getHeight() {
        return this.mHeight;
    }

    public Vector3f getPosition() {
        return this.mPosition;
    }

    public Vector3f getRotation() {
        return this.mRotation;
    }

    public Vector3f getRspeed() {
        return this.mRspeed;
    }

    public float getTextureId() {
        return (this.mTextureId);
    }

    public Vector3f getTransition() {
        return this.mTransition;
    }

    public float getWidth() {
        return this.mWidth;
    }

    public float getX() {
        return this.mPosition.x;
    }

    public float getY() {
        return this.mPosition.y;
    }

    public float getZ() {
        return this.mPosition.z;
    }

    public void onDraw(GL10 gl) {
    }

    public void setAlpha(float alpha) {
        this.mSrcAlpha = alpha;
    }

    public void setDspeed(Vector3f value) {
        this.mDspeed.set(value);
    }

    public void setHeight(float value) {
        this.mHeight = value;
    }

    public void setPosition(Vector3f value) {
        this.mPosition.set(value);
    }

    public void setRotation(Vector3f value) {
        this.mRotation.set(value);
    }

    public void setRspeed(Vector3f value) {
        this.mRspeed.set(value);
    }

    public void setTextureId(int value) {
        this.mTextureId = value;
    }

    public void setTransition(float x, float y, float z) {
        this.mTransition.set(x, y, z);
        this.mPosition.add(this.mTransition);
    }

    public void setWidth(float value) {
        this.mWidth = value;
    }

    public void setX(float value) {
        this.mPosition.x = value;
    }

    public void setY(float value) {
        this.mPosition.y = value;
    }

    public void setZ(float value) {
        this.mPosition.z = value;
    }
}
