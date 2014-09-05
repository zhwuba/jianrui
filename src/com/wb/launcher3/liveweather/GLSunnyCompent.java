package com.wb.launcher3.liveweather;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;
import com.dutils.math.Vector3f;

public class GLSunnyCompent extends GLMesh {
    private static final float DEFAULT_HEIGHT = 0f;
    private static final float DEFAULT_WIDTH = 0f;

    private float fadeAlpha;
    protected float mATime;
    protected float mATimePeriod;
    private float mAroundR;
    private Vector3f mCoordinateTranslation;
    protected float mTime;
    protected float mTimePeriod;
    private float scale;

    public GLSunnyCompent() {
        super();
        this.mCoordinateTranslation = new Vector3f();
        this.fadeAlpha = 0f;
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
        this.mSrcAlpha = 1f;
        this.mCoordinateTranslation.x = 0f;
        this.mCoordinateTranslation.y = 0f;
        this.mCoordinateTranslation.z = 0f;
    }

    public void fadeInByStep() {
        if (this.fadeAlpha != 1f) {
            if (this.fadeAlpha < 1f) {
                this.fadeAlpha += 0.02f;
            } else {
                this.fadeAlpha = 1f;
            }
        }
    }

    public void fadeOutByStepTo(float alpha) {
        if (this.fadeAlpha != alpha) {
            if (this.fadeAlpha > alpha) {
                this.fadeAlpha -= 0.02f;
            } else {
                this.fadeAlpha = alpha;
            }
        }
    }

    public float getATime() {
        return this.mATime;
    }

    public float getATimePeriod() {
        return this.mATimePeriod;
    }

    public float getAroundR() {
        return this.mAroundR;
    }

    public Vector3f getCoordinateTranslation() {
        return this.mCoordinateTranslation;
    }

    public float getScale() {
        return this.scale;
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

    public void onDrawLight(GL10 gl, float alpha) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();

        this.matTrans.setIdentity();
        this.matTrans.setTranslation(this.mPosition.x - this.mCoordinateTranslation.x, this.mPosition.y
                - this.mCoordinateTranslation.y, this.mPosition.z);
        this.matModel.set(this.matTrans);
        this.matRots.setIdentity();
        this.matRots.rotZ(this.mAroundR);
        this.matModel.mul(this.matRots);
        this.matTrans.setIdentity();
        this.matTrans.setTranslation(this.mCoordinateTranslation.x, this.mCoordinateTranslation.y, 0f);
        this.matModel.mul(this.matTrans);
        gl.glMultMatrixf(this.matModel.asFloatBuffer());
        gl.glVertexPointer(MESH_POSITION_DIM, GL10.GL_FLOAT, 0, this.mVertexPositionBuf);
        this.mDestAlpha = this.mSrcAlpha * alpha * this.fadeAlpha;
        gl.glScalef(this.scale, this.scale, 1f);
        gl.glColor4f(this.mDestAlpha, this.mDestAlpha, this.mDestAlpha, this.mDestAlpha);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, this.mTextureId);
        gl.glTexCoordPointer(MESH_TEXCOORD_DIM, GL10.GL_FLOAT, 0, this.mVertexTexCoordBuf);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, MESH_VERTEX_NUM);

        gl.glPopMatrix();
    }

    public void setATime(float value) {
        this.mATime = value;
    }

    public void setATimePeriod(float value) {
        this.mATimePeriod = value;
    }

    public void setAroundR(float value) {
        this.mAroundR = value;
    }

    public void setCoordinateTranslation(Vector3f value) {
        this.mCoordinateTranslation = value;
    }

    public void setScale(float sc) {
        this.scale = sc;
    }

    public void setTime(float value) {
        this.mTime = value;
    }

    public void setTimePeriod(float value) {
        this.mTimePeriod = value;
    }
}
