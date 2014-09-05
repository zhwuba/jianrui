package com.wb.launcher3.liveweather;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLU;
import android.os.SystemClock;

import com.dutils.math.RandomUtil;
import com.dutils.math.Vector3f;
import com.wb.launcher3.R;

public class GLCloudyRender extends LiveWeatherGLRender {
    private static final int CLOUDS_NUM = 6;
    private static final int CLOUDS_TYPE = 6;
    private static final int CLOUDY_RENDER_DELAY = 35;
    private static final float CLOUD_X_OFFSET_ADJUST_RATIO = 0.4f;
    private static final float CLOUD_X_OFFSET_DECELETATE_RATIO = 0.8f;

    private static final float DEFAULT_HEIGHT = 0f;
    private static final float DEFAULT_WIDTH = 0f;
    private static final float MIN_CLOUD_ALPHA = 0f;
    private static final float MAX_CLOUD_ALPHA = 0.7f;
    private static final float MIN_RESPONEDED_SLIDE_OFFSET = 0f;
    private static final float PERSPECTIVE_SCALE = 0.41f;

    private static final int TEXTURE_AMPLIFY = 4;
    private int[] mCloudsTextureAsserts;
    private TextureInfo[] mCloudsTextureInfos;
    private float[] mCloudsZ;
    private boolean mDealingOffset;
    private float mDirect;
    private float mDspeedx;
    private float mFirstOffset;
    private GLClouds[] mGLClouds;
    private long mOffsetTime;
    private long mOffsetTimePeriod;

    public GLCloudyRender(Context context, LiveWeatherGLView liveWeatherGLView) {
        super(context, liveWeatherGLView);

        this.mDealingOffset = false;
        this.mDirect = 1f;
        this.mOffsetTimePeriod = 1400;
        this.mOffsetTime = 0;
        this.mFirstOffset = 0f;
        this.mDspeedx = 0f;
        this.mCloudsTextureInfos = new TextureInfo[CLOUDS_NUM];
        this.mCloudsZ = new float[] { -0.1f, 0f, 0f, -0.4f, -2.2f, -0.2f };
        this.mCloudsTextureAsserts = new int[] { R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3,
                R.drawable.cloud4, R.drawable.cloud5, R.drawable.cloudfunny1 };
        this.mGLClouds = new GLClouds[CLOUDS_NUM];

        for (int i = 0; i < CLOUDS_NUM; ++i) {
            this.mGLClouds[i] = new GLClouds();
        }
    }

    private void dealOffset(long deltaTime) {
        float offset = this.getOffset();
        if (offset > 15f) {
            offset = 15f;
            if (this.mDealingOffset && this.mDirect == -1f) {
                this.mOffsetTime = 0;
            }

            this.mDirect = 1f;
        } else if (offset >= -15f) {
            offset = 0f;
        } else {
            offset = -15f;
            if (this.mDealingOffset && this.mDirect == 1f) {
                this.mOffsetTime = 0;
            }

            this.mDirect = -1f;
        }

        if (this.mDealingOffset) {
            this.mOffsetTime += deltaTime;
            if (this.mOffsetTime < this.mOffsetTimePeriod) {
                this.mDspeedx = this.getDspeedx(this.mOffsetTime, this.mOffsetTimePeriod) * this.mWidth / 1080f;
                this.translate(this.mDirect * this.mDspeedx, 0f, 0f);
            } else {
                this.mDealingOffset = false;
                this.mOffsetTime = 0;
            }
        } else if (offset != 0f) {
            this.mDealingOffset = true;
        }
    }

    private float getAlpha(float time, float timeperiod) {
        return 0.85f - 0.15f * (((float) Math.cos(6.283185 / ((timeperiod)) * ((time)))));
    }

    private float getDspeedx(long time, long timeperiod) {
        float v4 = 0.055f;
        float v3 = 0.1f;

        if (time >= timeperiod * v3) {
            return timeperiod * v3 * v4 - (time - timeperiod * v3) * v4 / 9f;
        } else {
            return time * 0.05f;
        }
    }

    @Override
    public float getOffset() {
        synchronized (this.mLock) {
            if (this.mOffset >= 1f || this.mOffset <= -1f) {
                this.mIsUpdatedOffset = true;
                return this.mOffset;
            } else {
                this.mOffset = 0f;
                this.mIsUpdatedOffset = true;
                return 0;
            }
        }
    }

    private float getRandomPositionY(int height, int i) {
        return height * (RandomUtil.floatRange(i * 0.07f + 0.5f, 0.57f + i * 0.07f) - 0.5f);
    }

