package com.wb.launcher3.liveweather;

import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLU;
import android.os.SystemClock;
import android.util.Log;

import com.dutils.math.RandomUtil;
import com.dutils.math.Vector3f;
import com.wb.launcher3.R;

public class GLSunnyRender extends LiveWeatherGLRender {
    private static final int DAWN_TIME = 6;
    private static final float DEFAULT_HEIGHT = 0f;
    private static final float DEFAULT_WIDTH = 0f;
    private static final int DUSK_TIME = 20;
    private static final int STATUSBAR_SHADER_NUM = 1;
    private static final int SUNNY_HOLO_NUM = 1;
    private static final int SUNNY_HOLO_TYPE = 1;
    private static final int SUNNY_LIGHT_GROUPNUM = 4;
    private static final int SUNNY_LIGHT_NUM = 8;
    private static final int SUNNY_LIGHT_TYPE = 4;
    private static final int SUNNY_RING = 0;
    private static final int SUNNY_RINGLIGHT_NUM = 4;
    private static final int SUNNY_RING_CROSS_LIGHT = 1;
    private static final int SUNNY_RING_DELAY = 800;
    private static final int SUNNY_RING_LIGHT = 4;
    private static final float SUNNY_RING_MAX_ALPHA = 0.8f;
    private static final float SUNNY_RING_MAX_SCALE = 0f;
    private static final int SUNNY_RING_NUM = 4;
    private static final int SUNNY_RING_RADIO = 2;
    private static final int SUNNY_RING_RAINBOW = 3;
    private static final float SUNNY_RING_SCALE = 0f;
    private static final int SUNNY_RING_SPOT = 4;
    private static final int SUNNY_RING_TIME = 1000;
    private static final int SUNNY_RING_TYPE = 5;
    private static final int SUNNY_SPOT_NUM = 8;
    private static final int SUNNY_SPOT_TYPE = 8;
    private static final int TEXTURE_AMPLIFY = 4;

    private int[] mDefaultLongAxis;
    private int[] mDefaultShortAxis;
    private GLSunnyCompent[] mGLStatusBarShader;
    private GLSunnyCompent[] mGLSunnyHolo;
    private GLSunnyCompent[] mGLSunnyLight;
    private GLSunnyCompent[] mGLSunnyRing;
    private GLSunnyCompent[] mGLSunnyRingLight;
    private GLSunnySpot[] mGLSunnySpot;
    private boolean mNewRing;
    private float mRingScalefactor;
    private int[] mStatusBarShaderTextureAsserts;
    private TextureInfo[] mStatusBarShaderTextureInfos;
    private long mSunnyBeginTime;
    private int[] mSunnyHoloTextureAsserts;
    private TextureInfo[] mSunnyHoloTextureInfos;
    private int[] mSunnyLightTextureAsserts;
    private TextureInfo[] mSunnyLightTextureInfos;
    private int[] mSunnyRingLightTextureAsserts;
    private TextureInfo[] mSunnyRingLightTextureInfos;
    private float mSunnyRingPositionX;
    private float mSunnyRingPositionY;
    private int[] mSunnyRingTextureAsserts;
    private TextureInfo[] mSunnyRingTextureInfos;
    private long mSunnySpotBeginTime;
    private long mSunnySpotMissTime;
    private int[] mSunnySpotTextureAsserts;
    private TextureInfo[] mSunnySpotTextureInfos;

