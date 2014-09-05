package com.wb.launcher3.liveweather;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;
import com.dutils.math.Vector3f;

public class GLSnowFlake extends GLMesh {
    private static final int DELTA_TIME = 50;
    protected static final float RENDER_DURATION = 16.666666f;
    public float mAccelerateSpeedY;
    public float mAngleSpeedX;
    public float mDelcelerateAllPeriod;
    public long mDelcelerateNowPeriod;
    public Vector3f mDelcelerateStartPoint;
    public float mDistanceY;
    public Vector3f mHuffSpeed;
    public int mSnowState;

    public GLSnowFlake() {
        super();
        this.mSnowState = 0;
        this.mAccelerateSpeedY = 0f;
        this.mDelcelerateStartPoint = new Vector3f();
        this.mDelcelerateAllPeriod = 0f;
        this.mDelcelerateNowPeriod = 0;
        this.mDistanceY = 0f;
        this.mHuffSpeed = new Vector3f();
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
    }

    public void generateAccelerateSpeedY() {
        this.mAccelerateSpeedY = -1f * this.mDspeed.y * this.mDspeed.y / (this.mDistanceY * 2f * 50f * 50f);
        this.mDelcelerateAllPeriod = this.mDistanceY * 2f / (Math.abs(this.mDspeed.y) / 50f);
    }

    public float getDelcelerateDistanceY(long deltaTime) {
        this.mDelcelerateNowPeriod += 50;
        if (this.mDelcelerateNowPeriod <= this.mDelcelerateAllPeriod) {
            return Math.abs(this.getDspeed().y) / 50f * this.mDelcelerateNowPeriod + 0.5f * this.mAccelerateSpeedY
                    * this.mDelcelerateNowPeriod * this.mDelcelerateNowPeriod;
        } else {
            return this.mDistanceY;
        }
    }

    public Vector3f getDelceleratePeriod() {
        return this.mDelcelerateStartPoint;
    }

    public Vector3f getDelcelerateStartPoint() {
        return this.mDelcelerateStartPoint;
    }

    public float getVatualX() {
        float xDis = Math.abs(this.mDistanceY * this.getDspeed().x / this.getDspeed().y);

        if (this.getDspeed().x <= 0f) {
            xDis = -xDis;
        }

        return this.mPosition.x + xDis;
    }

    public float getVatualY() {
        return this.mPosition.y - this.mDistanceY;
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

    public void setAccelerateDistanceY(float distanceY) {
        this.mDistanceY = distanceY;
    }

    public void setDelcelerateStartPointAndTime(Vector3f value) {
        this.mDelcelerateStartPoint.set(value);
        this.mDelcelerateNowPeriod = 0;
    }
}
