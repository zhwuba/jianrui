package com.wb.launcher3.liveweather;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;
import com.dutils.math.Vector3f;

public class GLDandelionSeed extends GLMesh {
    protected static final float RENDER_DURATION = 16.666666f;
    public float mAccelerateSpeedX;
    public int mDandelionState;
    public float mDelcelerateAllPeriod;
    public long mDelcelerateNowPeriod;
    public Vector3f mDelcelerateStartPoint;
    public float mDistanceX;
    private Vector3f mHuffFlySpeed;
    public int mHuffFlyState;
    private float mMaxAngle;
    private float mMinAngle;
    public boolean mWillNotStaticOnIcon;

    public GLDandelionSeed() {
        this.mDandelionState = 0;
        this.mHuffFlySpeed = new Vector3f();
        this.mHuffFlyState = 0;
        this.mWillNotStaticOnIcon = true;
        this.mAccelerateSpeedX = 0f;
        this.mDelcelerateStartPoint = new Vector3f();
        this.mDelcelerateAllPeriod = 0f;
        this.mDelcelerateNowPeriod = 0;
        this.mDistanceX = 0f;
        this.mMinAngle = 0f;
        this.mMaxAngle = 0f;
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
        this.mSrcAlpha = 1f;
    }

    public void generateAccelerateSpeedX() {
        this.mAccelerateSpeedX = -1f * this.mDspeed.x * this.mDspeed.x
                / (this.mDistanceX * 2f * RENDER_DURATION * RENDER_DURATION);
        this.mDelcelerateAllPeriod = this.mDistanceX * 2f / (this.mDspeed.x / RENDER_DURATION);
    }

    public float getDelcelerateDistanceX(long deltaTime) {
        this.mDelcelerateNowPeriod += deltaTime;
        if (this.mDelcelerateNowPeriod >= this.mDelcelerateAllPeriod) {
            return this.mDistanceX;
        } else {
            return this.mDspeed.x / RENDER_DURATION * this.mDelcelerateNowPeriod + 0.5f * this.mAccelerateSpeedX
                    * this.mDelcelerateNowPeriod * this.mDelcelerateNowPeriod;
        }
    }

    public Vector3f getDelceleratePeriod() {
        return this.mDelcelerateStartPoint;
    }

    public Vector3f getDelcelerateStartPoint() {
        return this.mDelcelerateStartPoint;
    }

    public Vector3f getHuffFlySpeed() {
        return this.mHuffFlySpeed;
    }

    public float getMaxAngle() {
        return this.mMaxAngle;
    }

    public float getMinAngle() {
        return this.mMinAngle;
    }

    public float getVatualX() {
        return this.mPosition.x + this.mDistanceX;
    }

    public float getVatualY() {
        return this.mPosition.y + this.mDistanceX * this.getDspeed().y / this.getDspeed().x;
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

    public void onRotateDraw(GL10 gl, float centerX, float centerY, float centerZ, float alpha) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();

        this.matTrans.setIdentity();
        this.matTrans.setTranslation(this.mPosition.x - centerX, this.mPosition.y - centerY, this.mPosition.z);
        this.matModel.set(this.matTrans);
        this.matRots.setIdentity();
        this.matRots.rotZ(this.mRotation.z);
        this.matModel.mul(this.matRots);
        this.matTrans.setIdentity();
        this.matTrans.setTranslation(centerX, centerY, 0f);
        this.matModel.mul(this.matTrans);
        gl.glMultMatrixf(this.matModel.asFloatBuffer());
        gl.glVertexPointer(MESH_POSITION_DIM, GL10.GL_FLOAT, 0, this.mVertexPositionBuf);
        this.mDestAlpha = this.mSrcAlpha * alpha;
        gl.glColor4f(this.mDestAlpha, this.mDestAlpha, this.mDestAlpha, this.mDestAlpha);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, this.mTextureId);
        gl.glTexCoordPointer(MESH_TEXCOORD_DIM, GL10.GL_FLOAT, 0, this.mVertexTexCoordBuf);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, MESH_VERTEX_NUM);

        gl.glPopMatrix();
    }

    public void setAccelerateDistanceX(float distanceX) {
        this.mDistanceX = distanceX;
    }

    public void setDelcelerateStartPointAndTime(Vector3f value) {
        this.mDelcelerateStartPoint.set(value);
        this.mDelcelerateNowPeriod = 0;
    }

    @Override
    public void setDspeed(Vector3f value) {
        this.mDspeed.set(value);
    }

    public void setHuffFlySpeed(Vector3f value) {
        this.mHuffFlySpeed.set(value);
    }

    public void setMaxAngle(float angle) {
        this.mMaxAngle = angle;
    }

    public void setMinAngle(float angle) {
        this.mMinAngle = angle;
    }
}
