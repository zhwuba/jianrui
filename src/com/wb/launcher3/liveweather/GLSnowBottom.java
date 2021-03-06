package com.wb.launcher3.liveweather;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;

public class GLSnowBottom extends GLMesh {
    public float mAngleSpeedX;
    private float mHeightRate;
    private float mWidthRate;

    public GLSnowBottom() {
        super();
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
        this.mSrcAlpha = 0f;
    }

    @Override
    public void buildMesh() {
        super.buildMesh();
        float[] texCoordData = new float[] { 0f, 0f, this.mWidthRate, 0f, 0f, this.mHeightRate, this.mWidthRate,
                this.mHeightRate };
        this.mVertexTexCoordBuf.position(0);
        this.mVertexTexCoordBuf.put(texCoordData);
        this.mVertexTexCoordBuf.position(0);
    }

    @Override
    public void onDraw(GL10 gl) {
        this.onDraw(gl, 1f);
    }

    public void onDraw(GL10 gl, float alpha) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();

        this.matTrans.setIdentity();
        this.matTrans.setTranslation(this.mPosition.x, this.mPosition.y, this.mPosition.z);
        this.matModel.set(this.matTrans);
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

    public void setTextureHeightRate(float heightRate) {
        this.mHeightRate = heightRate;
    }

    public void setTextureWidthRate(float widthRate) {
        this.mWidthRate = widthRate;
    }
}
