package com.wb.launcher3.liveweather;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;

public class GLSunnySpot extends GLMesh {
    private float angleA;
    private float angleAStep;
    private float angleB;
    private float fadeAlpha;
    private boolean isFadeining;
    private boolean isFadeouting;
    private int leftPointX;
    private int leftPointY;
    private int longAxis;
    private float positionX;
    private float positionY;
    private float scale;
    private int shortAxis;

    public GLSunnySpot() {
        super();
        this.fadeAlpha = 0f;
        this.isFadeouting = false;
        this.isFadeining = false;
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
        this.mSrcAlpha = 1f;
    }

    public void fadeInByStep() {
        if (this.fadeAlpha == 1f) {
            return;
        }

        if (this.fadeAlpha >= 1f) {
            this.fadeAlpha = 1f;
        } else {
            this.fadeAlpha += 0.02f;
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

    public float getAngleA() {
        return this.angleA;
    }

    public float getAngleAStep() {
        return this.angleAStep;
    }

    public float getAngleB() {
        return this.angleB;
    }

    public int getLeftPx() {
        return this.leftPointX;
    }

    public float getLeftPy() {
        return this.leftPointY;
    }

    public int getLongAxis() {
        return this.longAxis;
    }

    public float getPositionY() {
        return (float) (this.shortAxis * Math.sin(this.angleA) * Math.cos(this.angleB) + this.longAxis
                * Math.cos(this.angleA) * Math.sin(this.angleB) - this.longAxis * Math.sin(this.angleB) + this.leftPointY);
    }

    public float getScale() {
        return this.scale;
    }

    public int getShortAxis() {
        return this.shortAxis;
    }

    public void onDraw(GL10 gl, float alpha) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();

        this.matTrans.setIdentity();
        this.matTrans.setTranslation(this.positionX, this.positionY, 0f);
        this.matModel.set(this.matTrans);
        this.matRots.setIdentity();
        this.matRots.rotZ(this.mRotation.z);
        this.matModel.mul(this.matRots);
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

    @Override
    public void onDraw(GL10 gl) {
        this.onDraw(gl, 1f);
    }

    public void setAngleA(float angle) {
        this.angleA = angle;
    }

    public void setAngleAStep(float angle) {
        this.angleAStep = angle;
    }

    public void setAngleB(float angle) {
        this.angleB = angle;
    }

    public void setLeftPx(int x) {
        this.leftPointX = x;
    }

    public void setLeftPy(int y) {
        this.leftPointY = y;
    }

    public void setLongAxis(int axis) {
        this.longAxis = axis;
    }

    public void setScale(float sc) {
        this.scale = sc;
    }

    public void setShortAxis(int axis) {
        this.shortAxis = axis;
    }

    public void updatePosition() {
        float x = (float) (this.longAxis * Math.cos(this.angleA));
        float y = (float) (this.shortAxis * Math.sin(this.angleA));

        this.positionX = (float) (x * Math.cos(this.angleB) - y * Math.sin(this.angleB) - this.longAxis
                * Math.cos(this.angleB) + this.leftPointX);
        this.positionY = (float) (y * Math.cos(this.angleB) + x * Math.sin(this.angleB) - this.longAxis
                * Math.sin(this.angleB) + this.leftPointY);
    }
}
