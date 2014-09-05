package com.wb.launcher3.liveweather;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;
import com.dutils.math.Vector3f;

public class GLSnowDrop extends GLMesh {
    private static final float DROP_ALL_PERIOD = 0f;
    private static final float DROP_SHOW_PERIOD = 0f;
    private static final float DROP_VANISH_PERIOD = 0f;

    public int mAcceleratePeroid;
    public float mDropAccelerateY;
    public Vector3f mStartPosition;

    public GLSnowDrop() {
        super();
        this.mStartPosition = new Vector3f();
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
        this.mSrcAlpha = 1f;
    }

    public void generateDistanceAndAlpha(long deltaTime, float render_duration) {
        float alpha = 0f;

        this.mAcceleratePeroid += (int) deltaTime;
        this.getPosition().x = this.getDspeed().x / render_duration * this.mAcceleratePeroid + this.mStartPosition.x;
        this.getPosition().y = this.getDspeed().y / render_duration * this.mAcceleratePeroid + 0.5f
                * this.mDropAccelerateY * this.mAcceleratePeroid * this.mAcceleratePeroid + this.mStartPosition.y;
        if (this.mAcceleratePeroid >= 1000f) {
            this.mSrcAlpha = (2500f - this.mAcceleratePeroid) / 1500f;
            if (this.mSrcAlpha > 0f) {
                alpha = this.mSrcAlpha;
            }

            this.mSrcAlpha = alpha;
        }
    }

    public void onDraw(GL10 gl, float alpha) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();

        this.matTrans.setIdentity();
        this.matTrans.setTranslation(this.mPosition.x, this.mPosition.y, this.mPosition.z);
        this.matModel.set(this.matTrans);
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

    public void resetData() {
        this.mAcceleratePeroid = 0;
        this.mSrcAlpha = 1f;
    }

    public void setStartPosition(Vector3f startPosition) {
        this.mStartPosition.set(startPosition);
    }
}