    GLSunnyRender(Context context, LiveWeatherGLView liveWeatherGLView) {
        super(context, liveWeatherGLView);

        this.mDefaultLongAxis = new int[] { 230, 280, 380, 430, 600, 580, 650, 760 };
        this.mDefaultShortAxis = new int[] { 100, 115, 170, 180, 250, 255, 325, 380 };
        this.mSunnySpotBeginTime = SystemClock.uptimeMillis();
        this.mSunnyBeginTime = SystemClock.uptimeMillis();
        this.mRingScalefactor = 1f;
        this.mSunnySpotMissTime = 2500;
        this.mSunnyRingPositionX = 0f;
        this.mSunnyRingPositionY = 0f;
        this.mNewRing = false;
        this.mSunnySpotTextureInfos = new TextureInfo[SUNNY_SPOT_TYPE];
        this.mSunnyHoloTextureInfos = new TextureInfo[SUNNY_HOLO_TYPE];
        this.mSunnyLightTextureInfos = new TextureInfo[SUNNY_LIGHT_TYPE];
        this.mSunnyRingTextureInfos = new TextureInfo[SUNNY_RING_NUM];
        this.mSunnyRingLightTextureInfos = new TextureInfo[SUNNY_RING_LIGHT];
        this.mStatusBarShaderTextureInfos = new TextureInfo[STATUSBAR_SHADER_NUM];
        this.mSunnySpotTextureAsserts = new int[] { R.drawable.lightspot1, R.drawable.lightspot2,
                R.drawable.lightspot3, R.drawable.lightspot4, R.drawable.lightspot5, R.drawable.lightspot6,
                R.drawable.lightspot7, R.drawable.lightspot8 };
        this.mSunnyHoloTextureAsserts = new int[] { R.drawable.sunholo };
        this.mSunnyLightTextureAsserts = new int[] { R.drawable.sunnylight1, R.drawable.sunnylight2,
                R.drawable.sunnylight3, R.drawable.sunnylight4 };
        this.mSunnyRingTextureAsserts = new int[] { R.drawable.suncrosslight, R.drawable.sunradio, R.drawable.sunring,
                R.drawable.sunrainbow };
        this.mSunnyRingLightTextureAsserts = new int[] { R.drawable.lightspot1, R.drawable.lightspot4,
                R.drawable.lightspot6, R.drawable.lightspot7 };
        this.mStatusBarShaderTextureAsserts = new int[] { R.drawable.statusbarshader };
        this.mSunnyBeginTime = SystemClock.uptimeMillis();
        this.mGLSunnySpot = new GLSunnySpot[SUNNY_SPOT_NUM];
        this.mGLSunnyHolo = new GLSunnyCompent[SUNNY_HOLO_NUM];
        this.mGLSunnyLight = new GLSunnyCompent[SUNNY_LIGHT_NUM];
        this.mGLSunnyRing = new GLSunnyCompent[SUNNY_RING_NUM];
        this.mGLSunnyRingLight = new GLSunnyCompent[SUNNY_RING_LIGHT];
        this.mGLStatusBarShader = new GLSunnyCompent[STATUSBAR_SHADER_NUM];

        for (int i = 0; i < SUNNY_SPOT_NUM; ++i) {
            this.mGLSunnySpot[i] = new GLSunnySpot();
        }

        for (int i = 0; i < SUNNY_HOLO_NUM; ++i) {
            this.mGLSunnyHolo[i] = new GLSunnyCompent();
        }

        for (int i = 0; i < SUNNY_LIGHT_NUM; ++i) {
            this.mGLSunnyLight[i] = new GLSunnyCompent();
        }

        for (int i = 0; i < SUNNY_RING_NUM; ++i) {
            this.mGLSunnyRing[i] = new GLSunnyCompent();
        }

        for (int i = 0; i < SUNNY_RING_LIGHT; ++i) {
            this.mGLSunnyRingLight[i] = new GLSunnyCompent();
        }

        for (int i = 0; i < STATUSBAR_SHADER_NUM; ++i) {
            this.mGLStatusBarShader[i] = new GLSunnyCompent();
        }
    }

    private void fadeInByStep() {
        int i;

        for (i = 0; i < SUNNY_LIGHT_NUM; ++i) {
            this.mGLSunnyLight[i].fadeInByStep();
        }

        for (i = 0; i < SUNNY_SPOT_NUM; ++i) {
            this.mGLSunnySpot[i].fadeInByStep();
        }

        for (i = 0; i < SUNNY_RING_LIGHT; ++i) {
            this.mGLSunnyRingLight[i].fadeInByStep();
        }

        for (i = 0; i < SUNNY_HOLO_NUM; ++i) {
            this.mGLSunnyHolo[i].fadeInByStep();
        }

        for (i = 0; i < SUNNY_RING_NUM; ++i) {
            this.mGLSunnyRing[i].fadeInByStep();
        }

        for (i = 0; i < STATUSBAR_SHADER_NUM; ++i) {
            this.mGLStatusBarShader[i].fadeInByStep();
        }
    }

