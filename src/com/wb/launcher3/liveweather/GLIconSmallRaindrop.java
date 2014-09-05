package com.wb.launcher3.liveweather;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;
import com.dutils.math.Vector3f;

public class GLIconSmallRaindrop extends GLMesh {
    protected Vector3f mEndPosition;
    protected Vector3f mHalfPosition;
    protected int[] mIconEdge;
    protected boolean mIsFreefall;
    protected boolean mIsIconDropInvalid;
    protected float mScale;

    public GLIconSmallRaindrop() {
        super();
        this.mScale = 0.8f;
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
        this.mEndPosition = new Vector3f();
        this.mHalfPosition = new Vector3f();
        this.mIsFreefall = false;
        this.mIsIconDropInvalid = true;
        this.mSrcAlpha = 1f;
        this.mIconEdge = new int[4];
        this.mIconEdge[0] = 40;
        this.mIconEdge[1] = 40;
        this.mIconEdge[2] = 40;
        this.mIconEdge[3] = 40;
    }

    public Vector3f getEndPosition() {
        return this.mEndPosition;
    }

    public Vector3f getHalfPosition() {
        return this.mHalfPosition;
    }

    public int[] getIconEdge() {
        return this.mIconEdge;
    }

    public boolean getIsFreeFall() {
        return this.mIsFreefall;
    }

    public boolean getIsIconDropInvalid() {
        return this.mIsIconDropInvalid;
    }

    public float getScale() {
        return this.mScale;
    }

    public void onDraw(GL10 gl, float alpha) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();

        this.matTrans.setIdentity();
        this.matTrans.setTranslation(this.mPosition.x, this.mPosition.y, this.mPosition.z);
        this.matModel.set(this.matTrans);
        this.matRots.setIdentity();
        this.matRots.rotZ(this.mRotation.z);
        this.matModel.mul(this.matRots);
        gl.glMultMatrixf(this.matModel.asFloatBuffer());
        gl.glVertexPointer(MESH_POSITION_DIM, GL10.GL_FLOAT, 0, this.mVertexPositionBuf);
        this.mDestAlpha = this.mSrcAlpha * alpha;
        gl.glScalef(1f, 1f, 1f);
        gl.glColor4f(this.mDestAlpha, this.mDestAlpha, this.mDestAlpha, this.mDestAlpha);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, this.mTextureId);
        gl.glTexCoordPointer(MESH_TEXCOORD_DIM, GL10.GL_FLOAT, 0, this.mVertexTexCoordBuf);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, MESH_VERTEX_NUM);

        gl.glPopMatrix();
    }

    @Override
    public void onDraw(GL10 gl) {
        this.onDraw(gl, 1f);
    }

    public void setEndPosition(Vector3f value) {
        this.mEndPosition.set(value);
    }

    public void setHalfPosition(Vector3f value) {
        this.mHalfPosition.set(value);
    }

    public void setIconEdge(int[] value) {
        this.mIconEdge = value;
        this.mEndPosition.x = 0f;
        this.mEndPosition.y = 0f;
        this.mEndPosition.z = 0f;
        this.mHalfPosition.x = 0f;
        this.mHalfPosition.y = 0f;
        this.mHalfPosition.z = 0f;
    }

    public void setIsFreeFall(boolean value) {
        this.mIsFreefall = value;
    }

    public void setIsIconDropInvalid(boolean value) {
        this.mIsIconDropInvalid = value;
    }

    public void setScale(float value) {
        this.mScale = value;
    }
}
