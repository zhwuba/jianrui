package com.wb.launcher3.liveweather;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLU;
import android.os.Handler;
import android.os.SystemClock;

import com.dutils.math.RandomUtil;
import com.dutils.math.Vector3f;
import com.wb.launcher3.R;

public class GLDandelionRender extends LiveWeatherGLRender {
    private static final int BASE_SCREEN_WIDTH = 540;
    private static final int DANDELIONSEED_NUM = 25;
    private static final int DANDELIONSEED_TYPES = 6;
    private static final int STATIC_DANDELIONSEED_NUM = 6;
    protected static final int DANDELION_RENDER_DELAY = 35;
    private static final float DANDELION_X_OFFSET_ADJUST_RATIO = 0.4f;
    private static final float DANDELION_X_OFFSET_DECELETATE_RATIO = 0.8f;
    private static final int DELAY_TIME = 6000;
    private static final int DELAY_TIME_ICON = 8000;
    public static final float DELCELERATE_DISTANCE_ON_ICON = 0f;

    private static final float MAX_LEFT_ANGLE_OFFSET = -0.392699f;
    private static final float MAX_RIGHT_ANGLE_OFFSET = 0.392699f;
    private static final float MAX_STATIC_BOTTOM_Z_DISTANCE = 0f;
    private static final float MAX_STATIC_ICON_Z_DISTANCE = 0f;
    private static final float MAX_Z_DISTANCE = 0f;

    private float MIN_RESPONEDED_ACCELERATION;
    private static final float MIN_RESPONEDED_SLIDE_OFFSET = 0f;
    private static float MIN_Z_DISTANCE = 0f;
    private float SCREEN_RATIO;
    private static final int SLIDE_LAST_TIME = 660;

    public static final int SLIDE_SCREEN_NONE_STATE = 0;
    public static final int SLIDE_SCREEN_LEFT_STATE = 1;
    public static final int SLIDE_SCREEN_RIGHT_STATE = 2;

    public static final int INIT_FLYING = 0;
    public static final int STATIC_NEED_ROTATE = 1;
    public static final int STATIC_NOT_NEED_ROTATE = 2;
    public static final int FLYING_AFTER_STATIC = 4;
    public static final int DELCELERATE_ON_ICON = 8;
    public static final int FLYING_WILL_NOT_STATIC_ON_ICON = 16;
    public static final int STATIC_ON_ICON = 32;

    public static final float STATIC_NUM_PER_ICON = 0f;

    private float V_CACHE_DISTANCE;
    private static final float V_MAX_UNIT = 0f;
    private static final float V_MIN_UNIT = 0f;

    public Runnable delayInvokeStaticDandelion;
    public Runnable delayInvokeStaticDandelionIcon;
    Vector3f mActiveDspeed;
    private long mCurrTime;
    public int mDandelionSlideState;
    Vector3f mDspeed;
    private ArrayList<GLDandelionSeed> mGLDandelionSeed;
    public Handler mHandler;
    Vector3f mHuffSpeed;
    private int mIconNum;
    private int mMovingDandelions;
    Vector3f mPosition;
    Vector3f mRotation;
    Vector3f mRspeed;
    private boolean mShakeFlag;
    Vector3f mSlideSpeed;
    private long mSlideTime;
    private int mStaticDandelions;
    private int mStaticDandelionsIcon;
    private TextureInfo[] mTextureInfos;
    private int[] mTextureResources;
    private float mZDistance;