    private void fadeOutByStep() {
        int i;

        for (i = 0; i < SUNNY_LIGHT_NUM; ++i) {
            this.mGLSunnyLight[i].fadeOutByStepTo(0f);
        }

        for (i = 0; i < SUNNY_SPOT_NUM; ++i) {
            this.mGLSunnySpot[i].fadeOutByStepTo(0f);
        }

        for (i = 0; i < SUNNY_RING_LIGHT; ++i) {
            this.mGLSunnyRingLight[i].fadeOutByStepTo(0f);
        }

        for (i = 0; i < SUNNY_HOLO_NUM; ++i) {
            this.mGLSunnyHolo[i].fadeOutByStepTo(0f);
        }

        for (i = 0; i < SUNNY_RING_NUM; ++i) {
            this.mGLSunnyRing[i].fadeOutByStepTo(0.5f);
        }

        for (i = 0; i < STATUSBAR_SHADER_NUM; ++i) {
            this.mGLStatusBarShader[i].fadeOutByStepTo(0f);
        }
    }

    private float getBreathing(float time, float min, float max, float timeperiod) {
        return (float) (min + (max - min) * Math.sin(Math.PI * time / timeperiod));
    }

    private float getSunnySpotAlpha(float angle) {
        if (angle > 3f && angle < 3.283185f) {
            return 1f;
        } else if (angle <= 3f) {
            return angle / 3f;
        } else {
            return (float) ((Math.PI * 2 - angle) / 3);
        }
    }

    private float getSunnySpotAngleAStep(float angle, float anglestep) {
        if (angle <= Math.PI) {
            return (float) ((Math.PI - angle) * anglestep * 0.6f + anglestep);
        } else {
            return (float) ((angle - Math.PI) * anglestep * 0.6f + anglestep);
        }
    }

    private float getSunnySpotScale(float angle) {
        if (angle <= Math.PI) {
            return (float) (angle * 0.4f / Math.PI + 0.8f);
        } else {
            return (float) (0.4f - (angle - Math.PI) * 0.4f / Math.PI + 0.8f);
        }
    }

    private void initStatusBarShader(int width, int height) {
        float statusBarHeight = this.mIconCallBack == null ? 0 : this.mIconCallBack.getStatusBarHeight();

        Vector3f position = new Vector3f();
        position.x = -width * 0.25f;
        position.y = (height - statusBarHeight) * 0.5f;
        position.z = 0f;

        for (int i = 0; i < 1; ++i) {
            this.mGLStatusBarShader[i].setScale(1f);
            this.mGLStatusBarShader[i].setWidth(width * 0.5f);
            this.mGLStatusBarShader[i].setHeight(statusBarHeight);
            this.mGLStatusBarShader[i].setPosition(position);
            this.mGLStatusBarShader[i].buildMesh();
            this.mGLStatusBarShader[i].setTextureId(this.mStatusBarShaderTextureInfos[i].mId);
        }
    }

    private void initSunnyHolos(int width, int height) {
        Vector3f speed = new Vector3f();
        Vector3f position = new Vector3f();
        Vector3f rotation = new Vector3f();

        position.x = -540f * width / 1080f;
        position.y = 960f * height / 1920f;
        position.z = 0f;
        rotation.x = 0f;
        rotation.y = 0f;
        rotation.z = 0f;

        for (int i = 0; i < 1; ++i) {
            speed.x = 0f;
            speed.y = 0f;
            speed.z = -0.006f - 0.008f * i;
            this.mGLSunnyHolo[i].setAlpha(0f);
            this.mGLSunnyHolo[i].setWidth(this.mSunnyHoloTextureInfos[i].mWidth * 4 * height / 1920f);
            this.mGLSunnyHolo[i].setHeight(this.mSunnyHoloTextureInfos[i].mHeight * 4 * height / 1920f);
            this.mGLSunnyHolo[i].setScale(1f);
            this.mGLSunnyHolo[i].setPosition(position);
            this.mGLSunnyHolo[i].setRotation(rotation);
            this.mGLSunnyHolo[i].setRspeed(speed);
            this.mGLSunnyHolo[i].setTime(0f);
            this.mGLSunnyHolo[i].setTimePeriod(RandomUtil.floatRange(5500f, 7000f));
            this.mGLSunnyHolo[i].setATime(0f);
            this.mGLSunnyHolo[i].setATimePeriod(RandomUtil.floatRange(14000f, 18000f));
            this.mGLSunnyHolo[i].buildMesh();
            this.mGLSunnyHolo[i].setTextureId(this.mSunnyHoloTextureInfos[i].mId);
        }
    }

