package com.wb.launcher3.liveweather;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;
import com.dutils.math.Vector3f;

public class GLIconRaindrop extends GLMesh {
    public float distance;
    public boolean isConstantSpeedEnd;
    private Vector3f mAccele;
    public int mFrameCount;
    int mIndex;
    private Vector3f mInitialPosition;
    private Vector3f mInitialSpeed;
    private Vector3f mTempPostion;
    public float scale;
    public int timeSpend;

    public GLIconRaindrop() {
        super();
        this.mFrameCount = 0;
        this.scale = 1f;
        this.distance = 0f;
        this.timeSpend = 1;
        this.mIndex = -1;
        this.isConstantSpeedEnd = false;
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
        this.mInitialPosition = new Vector3f();
        this.mAccele = new Vector3f();
        this.mInitialSpeed = new Vector3f();
        this.mTempPostion = new Vector3f();
        this.mSrcAlpha = 0f;
    }

    public Vector3f getAccele() {
        return this.mAccele;
    }

    public Vector3f getInitialPosition() {
        return this.mInitialPosition;
    }

    public Vector3f getInitialSpeed() {
        return this.mInitialSpeed;
    }

    public Vector3f getTempPostion() {
        return this.mTempPostion;
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

    public void setAccele(Vector3f value) {
        this.mAccele.set(value);
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

    public void setInitialPosition(Vector3f value) {
        this.mInitialPosition.set(value);
    }

    public void setInitialSpeed(Vector3f value) {
        this.mInitialSpeed.set(value);
    }

    public void setTempPostion(Vector3f value) {
        this.mTempPostion.set(value);
    }
}