    GLDandelionRender(Context context, LiveWeatherGLView liveWeatherGLView) {
        super(context, liveWeatherGLView);

        this.V_CACHE_DISTANCE = 0f;
        this.MIN_RESPONEDED_ACCELERATION = 0f;
        this.mMovingDandelions = 0;
        this.mStaticDandelions = 0;
        this.mStaticDandelionsIcon = 0;
        this.mIconNum = 0;
        this.mGLDandelionSeed = new ArrayList<GLDandelionSeed>();
        this.SCREEN_RATIO = 0f;
        this.mShakeFlag = false;
        this.mZDistance = 0f;
        this.mDandelionSlideState = SLIDE_SCREEN_NONE_STATE;
        this.mSlideTime = 0;
        this.mCurrTime = 0;
        this.mDspeed = new Vector3f();
        this.mRspeed = new Vector3f();
        this.mPosition = new Vector3f();
        this.mRotation = new Vector3f();
        this.mSlideSpeed = new Vector3f();
        this.mHuffSpeed = new Vector3f();
        this.mActiveDspeed = new Vector3f();
        this.mTextureInfos = new TextureInfo[6];
        this.mTextureResources = new int[] { R.drawable.dandelion_1, R.drawable.dandelion_1s, R.drawable.dandelion_2,
                R.drawable.dandelion_2s, R.drawable.dandelion_3, R.drawable.dandelion_bug };

        this.delayInvokeStaticDandelion = new Runnable() {
            @Override
            public void run() {
                activateStaticDandelion();
                GLDandelionRender.this.mHandler.postDelayed((this), DELAY_TIME);
            }
        };
        this.delayInvokeStaticDandelionIcon = new Runnable() {
            @Override
            public void run() {
                activateStaticDandelionIcon();
                GLDandelionRender.this.mHandler.postDelayed((this), DELAY_TIME_ICON);
            }
        };

        this.mHandler = new Handler();
    }

    private void activateStaticDandelion() {
        if (this.mIconCallBack == null || !this.mIconCallBack.isToggleBarOpen()) {
            for (int i = 0; i < this.mGLDandelionSeed.size(); ++i) {
                if (this.mGLDandelionSeed.get(i).mDandelionState == STATIC_NOT_NEED_ROTATE
                        && RandomUtil.intRange(1, 100) > 80) {
                    this.mGLDandelionSeed.get(i).mDandelionState = FLYING_AFTER_STATIC;
                    this.mActiveDspeed = this.mGLDandelionSeed.get(i).getDspeed();
                    if (this.mActiveDspeed.y < 0f) {
                        this.mActiveDspeed.y = -this.mActiveDspeed.y;
                    }

                    this.mGLDandelionSeed.get(i).setDspeed(this.mActiveDspeed);
                    ++this.mMovingDandelions;
                    --this.mStaticDandelions;
                    return;
                }
            }
        }
    }

    private void activateStaticDandelionIcon() {
        for (int i = 0; i < this.mGLDandelionSeed.size(); ++i) {
            GLDandelionSeed seed = this.mGLDandelionSeed.get(i);

            if (seed != null && seed.mDandelionState == STATIC_ON_ICON) {
                if (RandomUtil.intRange(1, 100) > 60) {
                    seed.mDandelionState = FLYING_WILL_NOT_STATIC_ON_ICON;
                    ++this.mMovingDandelions;
                    --this.mStaticDandelionsIcon;
                }
            }
        }
    }

    public Vector3f calculateSlideDecSpeed(int slideState) {
        int frameTime = 30;
        long baseTime = this.getBaseSlideTime();
        long currentTime = this.getCurrSlideTime();

        if (currentTime == baseTime) {
            currentTime = baseTime + frameTime;
            this.setCurrSlideTime(currentTime);
        }

        this.setCurrSlideTime(frameTime + currentTime);
        float speed = this.getVariableSpeed(5f * this.SCREEN_RATIO, SLIDE_LAST_TIME / frameTime,
                (int) ((currentTime - baseTime) / frameTime), 5f * this.SCREEN_RATIO, 3, 1f * this.SCREEN_RATIO, 20);

        if (speed < 0f) {
            speed = 0f;
        }

        if (slideState == 1) {
            speed = -speed;
        }

        return new Vector3f(speed, 0f, 0f);
    }

    private void changeToInitFlyingState(GLDandelionSeed dandelion, float width, float height) {
        int[] xy = this.changeXY(((int) dandelion.mPosition.x), ((int) dandelion.mPosition.y));
        if (xy != null) {
            if (this.mIconCallBack != null && this.mIconCallBack.reckonPosInIcon(xy[0], xy[1])) {
                return;
            }

            dandelion.mDandelionState = INIT_FLYING;
            dandelion.mWillNotStaticOnIcon = true;
        }
    }

