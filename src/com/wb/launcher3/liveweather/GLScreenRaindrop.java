package com.wb.launcher3.liveweather;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;
import com.dutils.math.Vector3f;

public class GLScreenRaindrop extends GLMesh {
    private static final int TIME_PERIOD = 10000;
    protected float mImageHeight;
    protected Vector3f mScale;
    protected float mTime;
    protected float mTimePeriod;

    public GLScreenRaindrop() {
        super();
        this.mScale = new Vector3f(1f, 1f, 1f);
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
        this.mTimePeriod = TIME_PERIOD;
        this.mTime = 0f;
        this.mSrcAlpha = 1f;
    }

    public float getImageHeight() {
        return this.mImageHeight;
    }

    public Vector3f getScale() {
        return this.mScale;
    }

    public float getTime() {
        return this.mTime;
    }

    public float getTimePeriod() {
        return this.mTimePeriod;
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

    public void setImageHeight(float value) {
        this.mImageHeight = value;
    }

    public void setScale(Vector3f value) {
        this.mScale.set(value);
    }

    public void setTime(float value) {
        this.mTime = value;
    }

    public void setTimePeriod(float value) {
        this.mTimePeriod = value;
    }
}