    private void initSunnyLight(int width, int height) {
        Vector3f speed = new Vector3f();
        Vector3f position = new Vector3f();
        Vector3f rotation = new Vector3f();
        Vector3f translation = new Vector3f();

        position.x = width * -300 / 1080f;
        position.y = height * 400 / 1920f;
        position.z = 0f;

        rotation.x = width * -540 / 1080f;
        rotation.y = height * 960 / 1920f;
        rotation.z = 0f;

        for (int i = 0; i < 4; ++i) {
            speed.x = 0f;
            speed.y = 0f;
            speed.z = 0f;

            this.mGLSunnyLight[i * 2].setAlpha(0f);
            this.mGLSunnyLight[i * 2 + 1].setAlpha(0f);
            this.mGLSunnyLight[i * 2].setWidth(this.mSunnyLightTextureInfos[0].mWidth * 4 * height / 1920f);
            this.mGLSunnyLight[i * 2 + 1].setWidth(this.mSunnyLightTextureInfos[0].mWidth * 4 * height / 1920f);
            this.mGLSunnyLight[i * 2].setHeight(this.mSunnyLightTextureInfos[0].mHeight * 4 * height / 1920f);
            this.mGLSunnyLight[i * 2 + 1].setHeight(this.mSunnyLightTextureInfos[0].mHeight * 4 * height / 1920f);

            translation.x = this.mGLSunnyLight[i * 2].getWidth() * 0.5f * 1.2f;
            translation.y = -this.mGLSunnyLight[i * 2].getHeight() * 0.5f * 1.2f;
            translation.z = 0f;

            this.mGLSunnyLight[i * 2].setScale(1f);
            this.mGLSunnyLight[i * 2 + 1].setScale(1f);
            this.mGLSunnyLight[i * 2].setPosition(position);
            this.mGLSunnyLight[i * 2 + 1].setPosition(position);
            this.mGLSunnyLight[i * 2].setRotation(rotation);
            this.mGLSunnyLight[i].setRotation(rotation);
            this.mGLSunnyLight[i * 2].setRspeed(speed);
            this.mGLSunnyLight[i * 2 + 1].setRspeed(speed);

            float aroudr = RandomUtil.floatRange(-0.2f + i * 0.08f, -0.12f + i * 0.08f);

            this.mGLSunnyLight[i * 2].setAroundR(aroudr);
            this.mGLSunnyLight[i * 2 + 1].setAroundR(aroudr);
            this.mGLSunnyLight[i * 2].setTime(0f);
            this.mGLSunnyLight[i * 2 + 1].setTime(0f);

            this.mGLSunnyLight[i * 2].setTimePeriod(aroudr);
            this.mGLSunnyLight[i * 2 + 1].setTimePeriod(aroudr);
            this.mGLSunnyLight[i * 2].setATime(0f);
            this.mGLSunnyLight[i * 2 + 1].setATime(0f);

            float atimeperiod = RandomUtil.floatRange(7500f + 500f * i, 8000f + 500f * i);

            this.mGLSunnyLight[i * 2].setATimePeriod(atimeperiod);
            this.mGLSunnyLight[i * 2 + 1].setATimePeriod(atimeperiod);
            this.mGLSunnyLight[i * 2].setCoordinateTranslation(translation);
            this.mGLSunnyLight[i * 2 + 1].setCoordinateTranslation(translation);
            this.mGLSunnyLight[i * 2].buildMesh();
            this.mGLSunnyLight[i * 2 + 1].buildMesh();
            this.mGLSunnyLight[i * 2].setTextureId(this.mSunnyLightTextureInfos[0].mId);
            this.mGLSunnyLight[i * 2 + 1].setTextureId(this.mSunnyLightTextureInfos[0].mId);
        }
    }