    private boolean changeToStaticState(GLDandelionSeed dandelion, float width, float height) {
        float v0 = (this.mZDistance - dandelion.getPosition().z) * TAN_22_5;
        if (Math.abs(dandelion.getPosition().z) <= 150f * this.SCREEN_RATIO && dandelion.mDandelionState == 0
                && this.mStaticDandelions < STATIC_DANDELIONSEED_NUM
                && dandelion.mPosition.y - dandelion.getHeight() / 2 + v0 > 1f * this.SCREEN_RATIO
                && dandelion.mPosition.y - dandelion.getHeight() / 2 + v0 < 4f * this.SCREEN_RATIO) {
            dandelion.mDandelionState = STATIC_NEED_ROTATE;
            --this.mMovingDandelions;
            ++this.mStaticDandelions;
            if (this.mMovingDandelions < DANDELIONSEED_NUM) {
                this.mGLDandelionSeed.add(this.generateOneSeed(this.mWidth, this.mHeight));
            }

            return true;
        }

        return false;
    }

    private void changeToStaticStateIcon(GLDandelionSeed dandelion, float width, float height) {
        if (Math.abs(dandelion.getPosition().z) <= 135f * this.SCREEN_RATIO) {
            float v2 = this.mHeight / ((this.mZDistance - dandelion.getPosition().z) * TAN_22_5 * 2);
            int[] xy = this.changeXY(((int) (dandelion.getVatualX() * v2)), ((int) (dandelion.getVatualY() * v2)));
            if (xy != null) {
                int[] edge = null;

                if (this.mIconCallBack != null) {
                    edge = this.mIconCallBack.getEdgeForPos(xy[0], xy[1]);
                }

                if (edge != null) {
                    dandelion.mWillNotStaticOnIcon = false;
                    if (((edge[0])) >= dandelion.getWidth() / 2
                            && ((edge[2])) >= dandelion.getWidth() / 2
                            && ((edge[1])) >= dandelion.getHeight() / 2
                            && ((edge[3])) >= dandelion.getHeight() / 2) {
                        dandelion.mDandelionState = DELCELERATE_ON_ICON;
                        dandelion.setDelcelerateStartPointAndTime(new Vector3f(dandelion.getPosition().x, dandelion
                                .getPosition().y, dandelion.getPosition().z));
                        dandelion.generateAccelerateSpeedX();
                    }
                } else {
                    dandelion.mWillNotStaticOnIcon = true;
                }
            }
        }
    }

    private boolean checkFlyingOutScreen(GLMesh mesh, float screenWidth, float screenHeight) {
        float width = mesh.getWidth();
        float height = mesh.getHeight();
        this.mPosition.set(mesh.getPosition());

        return (this.mPosition.x - width > screenWidth / 2f) || (width + this.mPosition.x < 2f * -screenWidth)
                || (this.mPosition.y - height > screenHeight / 2f) || (height + this.mPosition.y < -screenHeight / 2f);
    }

    private boolean checkFlyingOutScreen(GLMesh mesh, float screenWidth, float screenHeight, float zDistance) {
        float width = mesh.getWidth();
        float height = mesh.getHeight();
        this.mPosition.set(mesh.getPosition());
        float f3 = TAN_22_5 * (2f * (zDistance - this.mPosition.z));
        float f4 = f3 * (screenWidth / screenHeight);

        return (this.mPosition.x - width > f4) || (width + this.mPosition.x < 1.5f * -f4)
                || (this.mPosition.y - height > f3 / 2.0f) || (height + this.mPosition.y < -f3 / 2.0f);
    }

    private void clearDandelionList() {
        this.mGLDandelionSeed.clear();
    }

    private GLDandelionSeed generateOneSeed(int width, int height) {
        GLDandelionSeed seed = new GLDandelionSeed();
        this.resetDandelionSeed(seed, width, height);
        ++this.mMovingDandelions;
        return seed;
    }

    public long getBaseSlideTime() {
        return this.mSlideTime;
    }

    public long getCurrSlideTime() {
        return this.mCurrTime;
    }

    public Vector3f getLRSwingSpeed(GLDandelionSeed dandelion, float delta) {
        Vector3f speed = new Vector3f();
        Vector3f temp = new Vector3f();
        Vector3f rotation = new Vector3f();

        speed.set(dandelion.getRspeed());
        speed.scale(delta);
        rotation.set(dandelion.getRotation());
        rotation.add(speed);

        if (rotation.z > MAX_RIGHT_ANGLE_OFFSET || rotation.z < MAX_LEFT_ANGLE_OFFSET) {
            speed.z = (-speed.z);
            temp.set(dandelion.getRspeed());
            temp.z = (-temp.z);
            dandelion.setRspeed(temp);
        }

        return speed;
    }