    private void initClouds(int width, int height) {
        float heightBase = 1920f;
        float widthBase = 1080f;

        Vector3f speed = new Vector3f();
        Vector3f position = new Vector3f();

        for (int i = 0; i < CLOUDS_NUM; ++i) {
            if (i % 2 == 0) {
                speed.x = 1.5f * ((width)) / widthBase;
            } else {
                speed.x = -1.5f * ((width)) / widthBase;
            }

            position.x = ((this.mCloudsTextureInfos[i].mWidth * 4 + width))
                    * RandomUtil.floatRange(-0.5f, 0.5f);
            position.y = this.getRandomPositionY(height, i);
            position.z = ((width)) * this.mCloudsZ[i];
            speed.y = 0f;
            speed.z = 0f;

            float alphaTimePeriod = RandomUtil.floatRange(15000f, 18000f);
            this.mGLClouds[i].setAlpha(0f);
            this.mGLClouds[i].setAlphaTime(0f);
            this.mGLClouds[i].setAlphaTimePeriod(alphaTimePeriod);
            this.mGLClouds[i].setDspeed(speed);
            this.mGLClouds[i].setPosition(position);
            this.mGLClouds[i].setWidth(((this.mCloudsTextureInfos[i].mWidth * 4 * height)) / heightBase);
            this.mGLClouds[i].setHeight(((this.mCloudsTextureInfos[i].mHeight * 4 * height)) / heightBase);
            this.mGLClouds[i].buildMesh();
            this.mGLClouds[i].setTextureId(this.mCloudsTextureInfos[i].mId);
        }
    }

    private void initTexture(GL10 gl) {
        for (int i = 0; i < 6; ++i) {
            this.mCloudsTextureInfos[i] = this.loadTexture2(gl, this.mCloudsTextureAsserts[i]);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        long uptime = SystemClock.uptimeMillis();
        this.dealOffset(CLOUDY_RENDER_DELAY);

        for (int i = 0; i < CLOUDS_NUM; ++i) {
            this.mGLClouds[i].onDraw(gl, this.mGLAlpha);
        }

        this.updateClouds(this.updateDrawTime(CLOUDY_RENDER_DELAY));
        int renderDelay = (int) (CLOUDY_RENDER_DELAY - (SystemClock.uptimeMillis() - uptime));
        this.requestRenderDelayed(renderDelay > 0 ? renderDelay : 0);
    }

    public void onPause() {
        this.mFirstOffset = 0f;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (this.mWidth != width || this.mHeight != height) {
            this.initClouds(width, height);
        }

        this.mWidth = width;
        this.mHeight = height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45f, (float) width / (float) height, 1f, 5000f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        GLU.gluLookAt(gl, 0f, 0f, height * ATAN2_45_DEGREE, 0f, 0f, 0f, 0f, 1f, 0f);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        this.initTexture(gl);
    }

    @Override
    public void setOffset(float offset) {
        if (this.mDealingOffset) {
            this.mFirstOffset = 0f;
        } else if (offset == 0f) {
            this.mFirstOffset = 0f;
        } else if (this.mFirstOffset == 0f) {
            this.mFirstOffset = offset;
        }

        super.setOffset(0f * 0.4f);
    }

    private void translate(float x, float y, float z) {
        for (int i = 0; i < CLOUDS_NUM; ++i) {
            this.mGLClouds[i].setTransition(x, y, z);
        }
    }

    private void updateClouds(long deltaTime) {
        for (int i = 0; i < CLOUDS_NUM; ++i) {
            GLClouds cloud = this.mGLClouds[i];
            Vector3f position = cloud.getPosition();
            position.add(cloud.getDspeed());

            float alphaTime = cloud.getAlphaTime();
            float alphaTimePeriod = cloud.getAlphaTimePeriod();
            float alpha = cloud.getAlpha();
            if (alpha < MAX_CLOUD_ALPHA) {
                alpha += 0.02f;
            } else {
                alpha = this.getAlpha(alphaTime, alphaTimePeriod);
                alphaTime += deltaTime;
            }

            cloud.setAlpha(alpha);
            if (alphaTime > alphaTimePeriod) {
                alphaTime = 0f;
            }

            cloud.setAlphaTime(alphaTime);
            if (i % 2 == 0) {
                if (position.x > 0.5f * (this.mWidth * (1f - this.mCloudsZ[i] * PERSPECTIVE_SCALE) + this.mCloudsTextureInfos[i].mWidth * 4)) {
                    position.x = -0.5f * this.mWidth * (1f - this.mCloudsZ[i] * PERSPECTIVE_SCALE)
                            - this.mCloudsTextureInfos[i].mWidth * 4 * RandomUtil.floatRange(0.5f, 0.65f);
                    position.y = this.getRandomPositionY(this.mHeight, i);
                }
            } else if (position.x < -0.5f
                    * (this.mWidth * (1f - this.mCloudsZ[i] * PERSPECTIVE_SCALE) + this.mCloudsTextureInfos[i].mWidth * 4)) {
                position.x = 0.5f * this.mWidth * (1f - this.mCloudsZ[i] * PERSPECTIVE_SCALE)
                        + this.mCloudsTextureInfos[i].mWidth * 4 * RandomUtil.floatRange(0.5f, 0.75f);
                position.y = this.getRandomPositionY(this.mHeight, i);
            }
        }
    }
}
