package com.wb.launcher3.liveweather;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.Log;

import com.dutils.math.Vector3f;
import com.wb.launcher3.liveweather.LiveWeatherGLView.IconCallBack;

public class LiveWeatherGLRender implements Renderer {
    public class TextureInfo {
        public int mHeight;
        public int mWidth;
        public int mId;

        public LiveWeatherGLRender render;

        public TextureInfo(LiveWeatherGLRender render) {
            this.render = render;
        }
    }

    protected static final float ATAN2_45_DEGREE = 1.207107f;
    protected static final float TAN_22_5 = 0.414214f;
    protected static final int RENDER_DELAY = 45;
    protected static final float RENDER_DURATION = 16.666666f;

    public static final int TAG_ALPHA_FADEIN = 1;
    public static final int TAG_ALPHA_FADEOUT = -1;
    public static final int TAG_ALPHA_NORMAL = 0;

    protected int mAcceleration;
    protected Context mContext;
    protected int mFadeTag;
    protected float mGLAlpha;
    protected int mHeight;
    protected IconCallBack mIconCallBack;
    protected boolean mIsUpdatedOffset;
    protected long mLastDrawTime;
    protected LiveWeatherGLView mLiveWeatherGLView;
    protected Object mLock;
    protected float mOffset;
    protected int mWidth;

    public LiveWeatherGLRender(Context context, LiveWeatherGLView liveWeatherGLView) {
        this.mGLAlpha = 1f;
        this.mFadeTag = TAG_ALPHA_NORMAL;
        this.mContext = context;
        this.mLiveWeatherGLView = liveWeatherGLView;
        this.mLock = new Object();
    }

    public int[] changeXY(int x, int y) {
        int[] xy;

        if (this.mWidth == 0 || this.mHeight == 0) {
            xy = null;
        } else {
            xy = new int[] { this.mWidth / 2 + x, this.mHeight / 2 - y };
        }

        return xy;
    }

    public int[] changeXYReverse(int x, int y) {
        int[] xy;

        if (this.mWidth == 0 || this.mHeight == 0) {
            xy = null;
        } else {
            xy = new int[] { x - this.mWidth / 2, this.mHeight / 2 - y };
        }

        return xy;
    }

    protected void checkGlError(GL10 gl, String info) {
        int error = gl.glGetError();
        if (error != 0) {
            Log.w("LiveWeatherGLRender", info + " GL error = " + GLU.gluErrorString(error));
        }
    }

    protected boolean checkInViewport(GLMesh glMesh, float vpWidth, float vpHeight) {
        Vector3f position = glMesh.getPosition();
        float width = glMesh.getWidth();
        float height = glMesh.getHeight();
        if (position.x + width < -vpWidth || position.x - width > vpWidth || position.y + height < -vpHeight
                || position.y - height > vpHeight) {
            return false;
        } else {
            return true;
        }
    }

    protected boolean checkInViewport(GLMesh glMesh, float vpWidth, float vpHeight, float zDistance) {
        Vector3f position = glMesh.getPosition();
        float width = glMesh.getWidth();
        float height = glMesh.getHeight();
        float newVpHeight = (zDistance - position.z) * TAN_22_5;
        float newVpWidth = vpWidth / vpHeight * newVpHeight;
        if (position.x + width < -newVpWidth || position.x - width > newVpWidth || position.y + height < -newVpHeight
                || position.y - height > newVpHeight) {
            return false;
        } else {
            return true;
        }
    }

    public void fadeIn() {
        this.mFadeTag = TAG_ALPHA_FADEIN;
    }

    public void fadeOut() {
        this.mFadeTag = TAG_ALPHA_FADEOUT;
    }

    public int getAcceleration() {
        synchronized (this.mLock) {
            return this.mAcceleration;
        }
    }

    public int getFadeTag() {
        return this.mFadeTag;
    }

    public float getGLAlpha() {
        return this.mGLAlpha;
    }