    private void initSunnyRingAndLight(int width, int height) {
        float flag;
        float randomFirstX = RandomUtil.floatRange(-50f * this.mWidth / 1080f, 50f * this.mWidth / 1080f);

        if (RandomUtil.flipCoin()) {
            flag = -1f;
        } else {
            flag = 1f;
        }

        float randomFirstY = (float) (flag * Math.sqrt(Math.pow(50, 2) - Math.pow(randomFirstX, 2)));

        for (int i = 0; i < SUNNY_RING_NUM; ++i) {
            this.mGLSunnyRing[i].setWidth(this.mSunnyRingTextureInfos[i].mWidth * height / 1920f);
            this.mGLSunnyRing[i].setHeight(this.mSunnyRingTextureInfos[i].mHeight * height / 1920f);
            this.mGLSunnyRing[i].buildMesh();
            this.mGLSunnyRing[i].setTextureId(this.mSunnyRingTextureInfos[i].mId);
            this.resetSunnyRing(this.mGLSunnyRing[i], i, randomFirstX, randomFirstY, true);
        }

        for (int i = 0; i < SUNNY_RING_LIGHT; ++i) {
            this.mGLSunnyRingLight[i].setWidth(this.mSunnyRingLightTextureInfos[i].mWidth * height / 1920f);
            this.mGLSunnyRingLight[i].setHeight(this.mSunnyRingLightTextureInfos[i].mHeight * height / 1920f);
            this.mGLSunnyRingLight[i].buildMesh();
            this.mGLSunnyRingLight[i].setTextureId(this.mSunnyRingLightTextureInfos[i].mId);
            this.resetSunnyRing(this.mGLSunnyRingLight[i], i + 4, randomFirstX, randomFirstY, true);
        }
    }

    private void initSunnySpots(int width, int height) {
        float baseWidth = 1080f;
        float baseHeight = 1920f;

        for (int i = 0; i < 8; ++i) {
            this.mGLSunnySpot[i].setAlpha(0f);
            this.mGLSunnySpot[i].setWidth(this.mSunnySpotTextureInfos[i].mWidth * height / baseHeight);
            this.mGLSunnySpot[i].setHeight(this.mSunnySpotTextureInfos[i].mHeight * height / baseHeight);
            this.mGLSunnySpot[i].setLongAxis((int) (this.mDefaultLongAxis[i] * height / baseHeight));
            this.mGLSunnySpot[i].setShortAxis((int) (this.mDefaultShortAxis[i] * width / baseWidth));
            this.mGLSunnySpot[i].setAngleA(0f);
            this.mGLSunnySpot[i].setAngleB(2.094395f);
            this.mGLSunnySpot[i].setAngleAStep(0.05236f);
            this.mGLSunnySpot[i].setLeftPx((int) (width * -500 / baseWidth));
            this.mGLSunnySpot[i].setLeftPy((int) (height * 900 / baseHeight));
            this.mGLSunnySpot[i].setScale(1f);
            this.mGLSunnySpot[i].buildMesh();
            this.mGLSunnySpot[i].setTextureId(this.mSunnySpotTextureInfos[i].mId);
            this.mGLSunnySpot[i].updatePosition();
        }
    }

    private void initTexture(GL10 gl) {
        int i;

        for (i = 0; i < SUNNY_SPOT_TYPE; ++i) {
            this.mSunnySpotTextureInfos[i] = this.loadTexture2(gl, this.mSunnySpotTextureAsserts[i]);
        }

        for (i = 0; i < SUNNY_HOLO_TYPE; ++i) {
            this.mSunnyHoloTextureInfos[i] = this.loadTexture2(gl, this.mSunnyHoloTextureAsserts[i]);
        }

        for (i = 0; i < SUNNY_LIGHT_TYPE; ++i) {
            this.mSunnyLightTextureInfos[i] = this.loadTexture2(gl, this.mSunnyLightTextureAsserts[i]);
        }

        for (i = 0; i < SUNNY_RING_NUM; ++i) {
            this.mSunnyRingTextureInfos[i] = this.loadTexture2(gl, this.mSunnyRingTextureAsserts[i]);
        }

        for (i = 0; i < SUNNY_RING_LIGHT; ++i) {
            this.mSunnyRingLightTextureInfos[i] = this.loadTexture2(gl, this.mSunnyRingLightTextureAsserts[i]);
        }

        for (i = 0; i < STATUSBAR_SHADER_NUM; ++i) {
            this.mStatusBarShaderTextureInfos[i] = this.loadTexture2(gl, this.mStatusBarShaderTextureAsserts[i]);
        }
    }

    private boolean isNowNight() {
        Calendar calendar = Calendar.getInstance();

        if (calendar != null) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            if (hour >= 6 && hour < 20) {
                return false;
            }

            return true;
        }