    @Override
    public float getOffset() {
        synchronized (this.mLock) {
            if (this.mOffset < 1f && this.mOffset > -1f) {
                this.mOffset = 0f;
                this.mIsUpdatedOffset = true;

                return 0;
            }

            this.mIsUpdatedOffset = true;
            this.mOffset = (DANDELION_X_OFFSET_DECELETATE_RATIO * this.mOffset);

            return this.mOffset;
        }
    }

    public Vector3f getSlideSpeed() {
        return this.mSlideSpeed;
    }

    public float getVariableSpeed(float initSpeed, int totalFrame, int currFrame, float accDValue, int accFrames,
            float decDvalue, int decFrames) {
        if (currFrame > totalFrame) {
            return 0;
        }

        if (currFrame > accFrames || accFrames <= 0) {
            return accFrames * accDValue + initSpeed - (currFrame - accFrames) * decDvalue;
        }

        return initSpeed + currFrame * accDValue;
    }

    private void initDandelionseeds(int width, int height) {
        this.clearDandelionList();

        for (int i = 0; i < DANDELIONSEED_NUM; ++i) {
            this.mGLDandelionSeed.add(this.generateOneSeed(width, height));
        }
    }

    private void initTexture(GL10 gl) {
        for (int i = 0; i < DANDELIONSEED_TYPES; ++i) {
            this.mTextureInfos[i] = this.loadTexture2(gl, this.mTextureResources[i]);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        long uptime = SystemClock.uptimeMillis();

        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        float offset = this.getOffset();
        int acceleration = this.getAcceleration();

        if (offset <= -15f || offset >= 15f) {
            offset = 0;
        }

        this.mIconNum = 0;
        if (this.mIconCallBack != null) {
            this.mIconNum = this.mIconCallBack.getIconNum();
        }

        int v26;
        if (this.mIconCallBack == null) {
            v26 = 0;
        } else {
            if (!this.mIconCallBack.isSteadyState() || this.mIconCallBack.isToggleBarOpen()
                    || this.mIconCallBack.isInScrollState() || this.mIconCallBack.isIconOnDrag() || this.mIconNum == 0) {
                v26 = 0;
            } else {
                v26 = 1;
            }
        }

        boolean isToggleBarOpen = false;
        if (this.mIconCallBack != null) {
            isToggleBarOpen = this.mIconCallBack.isToggleBarOpen();
        }

        if (this.mShakeFlag || ((Math.abs(acceleration))) > this.MIN_RESPONEDED_ACCELERATION
                || Math.abs(offset) > 1f) {
            this.slideScreenActivateStaticDandelion();
        }

        if (this.mShakeFlag || v26 == 0) {
            this.slideScreenActivateStaticDandelionIcon();
        }

        this.setSlideState(acceleration, SystemClock.uptimeMillis());
        Vector3f slideSpeed = this.getSlideSpeed();

        for (int i = 0; i < this.mGLDandelionSeed.size(); ++i) {
            GLDandelionSeed seed = this.mGLDandelionSeed.get(i);

            if (seed.mDandelionState != STATIC_NEED_ROTATE && seed.mDandelionState != DELCELERATE_ON_ICON
                    && seed.mDandelionState != STATIC_NOT_NEED_ROTATE && seed.mDandelionState != STATIC_ON_ICON) {
                if (this.mDandelionSlideState != SLIDE_SCREEN_NONE_STATE) {
                    seed.setTransition(slideSpeed.x, slideSpeed.y, slideSpeed.z);
                } else {
                    seed.setTransition(offset, 0f, 0f);
                }
            }

            if (this.checkInViewport(seed, this.mWidth / 2f, this.mHeight / 2f, this.mZDistance)) {
                switch (seed.mDandelionState) {
                case INIT_FLYING:
                    seed.onDraw(gl, this.mGLAlpha);
                    if (!isToggleBarOpen
                            && !(this.mDandelionSlideState != SLIDE_SCREEN_NONE_STATE
                                    || this.changeToStaticState(seed, this.mWidth / 2f, this.mHeight / 2f) || v26 == 0 || ((this.mStaticDandelionsIcon)) >= this.mIconNum * 1.5f)) {
                        this.changeToStaticStateIcon(seed, this.mWidth / 2f, this.mHeight / 2f);
                    }
                    break;
                case STATIC_NEED_ROTATE:
                case STATIC_NOT_NEED_ROTATE:
                    if (!isToggleBarOpen) {
                        seed.onRotateDraw(gl, 0f, seed.getHeight() / 2f, 0f, this.mGLAlpha);
                    }
                    break;
                case FLYING_AFTER_STATIC:
                    float v32 = 0f / this.V_CACHE_DISTANCE;
                    float v21 = seed.getPosition().y + this.mHeight / 2f;
                    float v9 = 0f - v21 * v32;
                    float v10 = seed.getHeight() / 2f - v21 * (seed.getHeight() / 2f / this.V_CACHE_DISTANCE);
                    if (v9 < -0.000001f) {
                        v9 = 0f;
                    }

                    if (v10 < -0.000001f) {
                        v10 = 0f;
                    }

                    seed.onRotateDraw(gl, v9, v10, 0f, this.mGLAlpha);
                    break;
                case DELCELERATE_ON_ICON:
                    if (v26 == 0) {
                        seed.mDandelionState = FLYING_WILL_NOT_STATIC_ON_ICON;
                    } else {
                        seed.onDraw(gl, this.mGLAlpha);
                    }
                    break;
                case FLYING_WILL_NOT_STATIC_ON_ICON:
                    seed.onDraw(gl, this.mGLAlpha);
                    if (v26 != 0) {
                        this.changeToInitFlyingState(seed, this.mWidth / 2f, this.mHeight / 2f);
                    }
                    break;

                case STATIC_ON_ICON:
                    seed.onDraw(gl, this.mGLAlpha);
                    break;
                }
            }
        }

        this.setAcceleration(0);
        this.updateDandelionseeds(this.updateDrawTime(DANDELION_RENDER_DELAY));
        this.mShakeFlag = false;

        int frameDelay = DANDELION_RENDER_DELAY - (int) (SystemClock.uptimeMillis() - uptime);
        this.requestRenderDelayed(frameDelay <= 0 ? 0 : frameDelay);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (this.mWidth != width || this.mHeight != height) {
            this.SCREEN_RATIO = width * 1f / BASE_SCREEN_WIDTH;
            GLDandelionRender.MIN_Z_DISTANCE = 1000f * this.SCREEN_RATIO;
            this.MIN_RESPONEDED_ACCELERATION = 500f * this.SCREEN_RATIO;
            this.V_CACHE_DISTANCE = height / 6f;
            this.initDandelionseeds(width, height);
            this.mHandler.removeCallbacks(this.delayInvokeStaticDandelion);
            this.mHandler.removeCallbacks(this.delayInvokeStaticDandelionIcon);
            this.mHandler.postDelayed(this.delayInvokeStaticDandelion, DELAY_TIME);
            this.mHandler.postDelayed(this.delayInvokeStaticDandelionIcon, DELAY_TIME_ICON);
        }

        this.mWidth = width;
        this.mHeight = height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45f, (float) width / (float) height, 1f, 5000f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        this.mZDistance = height * ATAN2_45_DEGREE;
        GLU.gluLookAt(gl, 0f, 0f, this.mZDistance, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        this.initTexture(gl);
    }

    public void registerHandler() {
        this.mHandler.postDelayed(this.delayInvokeStaticDandelion, DELAY_TIME);
        this.mHandler.postDelayed(this.delayInvokeStaticDandelionIcon, DELAY_TIME_ICON);
    }

    private void resetDandelionSeed(GLDandelionSeed dandelion, int width, int height) {
        this.mPosition.x = RandomUtil.floatRange(-width * 1.5f, -width * 0.6f);
        this.mPosition.y = RandomUtil.floatRange(-height * 0.45f, height * 0.5f);
        this.mPosition.z = RandomUtil.floatRange(-this.SCREEN_RATIO * 400f, this.SCREEN_RATIO * 400f);
        this.mDspeed.x = RandomUtil.floatRange(width * 0.0012f, width * 0.002f);
        this.mDspeed.y = RandomUtil.floatRange(-width * 0.0016f, width * 0.0025f);

        if (this.mPosition.z >= -this.SCREEN_RATIO * 100f || !RandomUtil.flipCoin()) {
            this.mDspeed.z = RandomUtil.floatRange(-width * 0.0018f, width * 0.0023f);
        } else {
            this.mDspeed.z = RandomUtil.floatRange(width * 0.001f, width * 0.0022f);
        }

        this.mRspeed.x = 0f;
        this.mRspeed.y = 0f;
        this.mRspeed.z = RandomUtil.floatRange(-0.007f, 0.007f);
        if (this.mRspeed.z > -0.003f && this.mRspeed.z < 0.003f) {
            this.mRspeed.z = 0.003f;
        }

        this.mRotation.x = 0f;
        this.mRotation.y = 0f;
        this.mRotation.z = 0f;

        float v2 = this.mPosition.z;
        if (v2 > 0) {
            v2 = 0f;
        }

        dandelion.setAlpha(Math.abs(1f - Math.abs(v2 / GLDandelionRender.MIN_Z_DISTANCE)));
        dandelion.setDspeed(this.mDspeed);
        dandelion.setRspeed(this.mRspeed);
        dandelion.setPosition(this.mPosition);
        dandelion.setRotation(this.mRotation);

        int textureIndex;

        if (this.mPosition.z <= 150f * this.SCREEN_RATIO || this.mDspeed.z >= 0f) {
            if (this.mDspeed.z >= 0.00001f) {
                if (this.mPosition.z >= -this.SCREEN_RATIO * 300f || this.mDspeed.z <= ((width)) * 0.0019f) {
                    textureIndex = RandomUtil.intRange(2, 5);
                } else {
                    textureIndex = RandomUtil.intRange(4, 6);
                }
            } else {
                textureIndex = RandomUtil.intRange(0, 2);
            }
        } else {
            textureIndex = RandomUtil.intRange(2, 4);
        }

        if (textureIndex != 5) {
            dandelion.setWidth((this.mTextureInfos[textureIndex].mWidth));
            dandelion.setHeight((this.mTextureInfos[textureIndex].mHeight));
        } else {
            dandelion.setWidth(((this.mTextureInfos[textureIndex].mWidth)) * this.SCREEN_RATIO);
            dandelion.setHeight(((this.mTextureInfos[textureIndex].mHeight)) * this.SCREEN_RATIO);
        }

        dandelion.buildMesh();

        if (textureIndex == 0) {
            dandelion.setMaxAngle(1.30899f);
            dandelion.setMinAngle(-1.29154f);
        } else if (textureIndex == 1) {
            dandelion.setMaxAngle(1.37881f);
            dandelion.setMinAngle(-1.41371f);
        } else if (textureIndex == 2) {
            dandelion.setMaxAngle(1.23918f);
            dandelion.setMinAngle(-1.29154f);
        } else if (textureIndex == 3) {
            dandelion.setMaxAngle(1.22173f);
            dandelion.setMinAngle(-1.25663f);
        } else if (textureIndex == 4) {
            dandelion.setMaxAngle(1.309f);
            dandelion.setMinAngle(-1.18682f);
        } else if (textureIndex == 5) {
            dandelion.setMaxAngle(1.22173f);
            dandelion.setMinAngle(-1.23918f);
        }

        dandelion.setTextureId(this.mTextureInfos[textureIndex].mId);
        dandelion.setAccelerateDistanceX(dandelion.getWidth() * 2f);
        dandelion.mDandelionState = INIT_FLYING;
    }

    public void resetSlideState() {
        this.mDandelionSlideState = SLIDE_SCREEN_NONE_STATE;
        this.setBaseSlideTime(0);
        this.setCurrSlideTime(0);
        this.setSlideSpeed(Vector3f.ZERO);
    }

    public void setBaseSlideTime(long time) {
        this.mSlideTime = time;
    }

    public void setCurrSlideTime(long time) {
        this.mCurrTime = time;
    }

    public void setDandelionAlpha(GLDandelionSeed dandelion) {
        float positionZ = dandelion.getPosition().z;

        if (positionZ > 0f) {
            positionZ = 0f;
        }

        float alpha = Math.abs(1f - Math.abs(positionZ / GLDandelionRender.MIN_Z_DISTANCE));

        if (alpha > 1f) {
            alpha = 1f;
        }

        dandelion.setAlpha(alpha);
    }

    @Override
    public void setOffset(float offset) {
        super.setOffset(offset * DANDELION_X_OFFSET_ADJUST_RATIO);
    }

    public void setSlideSpeed(Vector3f speed) {
        this.mSlideSpeed = speed;
    }

    public void setSlideState(int acceleration, long currTime) {
        if (this.getBaseSlideTime() <= 0) {
            if (acceleration > this.MIN_RESPONEDED_ACCELERATION) {
                this.mDandelionSlideState = SLIDE_SCREEN_RIGHT_STATE;
                this.setBaseSlideTime(currTime);
                this.setCurrSlideTime(currTime);
            } else if (acceleration < -this.MIN_RESPONEDED_ACCELERATION) {
                this.mDandelionSlideState = SLIDE_SCREEN_LEFT_STATE;
                this.setBaseSlideTime(currTime);
                this.setCurrSlideTime(currTime);
            }
        }
    }

    public void shake() {
        if (this.mIconCallBack == null || !this.mIconCallBack.isToggleBarOpen()) {
            this.mShakeFlag = true;
        }
    }

    private void slideScreenActivateStaticDandelion() {
        if (this.mIconCallBack == null || !this.mIconCallBack.isToggleBarOpen()) {
            for (int i = 0; i < this.mGLDandelionSeed.size(); ++i) {
                slideScreenActivateStaticDandelion(this.mGLDandelionSeed.get(i));
            }
        }
    }

    private void slideScreenActivateStaticDandelion(GLDandelionSeed dandelion) {
        if (this.mIconCallBack == null || !this.mIconCallBack.isToggleBarOpen()) {
            if (dandelion.mDandelionState != STATIC_NEED_ROTATE && dandelion.mDandelionState != STATIC_NOT_NEED_ROTATE) {
                return;
            }

            dandelion.mDandelionState = FLYING_AFTER_STATIC;
            ++this.mMovingDandelions;
            --this.mStaticDandelions;
            Vector3f speed = dandelion.getDspeed();
            if (speed.y < 0f) {
                speed.y = -speed.y;
            }

            dandelion.setDspeed(speed);
        }
    }

    private void slideScreenActivateStaticDandelionIcon() {
        for (int i = 0; i < this.mGLDandelionSeed.size(); i++) {
            slideScreenActivateStaticDandelionIcon(this.mGLDandelionSeed.get(i));
        }
    }

    private void slideScreenActivateStaticDandelionIcon(GLDandelionSeed dandelion) {
        if (dandelion.mDandelionState == DELCELERATE_ON_ICON || dandelion.mDandelionState == STATIC_ON_ICON) {
            if (dandelion.mDandelionState == STATIC_ON_ICON) {
                ++this.mMovingDandelions;
                --this.mStaticDandelionsIcon;
            }

            dandelion.mDandelionState = FLYING_WILL_NOT_STATIC_ON_ICON;
        }
    }

    public void unregisterHandler() {
        this.mHandler.removeCallbacks(this.delayInvokeStaticDandelion);
        this.mHandler.removeCallbacks(this.delayInvokeStaticDandelionIcon);
    }

    private void updateDandelionseeds(long deltaTime) {
        float delta = deltaTime / RENDER_DURATION;
        long baseSlideTime = this.getBaseSlideTime();
        long currSlideTime = this.getCurrSlideTime();

        if (this.mDandelionSlideState != SLIDE_SCREEN_NONE_STATE) {
            if (currSlideTime - baseSlideTime > SLIDE_LAST_TIME) {
                this.resetSlideState();
            } else {
                this.setSlideSpeed(this.calculateSlideDecSpeed(this.mDandelionSlideState));
            }
        }

        for (int i = 0; i < this.mGLDandelionSeed.size(); ++i) {
            GLDandelionSeed seed = this.mGLDandelionSeed.get(i);

            if (this.checkFlyingOutScreen(seed, this.mWidth, this.mHeight, this.mZDistance)) {
                if (this.mMovingDandelions <= DANDELIONSEED_NUM) {
                    this.resetDandelionSeed(seed, this.mWidth, this.mHeight);
                } else {
                    this.mGLDandelionSeed.remove(i);
                    --this.mMovingDandelions;
                    --i;
                    continue;
                }
            }

            switch (seed.mDandelionState) {
            case STATIC_NEED_ROTATE:
                this.mRotation.set(seed.getRotation());
                if (this.mRotation.z >= 0f && this.mRotation.z < seed.getMaxAngle()) {
                    this.mRspeed.x = 0f;
                    this.mRspeed.y = 0f;
                    this.mRspeed.z = RandomUtil.floatRange(0.02f, 0.07f);
                    seed.getRotation().add(this.mRspeed);
                    break;
                }

                if (this.mRotation.z >= seed.getMinAngle() && this.mRotation.z < 0f) {
                    this.mRspeed.x = 0f;
                    this.mRspeed.y = 0f;
                    this.mRspeed.z = RandomUtil.floatRange(-0.07f, -0.02f);
                    seed.getRotation().add(this.mRspeed);
                    break;
                }

                this.mGLDandelionSeed.get(i).mDandelionState = STATIC_NOT_NEED_ROTATE;
                break;
            case FLYING_AFTER_STATIC:
                this.mDspeed.set(seed.getDspeed());
                this.mDspeed.scale(delta);
                seed.getPosition().add(this.mDspeed);
                this.setDandelionAlpha(seed);
                this.mRotation.set(seed.getRotation());
                if (this.mRotation.z >= MAX_RIGHT_ANGLE_OFFSET) {
                    this.mRspeed.x = 0f;
                    this.mRspeed.y = 0f;
                    this.mRspeed.z = -0.03f;
                    seed.getRotation().add(this.mRspeed);
                } else if (this.mRotation.z <= MAX_LEFT_ANGLE_OFFSET) {
                    this.mRspeed.x = 0f;
                    this.mRspeed.y = 0f;
                    this.mRspeed.z = 0.03f;
                    seed.getRotation().add(this.mRspeed);
                }

                if (seed.getPosition().y + this.mHeight / 2f <= this.V_CACHE_DISTANCE) {
                    break;
                }

                seed.mDandelionState = INIT_FLYING;
                break;
            case DELCELERATE_ON_ICON:
                Vector3f position = seed.getPosition();
                position.set(seed.getDelcelerateStartPoint());

                float delcelerateX = seed.getDelcelerateDistanceX(deltaTime);
                position.x += delcelerateX;
                position.y += seed.getDspeed().y * delcelerateX / seed.getDspeed().x;
                position.z += 0f;

                this.mRspeed = this.getLRSwingSpeed(seed, delta);
                seed.getRotation().add(this.mRspeed);

                if (delcelerateX >= seed.getWidth() * 2f) {
                    seed.mDandelionState = STATIC_ON_ICON;
                    ++this.mStaticDandelionsIcon;
                    --this.mMovingDandelions;

                    if (this.mMovingDandelions < DANDELIONSEED_NUM) {
                        this.mGLDandelionSeed.add(this.generateOneSeed(this.mWidth, this.mHeight));
                    }
                }
                break;
            case INIT_FLYING:
            case FLYING_WILL_NOT_STATIC_ON_ICON:
                this.mDspeed.set(seed.getDspeed());
                this.mDspeed.scale(delta);
                if (this.mDandelionSlideState == SLIDE_SCREEN_LEFT_STATE) {
                    if (seed.getRotation().z - 0.03f > MAX_LEFT_ANGLE_OFFSET) {
                        this.mRspeed.x = 0f;
                        this.mRspeed.y = 0f;
                        this.mRspeed.z = -0.03f;
                        seed.getRotation().add(this.mRspeed);
                    }
                } else if (this.mDandelionSlideState != STATIC_NOT_NEED_ROTATE) {
                    this.mRspeed = this.getLRSwingSpeed(seed, delta);
                    seed.getRotation().add(this.mRspeed);
                } else if (seed.getRotation().z + 0.03f < MAX_RIGHT_ANGLE_OFFSET) {
                    this.mRspeed.x = 0f;
                    this.mRspeed.y = 0f;
                    this.mRspeed.z = 0.03f;
                    seed.getRotation().add(this.mRspeed);
                }

                seed.getPosition().add(this.mDspeed);
                this.setDandelionAlpha(seed);
                break;
            }
        }
    }
}