    public float getOffset() {
        return 0;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public static boolean isPowerOf2(int n) {
        if ((-n & n) == n) {
            return true;
        } else {
            return false;
        }
    }

    protected int loadTexture(GL10 gl, int resource) {
        Bitmap bitmap = null;

        int[] textures = new int[1];
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glGenTextures(1, textures, 0);
        this.checkGlError(gl, "loadTexture glGenTextures");

        int texture = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
        this.checkGlError(gl, "loadTexture glBindTexture");

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        InputStream inputStream = this.mContext.getResources().openRawResource(resource);
        try {
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            try {
                inputStream.close();
            } catch (IOException v1) {
                Log.w("LiveWeatherGLRender", "loadTexture IOException");
            }
        }

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        this.checkGlError(gl, "loadTexture texImage2D");

        bitmap.recycle();
        return texture;
    }

    protected TextureInfo loadTexture(GL10 gl, String res) {
        Bitmap bitmap = null;

        int[] textures = new int[1];
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glGenTextures(1, textures, 0);
        this.checkGlError(gl, "loadTexture glGenTextures");

        TextureInfo textureInfo = new TextureInfo(this);
        textureInfo.mId = textures[0];
        gl.glBindTexture(3553, textureInfo.mId);
        this.checkGlError(gl, "loadTexture glBindTexture");

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        InputStream inputStream = null;
        try {
            inputStream = this.mContext.getAssets().open(res);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException v7) {
            Log.e("LiveWeatherGLRender", "createImage() is close error.");
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (!LiveWeatherGLRender.isPowerOf2(width) || !LiveWeatherGLRender.isPowerOf2(height)) {
            int newWidth = LiveWeatherGLRender.nextPowerOf2(width);
            int newHeight = LiveWeatherGLRender.nextPowerOf2(height);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            bitmap.recycle();
            bitmap = scaledBitmap;
            width = newWidth;
            height = newHeight;
        }

        textureInfo.mWidth = width;
        textureInfo.mHeight = height;
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        this.checkGlError(gl, "loadTexture texImage2D");

        bitmap.recycle();
        return textureInfo;
    }

    protected TextureInfo loadTexture2(GL10 gl, int resource) {
        Bitmap bitmap = null;

        int[] textures = new int[1];
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glGenTextures(1, textures, 0);
        this.checkGlError(gl, "loadTexture glGenTextures");

        TextureInfo textureInfo = new TextureInfo(this);
        textureInfo.mId = textures[0];
        gl.glBindTexture(3553, textureInfo.mId);
        this.checkGlError(gl, "loadTexture glBindTexture");

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        InputStream inputStream = null;
        try {
            inputStream = this.mContext.getResources().openRawResource(resource);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException v7) {
            Log.e("LiveWeatherGLRender", "createImage() is close error.");
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (!LiveWeatherGLRender.isPowerOf2(width) || !LiveWeatherGLRender.isPowerOf2(height)) {
            int newWidth = LiveWeatherGLRender.nextPowerOf2(width);
            int newHeight = LiveWeatherGLRender.nextPowerOf2(height);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            bitmap.recycle();
            bitmap = scaledBitmap;
            width = newWidth;
            height = newHeight;
        }

        textureInfo.mWidth = width;
        textureInfo.mHeight = height;
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        this.checkGlError(gl, "loadTexture texImage2D");

        bitmap.recycle();
        return textureInfo;
    }

    public static int nextPowerOf2(int n) {
        --n;
        n |= n >>> 16;
        n |= n >>> 8;
        n |= n >>> 4;
        n |= n >>> 2;
        return (n | n >>> 1) + 1;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (this.mFadeTag < TAG_ALPHA_NORMAL) {
            this.mGLAlpha -= 0.17f;
            if (this.mGLAlpha < 0f) {
                this.mGLAlpha = 0f;
                this.mFadeTag = TAG_ALPHA_NORMAL;
                if (this.mIconCallBack != null) {
                    this.mIconCallBack.onFadeoutEnd();
                }
            }
        } else if (this.mFadeTag > TAG_ALPHA_NORMAL) {
            this.mGLAlpha += 0.1f;
            if (this.mGLAlpha > 1f) {
                this.mGLAlpha = 1f;
                this.mFadeTag = TAG_ALPHA_NORMAL;
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    public void requestRenderDelayed(long delay) {
        this.mLiveWeatherGLView.requestRenderDelayed(delay);
    }

    public void resetLastDrawTime() {
        this.mLastDrawTime = 0;
    }

    public void resetOffset() {
        synchronized (this.mLock) {
            this.mOffset = 0f;
            this.mIsUpdatedOffset = true;
        }
    }

    public void setAcceleration(int acceleration) {
        synchronized (this.mLock) {
            this.mAcceleration = acceleration;
        }
    }

    public void setGLAlpha(float alpha) {
        this.mGLAlpha = alpha;
    }

    public void setIconCallBack(IconCallBack iconCallBack) {
        this.mIconCallBack = iconCallBack;
    }

    public void setOffset(float offset) {
        synchronized (this.mLock) {
            if (!this.mIsUpdatedOffset) {
                this.mOffset += offset;
            } else {
                this.mOffset = offset;
                this.mIsUpdatedOffset = false;
            }
        }
    }

    protected long updateDrawTime(long delay) {
        long uptime = SystemClock.uptimeMillis();
        long drawtime = 0;

        if (this.mLastDrawTime > 0) {
            drawtime = uptime - this.mLastDrawTime;
        }

        if (drawtime < 0) {
            drawtime = 0;
        }

        this.mLastDrawTime = uptime;
        return drawtime;
    }

    public void updateEmptyAreaMotionEvent() {
    }

    public void updateMovtionEvent(float x, float y, int motionEvent) {
    }
}
