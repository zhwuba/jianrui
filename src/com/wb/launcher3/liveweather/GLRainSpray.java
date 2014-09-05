package com.wb.launcher3.liveweather;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;
import com.dutils.math.Vector3f;

public class GLRainSpray extends GLMesh {
    float mAccelerate;
    Vector3f mStartPosition;
    float mStartRotation;
    long mStartTime;
    int mType;

    public GLRainSpray() {
        super();
        this.mType = 0;
        this.mStartTime = 0;
        this.mStartPosition = null;
        this.mStartRotation = 0f;
        this.mAccelerate = 0f;
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
        this.mSrcAlpha = 0f;
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

    @Override
    public void setAlpha(float alpha) {
        if (alpha < 0f) {
            alpha = 0f;
        } else if (alpha > 1f) {
            alpha = 1f;
        }

        this.mSrcAlpha = alpha;
    }

    public void setStartPosition(Vector3f value) {
        this.setPosition(value);
        this.mStartPosition = value;
    }

    public void updateData(long period, long allTime) {
        double PI = Math.PI;

        switch (this.mType) {
        case 0:
            this.mSrcAlpha = (float) (Math.sin(period * PI / allTime) * 0.3f);
            break;
        case 1:
        case 2:
            this.getPosition().x = this.getDspeed().x * period + this.mStartPosition.x;
            this.getPosition().y = this.getDspeed().y * period + 0.5f * this.mAccelerate * period * period
                    + this.mStartPosition.y;
            this.mSrcAlpha = (float) (Math.sin(period * PI / allTime) * 0.8f);
            this.getRotation().z = this.mStartRotation + this.getRspeed().z * period;
            break;
        }
    }
}