        return false;
    }

    private boolean isToggleBarOpen() {
        if (this.mIconCallBack != null) {
            return this.mIconCallBack.isToggleBarOpen();
        } else {
            Log.e("GLSunnyRender", "error mIconCallBack = null");
            return false;
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        long beginTime = SystemClock.uptimeMillis();

        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        if (beginTime - this.mSunnySpotBeginTime > this.mSunnySpotMissTime) {
            for (int i = 0; i < SUNNY_SPOT_NUM; ++i) {
                this.mGLSunnySpot[i].onDraw(gl, this.mGLAlpha);
            }

            this.mSunnySpotBeginTime = -1;
            this.mSunnySpotMissTime = -1;
        }

        for (int i = 0; i < SUNNY_LIGHT_NUM; ++i) {
            this.mGLSunnyLight[i].onDrawLight(gl, this.mGLAlpha);
        }

        for (int i = 0; i < SUNNY_HOLO_NUM; ++i) {
            this.mGLSunnyHolo[i].onDraw(gl, this.mGLAlpha);
        }

        if (beginTime - this.mSunnyBeginTime > 800) {
            for (int i = 0; i < SUNNY_RING_NUM; ++i) {
                this.mGLSunnyRing[i].onDraw(gl, this.mGLAlpha);
            }

            for (int i = 0; i < SUNNY_RING_LIGHT; ++i) {
                this.mGLSunnyRingLight[i].onDraw(gl, this.mGLAlpha);
            }
        }

        for (int i = 0; i < STATUSBAR_SHADER_NUM; ++i) {
            this.mGLStatusBarShader[i].onDraw(gl, this.mGLAlpha);
        }

        if (this.isNowNight()) {
            this.fadeOutByStep();
        } else {
            this.fadeInByStep();
        }

        long deltaTime = this.updateDrawTime(45);
        if (beginTime - this.mSunnySpotBeginTime > this.mSunnySpotMissTime) {
            this.updateSunnySpots(deltaTime);
        }

        this.updateSunnyHolo(deltaTime);
        this.updateSunnyLight(deltaTime);
        if (beginTime - this.mSunnyBeginTime > 800) {
            this.updateSunnyRingAndLight(deltaTime);
        }

        int delay = (int) Math.max(0, 45 - (SystemClock.uptimeMillis() - beginTime));
        this.requestRenderDelayed((delay));
    }

    public void onPause() {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (this.mWidth != width || this.mHeight != height) {
            this.initSunnySpots(width, height);
            this.initSunnyHolos(width, height);
            this.initSunnyLight(width, height);
            this.initSunnyRingAndLight(width, height);
            this.initStatusBarShader(width, height);
        }

        this.mWidth = width;
        this.mHeight = height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45f, width * 1f / height, 1f, 5000f);
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

    private void resetSunnyRing(GLSunnyCompent glSunnyRing, int type, float randomFirstX, float randomFirstY,
            boolean isInit) {
        glSunnyRing.setTime(0f);

        if (!this.isNowNight() || !isInit) {
            this.mRingScalefactor = 1f;
        } else {
            this.mRingScalefactor = 0.7f;
        }

        int[] reversedXY = this.changeXYReverse(((int) this.mSunnyRingPositionX), ((int) this.mSunnyRingPositionY));
        if (reversedXY == null) {
            reversedXY = new int[] { 0, 0 };
        }

        glSunnyRing.setPosition(new Vector3f(reversedXY[0], reversedXY[1], 0f));
        glSunnyRing.setScale(3f * this.mRingScalefactor);
        glSunnyRing.setAlpha(0.8f);

        switch (type) {
        case 1:
            glSunnyRing.setRotation(new Vector3f(0f, 0f, RandomUtil.floatRange(0f, 6.283185f)));
            glSunnyRing.setRspeed(new Vector3f(0f, 0f, 0.04f));
            break;
        case 2:
            glSunnyRing.setRspeed(new Vector3f(0f, 0f, -0.04f));
            break;
        case 0:
        case 3:
            break;
        default:
            float v0 = (randomFirstX - reversedXY[0]) * (type - 3);
            float v1 = (randomFirstY - reversedXY[1]) * (type - 3);
            glSunnyRing.setScale(2f * this.mRingScalefactor);
            glSunnyRing.setPosition(new Vector3f(v0 + randomFirstX, v1 + randomFirstY, 0f));
            glSunnyRing.setDspeed(new Vector3f(0.06f * v0, 0.06f * v1, 0f));
            break;
        }
    }

    @Override
    public void updateMovtionEvent(float x, float y, int motionEvent) {
        if (!this.isToggleBarOpen() && motionEvent == 0 && !this.mNewRing) {
            this.mSunnyRingPositionX = x;
            this.mSunnyRingPositionY = y;
            this.mNewRing = true;
        }
    }

    private void updateSunnyHolo(long deltaTime) {
        for (int i = 0; i < SUNNY_HOLO_NUM; ++i) {
            this.mGLSunnyHolo[i].getAlpha();

            Vector3f speed = this.mGLSunnyHolo[i].getRspeed();
            Vector3f rotation = this.mGLSunnyHolo[i].getRotation();

            float time = this.mGLSunnyHolo[i].getTime();
            float timePeriod = this.mGLSunnyHolo[i].getTimePeriod();
            float atime = this.mGLSunnyHolo[i].getATime();
            float atimeperiod = this.mGLSunnyHolo[i].getATimePeriod();
            float scale = this.getBreathing(time, 0.95f - i * 0.1f, 1.05f + i * 0.1f, timePeriod);
            float alpha = this.getBreathing(atime, 0.3f, 1f, atimeperiod);

            time += deltaTime;
            atime += deltaTime;

            if (atime > atimeperiod) {
                this.mGLSunnyHolo[i].setATime(0f);
            } else {
                this.mGLSunnyHolo[i].setATime(atime);
            }

            if (time > timePeriod) {
                this.mGLSunnyHolo[i].setTime(0f);
            } else {
                this.mGLSunnyHolo[i].setTime(time);
            }

            this.mGLSunnyHolo[i].setAlpha(alpha);
            rotation.z += speed.z;
            this.mGLSunnyHolo[i].setRotation(rotation);
            this.mGLSunnyHolo[i].setScale(scale);
        }
    }

    private void updateSunnyLight(long deltaTime) {
        for (int i = 0; i < SUNNY_LIGHT_NUM / 2; ++i) {
            this.mGLSunnyLight[i * 2].getAlpha();
            float aroundr = this.mGLSunnyLight[i * 2].getAroundR();
            Vector3f speed = this.mGLSunnyLight[i * 2].getRspeed();
            Vector3f rotation = this.mGLSunnyLight[i * 2].getRotation();
            float time = this.mGLSunnyLight[i * 2].getTime();
            float timePeriod = this.mGLSunnyLight[i * 2].getTimePeriod();
            float atime = this.mGLSunnyLight[i * 2].getATime();
            float atimePeriod = this.mGLSunnyLight[i * 2].getATimePeriod();
            float aroundr2 = RandomUtil.floatRange(-0.08f + ((i)) * 0.08f, 0f + ((i)) * 0.08f);
            float scale = this.getBreathing(time, 0.9f, 1.35f, timePeriod);
            float scale2 = this.getBreathing(time, 1f, 1.25f, timePeriod);
            float alpha = this.getBreathing(atime, 0f, 1f, atimePeriod);

            time += deltaTime;
            atime += deltaTime;

            if (atime > atimePeriod) {
                this.mGLSunnyLight[i * 2].setATime(0f);
                this.mGLSunnyLight[i * 2 + 1].setATime(0f);
                this.mGLSunnyLight[i * 2].setAroundR(aroundr2);
                this.mGLSunnyLight[i * 2 + 1].setAroundR(aroundr2);
                continue;
            }

            this.mGLSunnyLight[i * 2].setATime(atime);
            this.mGLSunnyLight[i * 2 + 1].setATime(atime);

            if (time <= timePeriod) {
                this.mGLSunnyLight[i * 2].setTime(time);
                this.mGLSunnyLight[i * 2 + 1].setTime(time);
            } else {
                this.mGLSunnyLight[i * 2].setTime(0f);
                this.mGLSunnyLight[i * 2 + 1].setTime(0f);
            }

            this.mGLSunnyLight[i * 2].setAlpha(alpha);
            this.mGLSunnyLight[i * 2 + 1].setAlpha(alpha);
            rotation.z += speed.z;
            this.mGLSunnyLight[i * 2].setRotation(rotation);
            this.mGLSunnyLight[i * 2 + 1].setRotation(rotation);
            this.mGLSunnyLight[i * 2].setAroundR(aroundr - 0.0013f);
            this.mGLSunnyLight[i * 2 + 1].setAroundR(aroundr - 0.001f);
            this.mGLSunnyLight[i * 2].setScale(scale);
            this.mGLSunnyLight[i * 2 + 1].setScale(scale2);
        }
    }

    private void updateSunnyRingAndLight(long deltaTime) {
        GLSunnyCompent sunnyRing;

        float flag;
        float randomFirstX = 0f;
        float randomFirstY = 0f;

        if (this.mNewRing) {
            int[] v15 = this.changeXYReverse((int) this.mSunnyRingPositionX, (int) this.mSunnyRingPositionY);
            randomFirstX = RandomUtil.floatRange(-50f * this.mWidth / 1080f + v15[0], 50f * this.mWidth / 1080f
                    + v15[0]);

            if (RandomUtil.flipCoin()) {
                flag = -1f;
            } else {
                flag = 1f;
            }

            randomFirstY = (float) (flag * Math.sqrt(Math.pow(50, 2) - Math.pow(randomFirstX - v15[0], 2)) + v15[1]);
        }

        float v14 = (this.mGLSunnyRing[0].getTime() + deltaTime) / 1000f;

        for (int i = 0; i < SUNNY_RING_NUM; ++i) {
            sunnyRing = this.mGLSunnyRing[i];

            if (this.mNewRing) {
                this.resetSunnyRing(sunnyRing, i, randomFirstX, randomFirstY, false);
            } else if (sunnyRing.getTime() <= 1000f) {
                sunnyRing.setTime(sunnyRing.getTime() + deltaTime);
                float time = sunnyRing.getTime();
                sunnyRing.setAlpha((1000f - time) / 1000f * 0.8f);

                if (i < 1) {
                    sunnyRing.setScale((time / 1000f * 3f + 3f) * this.mRingScalefactor);
                } else if (i >= 1 && i < 4) {
                    sunnyRing.getRotation().add(new Vector3f(0f, 0f, sunnyRing.getRspeed().z * (1f - v14)));
                }
            } else {
                break;
            }
        }

        for (int i = 0; i < SUNNY_RING_NUM; ++i) {
            sunnyRing = this.mGLSunnyRingLight[i];

            if (this.mNewRing) {
                this.resetSunnyRing(sunnyRing, i + 4, randomFirstX, randomFirstY, false);
            } else if (sunnyRing.getTime() <= 1000f) {
                sunnyRing.setTime(sunnyRing.getTime() + ((deltaTime)));
                sunnyRing.setAlpha((1000f - sunnyRing.getTime()) / 1000f * 0.8f);
                sunnyRing.getPosition().add(
                        new Vector3f(sunnyRing.getDspeed().x * (1f - v14), sunnyRing.getDspeed().y * (1f - v14),
                                sunnyRing.getDspeed().z * (1f - v14)));
            } else {
                break;
            }
        }

        this.mNewRing = false;
    }

    private void updateSunnySpots(long deltaTime) {
        for (int i = 0; i < SUNNY_SPOT_NUM; ++i) {
            float angleA = this.mGLSunnySpot[i].getAngleA();
            float angleStep = this.mGLSunnySpot[i].getAngleAStep();

            if (angleA + angleStep > Math.PI * 2) {
                this.mGLSunnySpot[i].setAngleA(0f);
                this.mGLSunnySpot[i].setAlpha(0f);
                this.mGLSunnySpot[i].setScale(1f);
                this.mSunnySpotBeginTime = SystemClock.uptimeMillis();
                this.mSunnySpotMissTime = RandomUtil.intRange(5000, 9000);
            } else {
                this.mGLSunnySpot[i].setAngleA(angleA + this.getSunnySpotAngleAStep(angleA, angleStep));
                this.mGLSunnySpot[i].setAlpha(this.getSunnySpotAlpha(angleA + angleStep));
                this.mGLSunnySpot[i].setScale(this.getSunnySpotScale(angleA + angleStep));
            }

            this.mGLSunnySpot[i].updatePosition();
        }
    }
}
