package com.wb.launcher3.liveweather;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.dutils.buffer.IBufferFactory;
import com.dutils.math.Matrix4f;

public class GLRaindrop extends GLMesh {
    private GLRainDropAlgorithm mGLRainDropAlgorithm;

    public GLRaindrop() {
        super();
        this.mGLRainDropAlgorithm = null;
        this.mVertexPositionBuf = IBufferFactory.newFloatBuffer(MESH_POSITION_DATA_LEN);
        this.mVertexTexCoordBuf = IBufferFactory.newFloatBuffer(MESH_TEXCOORD_DATA_LEN);
    }

    public void onDraw(GL10 gl, float alpha) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();

        FloatBuffer floatBuffer = null;
        if (this.mGLRainDropAlgorithm != null) {
            floatBuffer = this.mGLRainDropAlgorithm.glRainDropTransform();
        }

        if (floatBuffer == null) {
            floatBuffer = new Matrix4f().asFloatBuffer();
        }

        gl.glMultMatrixf(floatBuffer);

        float fadeLevel = 1f;
        if (this.mGLRainDropAlgorithm != null) {
            fadeLevel = this.mGLRainDropAlgorithm.getRainDropFadeLevel();
        }

        this.mDestAlpha = fadeLevel * alpha;
        gl.glColor4f(this.mDestAlpha, this.mDestAlpha, this.mDestAlpha, this.mDestAlpha);
        gl.glVertexPointer(MESH_POSITION_DIM, GL10.GL_FLOAT, 0, this.mVertexPositionBuf);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, this.mTextureId);
        gl.glTexCoordPointer(MESH_TEXCOORD_DIM, GL10.GL_FLOAT, 0, this.mVertexTexCoordBuf);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, MESH_VERTEX_NUM);

        gl.glPopMatrix();

        if (this.mGLRainDropAlgorithm == null) {
            return;
        }

        this.mGLRainDropAlgorithm
                .updateRainDropInfo(((float) ((((double) (2f * ((System.nanoTime()))))) / 1000000000)));
    }

    @Override
    public void onDraw(GL10 gl) {
        this.onDraw(gl, 1f);
    }

    public float randomScaleValue(float amplitude) {
        if (this.mGLRainDropAlgorithm != null) {
            return this.mGLRainDropAlgorithm.randomScaleValue(amplitude);
        }

        return 1f;
    }

    public int randomTextureValue() {
        if (this.mGLRainDropAlgorithm != null) {
            return this.mGLRainDropAlgorithm.randomTextureValue();
        }

        return 0;
    }

    public void resetScreenOffsetX() {
        if (this.mGLRainDropAlgorithm == null) {
            return;
        }

        this.mGLRainDropAlgorithm.resetScreenOffsetX();
    }

    public void setAdaptRotateHeight(float randomHeight) {
        if (this.mGLRainDropAlgorithm != null) {
            this.mGLRainDropAlgorithm.setAdaptRotateHeight(randomHeight);
        }
    }

    public void setRainDropAlgorithm(float width, float height) {
        this.mGLRainDropAlgorithm = GLRainDropAlgorithm.getInstance(width, height, false);
    }

    public void setScreenOffsetX(float transition) {
        if (this.mGLRainDropAlgorithm == null) {
            return;
        }

        this.mGLRainDropAlgorithm.setScreenOffsetX(transition);
    }

    public void setTagRainDropDepth(int tagRainDropDepth) {
        if (this.mGLRainDropAlgorithm != null) {
            this.mGLRainDropAlgorithm.setTagRainDropDepth(tagRainDropDepth);
        }
    }
}
