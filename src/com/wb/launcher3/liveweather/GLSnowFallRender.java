package com.wb.launcher3.liveweather;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLU;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.dutils.math.RandomUtil;
import com.dutils.math.Vector3f;
import com.wb.launcher3.weatherIcon.SnowIconDrawInfo;
import com.wb.launcher3.weatherIcon.WeatherIconDrawInfo;
import com.wb.launcher3.R;

public class GLSnowFallRender extends LiveWeatherGLRender {
    private static final int BLUR_SNOW_DELAY_TIME = 5000;
    private static final int DELAY_TIME_ICON = 5000;
    public static final float DELCELERATE_DISTANCE_ON_ICON = 0f;
    private static final float DOCKBAR_HEIGHT_SCALE = 0.88f;

    private static final float LEFT_ICE_WIDTH_DEVIDE_SCREEN_WIDTH = 0f;
    private int MIN_RESPONEDED_ACCELERATION;
    private static final float MIN_RESPONEDED_SLIDE_OFFSET = 0f;

    public static final int FLYING_WILL_NOT_STATIC_ON_ICON = 3;
    public static final int STATIC_ON_ICON = 2;
    public static final int DELCELERATE_ON_ICON = 1;
    public static final int NORMAL_FLYING = 0;

    private static final float SINGLE_ICON_CAN_STATIC_SNOWFLAKE_NUMBER = 0f;
    private static final int SLIDE_LAST_TIME = 660;

    public static final int SLIDE_SCREEN_LEFT_STATE = 1;
    public static final int SLIDE_SCREEN_NONE_STATE = 0;
    public static final int SLIDE_SCREEN_RIGHT_STATE = 2;

    private static final int SNOWFLAKE_NUM = 100;
    private static final int SNOWFLAKE_TYPES = 7;
    private static final int SNOW_BLURRED_NUM = 3;
    private static final int SNOW_BLURRED_TYPE = 1;
    private static final float SNOW_BOTTOM_PREPARE_TIME = 0f;
    private static final int SNOW_BOTTOM_SHAKE_NUM = 200;
    private static final int SNOW_BOTTOM_SHAKE_TYPES = 9;
    private static final float SNOW_BOTTOM_START_SHOW_TIME = 0f;
    private static final int SNOW_BOTTOM_TYPES = 3;
    private static final int SNOW_DROP_NUM_MAX = 100;
    private static final int SNOW_DROP_NUM_PER_ICON = 9;
    private static final int SNOW_DROP_TYPES = 2;
    private static final float SNOW_ICE_ALPHA = 0.8f;
    private static final float SNOW_ICE_ALPHA_GRADUAL_CHANGE_TIME = 0f;
    private static final int SNOW_ICE_BOTTOM = 0;
    private static final int SNOW_ICE_NUM = 1;
    private static final int SNOW_ICE_TYPE = 1;
    private static final float SNOW_ICE_PREPARE_TIME = 0f;
    private static final float SNOW_X_OFFSET_ADJUST_RATIO = 0.3f;
    private static final float SNOW_X_OFFSET_DECELETATE_RATIO = 0.96f;
    private static final float TOP_ICE_HEIGHT_DEVIDE_SCREEN_HEIGHT = 0f;
    public Runnable delayInvokeStaticSnowflakeIcon;
    private long mBlurSnowInitTime;
    private long mCurrTime;
    private int mCurrentPageIcons;
    private int mDelcelerateAndStaticSnows;
    private int mDockBarIconNum;
    private boolean mDrawIce;
    private boolean mDrawSnowBottom;
    private int mDrawSnowBottomIndex;
    Vector3f mDspeed;
    private int mFlyingSnows;
    private GLSnowFlake[] mGLSnowBlurred;
    private GLSnowBottom[] mGLSnowBottom;
    private GLSnowBottomShake[] mGLSnowBottomShake;
    private GLSnowDrop[] mGLSnowDrop;
    ArrayList<GLSnowDrop> mGLSnowDropDrawing;
    ArrayList<GLSnowDrop> mGLSnowDropSparing;
    private GLSnowIce[] mGLSnowIces;
    private ArrayList<GLSnowFlake> mGLSnowFlakes;
    private boolean mGenerateNewSnowDrops;
    private Handler mHandler;
    private float[] mInitVelocity;
    private boolean mIsShaking;
    private boolean mIsSnowBottomShaking;
    Vector3f mPosition;
    Vector3f mRotation;
    Vector3f mRspeed;
    private Vector3f mSlideSpeed;
    private long mSlideTime;
    private int[] mSnowBlurredTextureAsserts;
    private TextureInfo[] mSnowBlurredTextureInfos;
    private int[] mSnowBottomShakeTextureAsserts;
    private TextureInfo[] mSnowBottomShakeTextureInfos;
    private long mSnowBottomStartTime;
    private int[] mSnowBottomTextureAsserts;
    private TextureInfo[] mSnowBottomTextureInfos;
    private int[] mSnowDropTextureAsserts;
    private TextureInfo[] mSnowDropTextureInfos;
    private int[] mSnowIceTextureAsserts;
    private TextureInfo[] mSnowIceTextureInfos;
    public int mSnowflakeSlideState;
    private int[] mSnowflakeTextureAsserts;
    private TextureInfo[] mSnowflakeTextureInfos;
    private long mStartTime;
    private int mStaticIconSnows;

    GLSnowFallRender(Context context, LiveWeatherGLView liveWeatherGLView) {
        super(context, liveWeatherGLView);

        this.mFlyingSnows = 0;
        this.mStaticIconSnows = 0;
        this.mDelcelerateAndStaticSnows = 0;
        this.mCurrentPageIcons = 0;
        this.MIN_RESPONEDED_ACCELERATION = 0;
        this.mGLSnowFlakes = new ArrayList<GLSnowFlake>();
        this.mSnowflakeTextureInfos = new TextureInfo[SNOWFLAKE_TYPES];
        this.mSnowIceTextureInfos = new TextureInfo[SNOW_ICE_TYPE];
        this.mSnowDropTextureInfos = new TextureInfo[SNOW_DROP_TYPES];
        this.mSnowBottomTextureInfos = new TextureInfo[SNOW_BOTTOM_TYPES];
        this.mSnowBottomShakeTextureInfos = new TextureInfo[SNOW_BOTTOM_SHAKE_TYPES];
        this.mSnowBlurredTextureInfos = new TextureInfo[SNOW_BLURRED_TYPE];
        this.mStartTime = 0;
        this.mDrawIce = false;
        this.mGenerateNewSnowDrops = false;
        this.mIsShaking = false;
        this.mDrawSnowBottom = false;
        this.mSnowBottomStartTime = 0;
        this.mDrawSnowBottomIndex = 0;
        this.mIsSnowBottomShaking = false;
        this.mInitVelocity = new float[SNOW_BOTTOM_SHAKE_NUM];
        this.mDockBarIconNum = 0;
        this.mGLSnowDropDrawing = new ArrayList<GLSnowDrop>();
        this.mGLSnowDropSparing = new ArrayList<GLSnowDrop>();
        this.mBlurSnowInitTime = 0;
        this.mDspeed = new Vector3f();
        this.mRspeed = new Vector3f();
        this.mPosition = new Vector3f();
        this.mRotation = new Vector3f();
        this.mSlideTime = 0;
        this.mCurrTime = 0;
        this.mSlideSpeed = new Vector3f();
        this.mSnowflakeSlideState = 0;
        this.mSnowflakeTextureAsserts = new int[] { R.drawable.snowflake_1, R.drawable.snowflake_2,
                R.drawable.snowflake_3, R.drawable.snowflake_4, R.drawable.snowflake_5, R.drawable.snowflake_6,
                R.drawable.snowflake_7 };
        this.mSnowIceTextureAsserts = new int[] { R.drawable.snowice_bottom };
        this.mSnowDropTextureAsserts = new int[] { R.drawable.snowdrop_1, R.drawable.snowdrop_2 };
        this.mSnowBottomTextureAsserts = new int[] { R.drawable.snowbottom_1, R.drawable.snowbottom_2,
                R.drawable.snowbottom_3 };
        this.mSnowBottomShakeTextureAsserts = new int[] { R.drawable.snowbottomshake1, R.drawable.snowbottomshake2,
                R.drawable.snowbottomshake3, R.drawable.snowbottomshake4, R.drawable.snowbottomshake5,
                R.drawable.snowbottomshake6, R.drawable.snowbottomshake7, R.drawable.snowbottomshake8,
                R.drawable.snowbottomshake9 };
        this.mSnowBlurredTextureAsserts = new int[] { R.drawable.snowblur_1 };

        this.delayInvokeStaticSnowflakeIcon = new Runnable() {
            @Override
            public void run() {
                activateStaticSnowflakeIcon();
                mHandler.postDelayed((this), BLUR_SNOW_DELAY_TIME);
            }
        };

        this.mGLSnowIces = new GLSnowIce[SNOW_ICE_NUM];
        for (int i = 0; i < SNOW_ICE_NUM; ++i) {
            this.mGLSnowIces[i] = new GLSnowIce();
        }

        this.mGLSnowDrop = new GLSnowDrop[SNOW_DROP_NUM_MAX];
        for (int i = 0; i < SNOW_DROP_NUM_MAX; ++i) {
            this.mGLSnowDrop[i] = new GLSnowDrop();
            this.mGLSnowDropSparing.add(this.mGLSnowDrop[i]);
        }

        this.mGLSnowBottom = new GLSnowBottom[SNOW_BOTTOM_TYPES];
        for (int i = 0; i < SNOW_BOTTOM_TYPES; ++i) {
            this.mGLSnowBottom[i] = new GLSnowBottom();
        }

        this.mGLSnowBottomShake = new GLSnowBottomShake[SNOW_BOTTOM_SHAKE_NUM];
        for (int i = 0; i < SNOW_BOTTOM_SHAKE_NUM; ++i) {
            this.mGLSnowBottomShake[i] = new GLSnowBottomShake();
        }

        this.mGLSnowBlurred = new GLSnowFlake[SNOW_BLURRED_NUM];
        for (int i = 0; i < SNOW_BLURRED_NUM; ++i) {
            this.mGLSnowBlurred[i] = new GLSnowFlake();
        }

        this.mHandler = new Handler();
    }

    private void activateAllStaticSnowflake() {
        for (int i = 0; i < this.mGLSnowFlakes.size(); ++i) {
            GLSnowFlake snowFlake = this.mGLSnowFlakes.get(i);
            if (snowFlake.mSnowState == STATIC_ON_ICON || snowFlake.mSnowState == DELCELERATE_ON_ICON) {
                if (snowFlake.mSnowState == STATIC_ON_ICON) {
                    --this.mStaticIconSnows;
                    ++this.mFlyingSnows;
                }

                --this.mDelcelerateAndStaticSnows;
                snowFlake.mSnowState = FLYING_WILL_NOT_STATIC_ON_ICON;
            }
        }
    }

    private void activateStaticSnowflakeIcon() {
        for (int i = 0; i < this.mGLSnowFlakes.size(); ++i) {
            GLSnowFlake snowFlake = this.mGLSnowFlakes.get(i);
            if (snowFlake != null && snowFlake.mSnowState == STATIC_ON_ICON && RandomUtil.intRange(1, 100) > 80) {
                snowFlake.mSnowState = FLYING_WILL_NOT_STATIC_ON_ICON;
                ++this.mFlyingSnows;
                --this.mStaticIconSnows;
                --this.mDelcelerateAndStaticSnows;
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

        float speed = this.getVariableSpeed(5f * this.mWidth / 540f, 660 / frameTime,
                (int) ((currentTime - baseTime) / frameTime), 5f * this.mWidth / 540f, 3, 1f * this.mWidth / 540f, 20);

        if (speed < 0f) {
            speed = 0f;
        }

        if (slideState == SLIDE_SCREEN_LEFT_STATE) {
            speed = -speed;
        }

        return new Vector3f(speed, 0, 0);
    }

    private void changeToInitFlyingState(GLSnowFlake snowflake, float width, float height) {
        int[] xy = this.changeXY((int) snowflake.getPosition().x, (int) snowflake.getPosition().y);
        if (xy != null && !this.mIconCallBack.reckonPosInIcon(xy[0], xy[1])) {
            snowflake.mSnowState = 0;
        }
    }

    private boolean changeToStaticIconState(GLSnowFlake snowflake, float width, float height, int deltaTime) {
        boolean result;
        if (RandomUtil.intRange(0, 100) > 15) {
            result = false;
        } else {
            int[] xy = this.changeXY(((int) snowflake.getVatualX()), ((int) snowflake.getVatualY()));
            if (xy == null) {
                result = false;
            } else {
                int[] edge = this.mIconCallBack.getEdgeForPos(xy[0], xy[1]);

                if (edge != null) {
                    if (edge[0] >= snowflake.getWidth() && edge[1] >= snowflake.getWidth()
                            && edge[2] >= snowflake.getHeight() && edge[3] >= snowflake.getHeight()) {
                        if (RandomUtil.intRange(0, 100) > 15) {
                            snowflake.mSnowState = DELCELERATE_ON_ICON;
                            ++this.mDelcelerateAndStaticSnows;
                            snowflake.setDelcelerateStartPointAndTime(snowflake.getPosition());
                            snowflake.generateAccelerateSpeedY();
                            result = true;
                            return result;
                        } else {
                            snowflake.mSnowState = FLYING_WILL_NOT_STATIC_ON_ICON;
                            result = false;
                            return result;
                        }
                    }

                    result = false;
                } else {
                    result = false;
                }
            }
        }

        return result;
    }

    private GLSnowFlake generateSingleSnowflake(int width, int height) {
        GLSnowFlake snowFlake = new GLSnowFlake();
        this.resetSingleSnowflake(snowFlake, width, height);
        ++this.mFlyingSnows;
        return snowFlake;
    }

    public long getBaseSlideTime() {
        return this.mSlideTime;
    }

    public long getCurrSlideTime() {
        return this.mCurrTime;
    }

    @Override
    public float getOffset() {
        float offset = 0;

        synchronized (this.mLock) {
            if (this.mOffset < 1f && this.mOffset > -1f) {
                this.mOffset = 0f;
                this.mIsUpdatedOffset = true;

                return 0;
            }

            this.mIsUpdatedOffset = true;
            this.mOffset *= 0.96f;

            offset = this.mOffset;

            if (offset > 15f) {
                offset = 15f;
            } else if (offset < -15f) {
                offset = -15f;
            }
        }

        return offset;
    }

    public Vector3f getSlideSpeed() {
        return this.mSlideSpeed;
    }

    public float getVariableSpeed(float initSpeed, int totalFrame, int currFrame, float accDValue, int accFrames,
            float decDvalue, int decFrames) {

        if (currFrame > totalFrame) {
            return 0;
        } else if (currFrame > accFrames || accFrames <= 0) {
            return accFrames * accDValue + initSpeed - (currFrame - accFrames) * decDvalue;
        } else {
            return initSpeed + currFrame * accDValue;
        }
    }

    private void initSnowBlurred(int width, int height) {
        for (int i = 0; i < 3; ++i) {
            this.resetSnowBlurred(this.mGLSnowBlurred[i], width, height);
        }
    }

    private void initSnowBottom(int width, int height) {
    	//Log.i("myl","zhangwuba ----------- initSnowBottom height = " + height);
        float offerHeight;
        this.mSnowBottomStartTime = SystemClock.uptimeMillis();

        for (int i = 0; i < 3; ++i) {
            float snowBottomHeight = this.mSnowBottomTextureInfos[i].mHeight;
            float textureWidthRate = 0f;
            float textureHeightRate = 0f;

            if (width >= this.mSnowBottomTextureInfos[i].mWidth) {
                snowBottomHeight = ((float) width) * this.mSnowBottomTextureInfos[i].mHeight
                        / this.mSnowBottomTextureInfos[i].mWidth;
                textureHeightRate = 1f;
                textureWidthRate = 1f;
            } else if (width < this.mSnowBottomTextureInfos[i].mWidth) {
                if (i == 0) {
                    offerHeight = 7f;
                } else if (i == 1) {
                    offerHeight = 19f;
                } else {
                    offerHeight = 13f;
                }

                snowBottomHeight = this.mSnowBottomTextureInfos[i].mHeight - offerHeight;
                textureHeightRate = snowBottomHeight / this.mSnowBottomTextureInfos[i].mHeight;
                textureWidthRate = ((float) width) / this.mSnowBottomTextureInfos[i].mWidth;
            }
            //Log.i("myl","zhangwuba ----------- initSnowBottom snowBottomHeight = " + snowBottomHeight);
            this.mPosition.x = 0f;
            this.mPosition.y = -height / 2f + snowBottomHeight / 2f;
            //Log.i("myl","zhangwuba ----------- initSnowBottom mPosition.y = " + mPosition.y);
            this.mPosition.z = 0f;
            this.mGLSnowBottom[i].setWidth(width);
            this.mGLSnowBottom[i].setHeight(snowBottomHeight);
            this.mGLSnowBottom[i].setTextureWidthRate(textureWidthRate);
            this.mGLSnowBottom[i].setTextureHeightRate(textureHeightRate);
            this.mGLSnowBottom[i].setPosition(this.mPosition);
            this.mGLSnowBottom[i].buildMesh();
            this.mGLSnowBottom[i].setTextureId(this.mSnowBottomTextureInfos[i].mId);
        }
    }

    private void initSnowBottomShake(int width, int height) {
        for (int i = 0; i < SNOW_BOTTOM_SHAKE_NUM; ++i) {
            int index = RandomUtil.intRange(0, 9);
            if (width < 1080) {
                this.mGLSnowBottomShake[i].setWidth(this.mSnowBottomShakeTextureInfos[index].mWidth * width * 5f
                        / 3240f);
                this.mGLSnowBottomShake[i].setHeight(this.mSnowBottomShakeTextureInfos[index].mHeight * height * 5f
                        / 5760f);
            } else {
                this.mGLSnowBottomShake[i].setWidth(this.mSnowBottomShakeTextureInfos[index].mWidth);
                this.mGLSnowBottomShake[i].setHeight(this.mSnowBottomShakeTextureInfos[index].mHeight);
            }
            this.mGLSnowBottomShake[i].buildMesh();
            this.mGLSnowBottomShake[i].setTextureId(this.mSnowBottomShakeTextureInfos[index].mId);
        }
    }

    private void initSnowDrops(int width, int height, float offset, ArrayList<Rect> iconRects) {
        int dropSnowNum;
        int dropIconNum;
        int iconRealNum = iconRects.size();
        int restIconCur = iconRealNum;

        if (iconRealNum <= 3) {
            dropIconNum = iconRealNum;
        } else {
            dropIconNum = (int) (iconRealNum * 0.7f);
        }

        int snowDropNumPerIcon = this.mGLSnowDropSparing.size() / dropIconNum;
        if (snowDropNumPerIcon > 9) {
            dropSnowNum = 9;
        } else {
            dropSnowNum = snowDropNumPerIcon;
        }

        for (int i = 0; i < iconRealNum; ++i) {
            float snowThickness;
            WeatherIconDrawInfo drawInfo = this.mIconCallBack.getIconInfo(this.mDockBarIconNum + i, 0);

            if (drawInfo != null && drawInfo instanceof SnowIconDrawInfo) {
                snowThickness = Float.valueOf(((SnowIconDrawInfo) drawInfo).getThickness());

                if (snowThickness < 1.5f) {
                    --restIconCur;
                    continue;
                }

                if (dropIconNum <= 0) {
                    return;
                }

                if (dropIconNum < restIconCur) {
                    if (RandomUtil.intRange(0, restIconCur * 100 + 1) >= dropIconNum * 100) {
                        --restIconCur;
                        continue;
                    } else {
                        --restIconCur;
                        --dropIconNum;
                    }
                }

                int decide = RandomUtil.intRange(dropSnowNum - 2, dropSnowNum + 3);
                Rect iconRect = iconRects.get(i);

                for (int j = 0; j < decide; ++j) {
                    if (this.mGLSnowDropSparing.size() <= 0) {
                        return;
                    }

                    GLSnowDrop snowDrop = this.mGLSnowDropSparing.get(0);

                    float scale = RandomUtil.floatRange(0f, 1f);
                    float offsetScale = -offset * 0.2f * scale;

                    if (offset <= 0f) {
                        this.mDspeed.x = RandomUtil.floatRange(0.0003f * offsetScale * width, 0.0005f * offsetScale
                                * width);
                    } else {
                        this.mDspeed.x = RandomUtil.floatRange(0.0005f * offsetScale * width, 0.0003f * offsetScale
                                * width);
                    }

                    this.mDspeed.y = -height * 0.0015f;
                    this.mDspeed.z = 0f;
                    int rectHeight = iconRect.bottom - iconRect.top;
                    this.mPosition.x = RandomUtil.floatRange(iconRect.left - 0.5f * width, iconRect.right - 0.5f
                            * width);
                    this.mPosition.y = RandomUtil.floatRange(0.5f * height - iconRect.top - (rectHeight / 3f), 0.5f
                            * height - iconRect.top);
                    this.mPosition.z = 0f;
                    snowDrop.setDspeed(this.mDspeed);
                    snowDrop.setPosition(this.mPosition);

                    int textureIndex = 0;
                    if (scale < 0f) {
                        textureIndex = 1;
                    }

                    //*/zhangwuba modify 2014-8-18
                    if(width > height){
                    	snowDrop.setWidth(this.mSnowDropTextureInfos[textureIndex].mWidth * scale * width / 1920f);
                    	snowDrop.setHeight(this.mSnowDropTextureInfos[textureIndex].mHeight * scale * height / 1080f);
                    }else{
                    	snowDrop.setWidth(this.mSnowDropTextureInfos[textureIndex].mWidth * scale * width / 1080f);
                    	snowDrop.setHeight(this.mSnowDropTextureInfos[textureIndex].mHeight * scale * height / 1920f);
                    }
                    //*/
                    snowDrop.buildMesh();
                    snowDrop.mDropAccelerateY = (-height * 0.001f - height * 0.0015f * scale) / 16.666666f / 1000f;
                    snowDrop.setStartPosition(this.mPosition);
                    snowDrop.resetData();
                    snowDrop.setTextureId(this.mSnowDropTextureInfos[textureIndex].mId);
                    this.mGLSnowDropDrawing.add(snowDrop);
                    this.mGLSnowDropSparing.remove(0);
                }

                this.mIconCallBack.onIconAction(this.mDockBarIconNum + i, 0);
            }
        }
    }

    private void initSnowIces(int width, int height) {
        float iconWidth = 0f;
        float iceHeight = 0f;

        for (int i = 0; i < 1; ++i) {
            switch (i) {
            case 0:
                this.mPosition.x = 0f;
                this.mPosition.y = -0.75f * height / 2f;
                this.mPosition.z = 0f;
                iconWidth = width;
                iceHeight = 0.25f * height;
                break;
            }

            this.mGLSnowIces[i].setWidth(iconWidth);
            this.mGLSnowIces[i].setHeight(iceHeight);
            this.mGLSnowIces[i].setPosition(this.mPosition);
            this.mGLSnowIces[i].buildMesh();
            this.mGLSnowIces[i].setTextureId(this.mSnowIceTextureInfos[i].mId);
        }
    }

    private void initSnowflakes(int width, int height) {
        for (int i = 0; i < 100; ++i) {
            this.mGLSnowFlakes.add(this.generateSingleSnowflake(width, height));
        }
    }

    private void initTexture(GL10 gl) {
        for (int i = 0; i < SNOWFLAKE_TYPES; ++i) {
            this.mSnowflakeTextureInfos[i] = this.loadTexture2(gl, this.mSnowflakeTextureAsserts[i]);
        }

        for (int i = 0; i < SNOW_ICE_TYPE; ++i) {
            this.mSnowIceTextureInfos[i] = this.loadTexture2(gl, this.mSnowIceTextureAsserts[i]);
        }

        for (int i = 0; i < SNOW_DROP_TYPES; ++i) {
            this.mSnowDropTextureInfos[i] = this.loadTexture2(gl, this.mSnowDropTextureAsserts[i]);
        }

        for (int i = 0; i < SNOW_BOTTOM_TYPES; ++i) {
            this.mSnowBottomTextureInfos[i] = this.loadTexture2(gl, this.mSnowBottomTextureAsserts[i]);
        }

        for (int i = 0; i < SNOW_BOTTOM_SHAKE_TYPES; ++i) {
            this.mSnowBottomShakeTextureInfos[i] = this.loadTexture2(gl, this.mSnowBottomShakeTextureAsserts[i]);
        }

        for (int i = 0; i < SNOW_BLURRED_TYPE; ++i) {
            this.mSnowBlurredTextureInfos[i] = this.loadTexture2(gl, this.mSnowBlurredTextureAsserts[i]);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        int delayTime;
        long beginTime = SystemClock.uptimeMillis();
        long deltaTime = this.updateDrawTime(45);

        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        Vector3f slideSpeed = this.getSlideSpeed();
        float offset = this.getOffset();
        float vpWidth = this.mWidth / 2;
        float vpHeight = this.mHeight / 2;

        if (this.mIconCallBack != null
                && (this.mIconCallBack.isToggleBarOpen() || this.mIconCallBack.isCurrentPrivatePage())) {
            this.mIsSnowBottomShaking = false;
        }

        for (int i = 0; i < this.mGLSnowFlakes.size(); ++i) {
            GLSnowFlake snowFlake = this.mGLSnowFlakes.get(i);

            if (snowFlake.mSnowState != STATIC_ON_ICON && snowFlake.mSnowState != DELCELERATE_ON_ICON) {
                if (this.mSnowflakeSlideState == SLIDE_SCREEN_NONE_STATE) {
                    snowFlake.setTransition(offset, 0f, 0f);
                } else {
                    snowFlake.setTransition(slideSpeed.x, slideSpeed.y, slideSpeed.z);
                }
            }

            if (this.checkInViewport((snowFlake), vpWidth, vpHeight)) {
                snowFlake.onDraw(gl, this.mGLAlpha);
            }
        }

        for (int i = 0; i < SNOW_BLURRED_NUM; ++i) {
            if (this.mSnowflakeSlideState == SLIDE_SCREEN_NONE_STATE) {
                this.mGLSnowBlurred[i].setTransition(offset, 0f, 0f);
            } else {
                this.mGLSnowBlurred[i].setTransition(slideSpeed.x, slideSpeed.y, slideSpeed.z);
            }

            if (this.checkInViewport(this.mGLSnowBlurred[i], vpWidth, vpHeight)) {
                this.mGLSnowBlurred[i].onDraw(gl, this.mGLAlpha);
            }
        }

        if (this.mDrawIce) {
            for (int i = 0; i < SNOW_ICE_NUM; ++i) {
                this.mGLSnowIces[i].onDraw(gl, this.mGLAlpha);
            }
        }

        for (int i = 0; i < this.mGLSnowDropDrawing.size(); ++i) {
            this.mGLSnowDropDrawing.get(i).onDraw(gl, this.mGLAlpha);
        }

        if (this.mDrawSnowBottom /*&& !this.mIconCallBack.isToggleBarOpen() && !this.mIconCallBack.isCurrentPrivatePage()*/) {
            for (int i = 0; i <= this.mDrawSnowBottomIndex; ++i) {
                this.mGLSnowBottom[i].onDraw(gl, this.mGLAlpha);
            }

            if (this.mDrawSnowBottomIndex < 2 && ((this.mLastDrawTime - this.mSnowBottomStartTime)) > 6000f) {
                ++this.mDrawSnowBottomIndex;
                this.mSnowBottomStartTime = this.mLastDrawTime;
            }
        }

        if (this.mIsSnowBottomShaking) {
            for (int i = 0; i < SNOW_BOTTOM_SHAKE_NUM; ++i) {
                this.mGLSnowBottomShake[i].onDraw(gl, this.mGLAlpha);
            }
        }

        this.updateSnowflakes(deltaTime);
        this.updateSnowIces();
        this.updateSnowBottom();

        if (this.mIsSnowBottomShaking) {
            this.updateSnowBottomShake(deltaTime);
        }

        this.updateSnowDrop(deltaTime, offset);
        this.updateSnowBlurred(deltaTime);
        this.setAcceleration(0);

        delayTime = Math.max(0, (int) (45 - (SystemClock.uptimeMillis() - beginTime)));
        this.requestRenderDelayed(delayTime);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (this.mWidth != width || this.mHeight != height) {
            this.initSnowflakes(width, height);
            this.initSnowIces(width, height);
            this.initSnowBottom(width, height);
            this.initSnowBottomShake(width, height);
            this.initSnowBlurred(width, height);
            this.mBlurSnowInitTime = SystemClock.uptimeMillis();
            this.MIN_RESPONEDED_ACCELERATION = width;
            this.unregisterHandler();
            this.registerHandler();
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

        if (this.mDrawIce) {
            return;
        }

        this.mStartTime = SystemClock.uptimeMillis();
    }

    public void registerHandler() {
        if (this.mHandler == null) {
            return;
        }

        this.mHandler.postDelayed(this.delayInvokeStaticSnowflakeIcon, BLUR_SNOW_DELAY_TIME);
    }

    private void resetSingleSnowflake(GLSnowFlake snowflake, int width, int height) {
        int index = RandomUtil.intRange(0, 7);

        if (index < 3 || index > 6) {
            if (index == 2 && (RandomUtil.percent(0.5f))) {
                index = RandomUtil.intRange(0, 2);
            }
        } else if (index == 4) {
            if (RandomUtil.percent(0.6f)) {
                index = RandomUtil.intRange(0, 3);
            }
        } else if (RandomUtil.percent(0.65f)) {
            index = RandomUtil.intRange(0, 3);
        }

        this.mDspeed.y = RandomUtil.floatRange((((-width))) * 0.0015f, (((-width))) * 0.005f) * 2f;
        if (index < 2) {
            this.mDspeed.x = this.mDspeed.y * 0.2f;
        } else {
            this.mDspeed.x = this.mDspeed.y * 0.7f;
        }

        if (RandomUtil.flipCoin()) {
            this.mDspeed.x = -this.mDspeed.x;
        }

        this.mDspeed.z = 0f;
        this.mRspeed.x = RandomUtil.floatRange(0.05f, 0.1f);
        this.mRspeed.y = RandomUtil.floatRange(0.05f, 0.1f);
        this.mRspeed.z = RandomUtil.floatRange(0.04f, 0.06f);

        this.mPosition.x = RandomUtil.floatRange(-width, width);
        this.mPosition.y = RandomUtil.floatRange(height * 0.5f, height * 1.5f);
        this.mPosition.z = 0f;

        this.mRotation.x = 0f;
        this.mRotation.y = 0f;
        this.mRotation.z = 0f;

        snowflake.mAngleSpeedX = RandomUtil.floatRange(0f, 6.283185f);
        snowflake.setDspeed(this.mDspeed);
        snowflake.setRspeed(this.mRspeed);
        snowflake.setPosition(this.mPosition);
        snowflake.setRotation(this.mRotation);
      //*/zhangwuba modify 2014-8-18
        if(width > height){
        snowflake.setWidth(this.mSnowflakeTextureInfos[index].mWidth * width / 1920f);
        snowflake.setHeight(this.mSnowflakeTextureInfos[index].mHeight * height / 1080f);
        }else{
        	snowflake.setWidth(this.mSnowflakeTextureInfos[index].mWidth * width / 1080f);
            snowflake.setHeight(this.mSnowflakeTextureInfos[index].mHeight * height / 1920f);
        }
       //*/
        snowflake.buildMesh();
        snowflake.setTextureId(this.mSnowflakeTextureInfos[index].mId);
        snowflake.mSnowState = 0;
        snowflake.setAccelerateDistanceY(snowflake.getWidth() * 3f);
    }

    public void resetSlideState() {
        this.mSnowflakeSlideState = SLIDE_SCREEN_NONE_STATE;
        this.setBaseSlideTime(0);
        this.setCurrSlideTime(0);
        this.setSlideSpeed(Vector3f.ZERO);
    }

    private void resetSnowBlurred(GLSnowFlake snowBlurred, int width, int height) {
        this.mPosition.x = RandomUtil.floatRange(-width * 0.8f, width * 0.8f);
        this.mPosition.y = RandomUtil.floatRange(width * 1f, width * 1.5f);
        this.mPosition.z = 0f;

        this.mRotation.x = 0f;
        this.mRotation.y = 0f;
        this.mRotation.z = 0f;

        snowBlurred.mAngleSpeedX = RandomUtil.floatRange(0f, 6.283185f);
        this.mRspeed.x = RandomUtil.floatRange(0.05f, 0.1f);
        this.mRspeed.y = RandomUtil.floatRange(0.05f, 0.1f);
        this.mRspeed.z = RandomUtil.floatRange(0.04f, 0.06f);

        int index = RandomUtil.intRange(0, 1);
        float size = RandomUtil.floatRange(0.5f, 3f);

        if (size < 1f) {
            this.mDspeed.x = RandomUtil.floatRange(-width * 0.006f, width * 0.006f);
            this.mDspeed.y = RandomUtil.floatRange(-width * 0.008f, -width * 0.012f);
            this.mDspeed.z = 0f;
        } else if (size < 1.7f) {
            this.mDspeed.x = RandomUtil.floatRange(-width * 0.008f, width * 0.008f);
            this.mDspeed.y = RandomUtil.floatRange(-width * 0.01f, -width * 0.015f);
            this.mDspeed.z = 0f;
        } else if (size < 2.5f) {
            this.mDspeed.x = RandomUtil.floatRange(-width * 0.01f, width * 0.01f);
            this.mDspeed.y = RandomUtil.floatRange(-width * 0.015f, -width * 0.02f);
            this.mDspeed.z = 0f;
        } else {
            this.mDspeed.x = RandomUtil.floatRange(-width * 0.015f, width * 0.015f);
            this.mDspeed.y = RandomUtil.floatRange(-width * 0.025f, -width * 0.03f);
            this.mDspeed.z = 0f;
        }
        //*/zhangwuba modify 2014-8-18
        if(width > height){
        	snowBlurred.setWidth(this.mSnowBlurredTextureInfos[index].mWidth * size * width / 1920f);
        	snowBlurred.setHeight(this.mSnowBlurredTextureInfos[index].mHeight * size * height / 1080f);
        }else{
        	 snowBlurred.setWidth(this.mSnowBlurredTextureInfos[index].mWidth * size * width / 1080f);
             snowBlurred.setHeight(this.mSnowBlurredTextureInfos[index].mHeight * size * height / 1920f);
        }
        snowBlurred.setDspeed(this.mDspeed);
        snowBlurred.setRspeed(this.mRspeed);
        snowBlurred.setPosition(this.mPosition);
        snowBlurred.setRotation(this.mRotation);
        snowBlurred.buildMesh();
        snowBlurred.setTextureId(this.mSnowBlurredTextureInfos[index].mId);
    }

    private void resetSnowBottom() {
        this.mDrawSnowBottom = true;
        this.mDrawSnowBottomIndex = 0;

        for (int i = 0; i < 3; ++i) {
            this.mGLSnowBottom[i].setAlpha(0f);
        }
    }

    private void resetSnowBottomShake() {
        if (this.mDrawSnowBottomIndex >= 1) {
            this.mDrawSnowBottomIndex = 1;
            this.mGLSnowBottom[this.mDrawSnowBottomIndex].setAlpha(0.5f);

            for (int i = this.mDrawSnowBottomIndex + 1; i < SNOW_BOTTOM_TYPES; ++i) {
                this.mGLSnowBottom[i].setAlpha(0f);
            }
        }

        for (int i = 0; i < SNOW_BOTTOM_SHAKE_NUM; ++i) {
            this.mDspeed.x = RandomUtil.floatRange(-this.mWidth * 0.005f, this.mWidth * 0.005f);
            this.mInitVelocity[i] = this.mDspeed.y = RandomUtil.floatRange(this.mHeight * 0.0015f,
                    this.mHeight * 0.003f);
            this.mDspeed.z = 0f;
            this.mPosition.x = RandomUtil.floatRange(-this.mWidth / 2f, this.mWidth / 2f);
            this.mPosition.y = RandomUtil.floatRange(-this.mHeight / 2f * 0.95f, -this.mHeight / 2f * 1f);
            this.mPosition.z = 0f;
            this.mGLSnowBottomShake[i].setAlpha(1f);
            this.mGLSnowBottomShake[i].setDspeed(this.mDspeed);
            this.mGLSnowBottomShake[i].setPosition(this.mPosition);
        }
    }

    public void setBaseSlideTime(long time) {
        this.mSlideTime = time;
    }

    public void setCurrSlideTime(long time) {
        this.mCurrTime = time;
    }

    @Override
    public void setOffset(float offset) {
        super.setOffset(offset * 0.3f);
    }

    public void setSlideSpeed(Vector3f speed) {
        this.mSlideSpeed = speed;
    }

    public void setSlideState(int acceleration, long currTime) {
        if (this.getBaseSlideTime() <= 0) {
            if (acceleration > this.MIN_RESPONEDED_ACCELERATION) {
                this.mSnowflakeSlideState = SLIDE_SCREEN_RIGHT_STATE;
                this.setBaseSlideTime(currTime);
                this.setCurrSlideTime(currTime);
            } else if (acceleration < -this.MIN_RESPONEDED_ACCELERATION) {
                this.mSnowflakeSlideState = SLIDE_SCREEN_LEFT_STATE;
                this.setBaseSlideTime(currTime);
                this.setCurrSlideTime(currTime);
            }
        }
    }

    public void shake() {
        this.mIsShaking = true;
        if (this.mIconCallBack != null && !this.mIconCallBack.isToggleBarOpen()
                && !this.mIconCallBack.isCurrentPrivatePage()) {
            this.mIsSnowBottomShaking = true;
            this.resetSnowBottomShake();
            this.activateAllStaticSnowflake();
        }
    }

    public void unregisterHandler() {
        if (this.mHandler == null) {
            return;
        }

        this.mHandler.removeCallbacks(this.delayInvokeStaticSnowflakeIcon);
    }

    private void updateSnowBlurred(long deltaTime) {
        if (SystemClock.uptimeMillis() - this.mBlurSnowInitTime >= BLUR_SNOW_DELAY_TIME) {
            float delta = deltaTime / 16.666666f;

            for (int i = 0; i < 3; ++i) {
                GLSnowFlake snowFlake = this.mGLSnowBlurred[i];
                this.mPosition.set(snowFlake.getPosition());

                if (this.mPosition.y >= -this.mHeight / 2f - snowFlake.getHeight()) {
                    this.mPosition.set(snowFlake.getPosition());
                    this.mDspeed.set(snowFlake.getDspeed());
                    snowFlake.mAngleSpeedX += delta * 0.01f;
                    this.mDspeed.x *= Math.sin(snowFlake.mAngleSpeedX)
                            * (3 - (this.mPosition.y + this.mHeight / 2f) * 3f / this.mHeight) * 0.7f;
                    this.mDspeed.scale(delta);
                    snowFlake.getPosition().add(this.mDspeed);
                    this.mPosition.set(snowFlake.getPosition());

                    if (this.mPosition.x <= this.mWidth && this.mPosition.x >= -this.mWidth) {
                        snowFlake.getPosition().set(this.mPosition);
                        this.mRspeed.set(snowFlake.getRspeed());
                        this.mRspeed.scale(delta);
                        snowFlake.getRotation().add(this.mRspeed);
                        continue;
                    }

                    this.mPosition.x = RandomUtil.floatRange(-this.mWidth, -this.mWidth * 0.5f);

                    if (!RandomUtil.flipCoin()) {
                        continue;
                    }

                    this.mPosition.x = -this.mPosition.x;
                } else if (RandomUtil.percent(0.01f)) {
                    this.resetSnowBlurred(snowFlake, this.mWidth, this.mHeight);
                }
            }
        }
    }

    private void updateSnowBottom() {
        if (this.mDrawSnowBottomIndex != 2 || 0.0001f <= 1f - this.mGLSnowBottom[this.mDrawSnowBottomIndex].getAlpha()) {
            if (!this.mDrawSnowBottom) {
                if (this.mLastDrawTime - this.mSnowBottomStartTime > 10000f) {
                    this.mSnowBottomStartTime = this.mLastDrawTime;
                    this.mDrawSnowBottom = true;
                    this.mDrawSnowBottomIndex = 0;
                } else {
                    return;
                }
            }

            float alpha = (this.mLastDrawTime - this.mSnowBottomStartTime) / 6000f;

            if (alpha > 1f) {
                alpha = 1f;
            }

            this.mGLSnowBottom[this.mDrawSnowBottomIndex].setAlpha(alpha);
        }
    }

    private void updateSnowBottomShake(long deltaTime) {
        float ay;
        float delta = deltaTime / 16.666666f;

        if (this.mWidth < 1080) {
            ay = 0.07f;
        } else {
            ay = 0.1f;
        }

        this.mIsSnowBottomShaking = false;

        for (int i = 0; i < SNOW_BOTTOM_SHAKE_NUM; ++i) {
            GLSnowBottomShake snowBottomShake = this.mGLSnowBottomShake[i];

            this.mPosition.set(snowBottomShake.getPosition());
            this.mDspeed.set(snowBottomShake.getDspeed());

            if (this.mDspeed.y > 0f) {
                if (i < 40) {
                    this.mGLSnowBottomShake[i].setAlpha(this.mDspeed.y / this.mInitVelocity[i]);
                }
            } else if (i < 40) {
                this.mGLSnowBottomShake[i].setAlpha(0f);
            } else {
                this.mGLSnowBottomShake[i].setAlpha(1f + this.mDspeed.y / this.mInitVelocity[i]);
            }

            this.mPosition.x += this.mDspeed.x * delta;
            this.mPosition.y += (this.mDspeed.y - ay / 2f * delta) * delta;
            this.mDspeed.y -= ay * delta;
            this.mGLSnowBottomShake[i].setDspeed(this.mDspeed);
            snowBottomShake.getPosition().set(this.mPosition);

            if (this.mPosition.y > -this.mHeight / 2f) {
                this.mIsSnowBottomShaking = true;
            }
        }
    }

    private void updateSnowDrop(long deltaTime, float offset) {
        boolean generateNewSnowDrops;
        boolean isSteayState = this.mIconCallBack.isSteadyState();
        int i;

        for (i = 0; i < this.mGLSnowDropDrawing.size(); ++i) {
            GLSnowDrop snowDrop = this.mGLSnowDropDrawing.get(i);

            snowDrop.generateDistanceAndAlpha(deltaTime, 16.666666f);
            if (snowDrop.getAlpha() == 0f || !this.checkInViewport(snowDrop, this.mWidth / 2f, this.mHeight / 2f)) {
                this.mGLSnowDropSparing.add(this.mGLSnowDropDrawing.get(i));
                this.mGLSnowDropDrawing.remove(i);
                --i;
            }
        }

        if (isSteayState) {
            if ((this.mIconCallBack.isToggleBarOpen()) || (this.mIconCallBack.isInScrollState())) {
                generateNewSnowDrops = false;
            } else {
                generateNewSnowDrops = true;
            }

            this.mGenerateNewSnowDrops = generateNewSnowDrops;
        }

        if ((this.mIsShaking || !isSteayState) && (this.mGenerateNewSnowDrops)) {
            ArrayList<Rect> iconRects = this.mIconCallBack.getIconRects();

            this.mDockBarIconNum = 0;

            if (iconRects != null) {
                if (!this.mIsShaking) {
                    i = 0;

                    while (iconRects.size() > 0) {
                        if (iconRects.get(i).bottom <= DOCKBAR_HEIGHT_SCALE * this.mHeight) {
                            break;
                        }

                        iconRects.remove(i);
                        ++this.mDockBarIconNum;
                    }
                }

                if (iconRects.size() > 0) {
                    this.initSnowDrops(this.mWidth, this.mHeight, offset, iconRects);
                    this.mDockBarIconNum = 0;
                }
            }

            if (this.mIsShaking) {
                generateNewSnowDrops = this.mGenerateNewSnowDrops;
            } else {
                generateNewSnowDrops = false;
            }

            this.mGenerateNewSnowDrops = generateNewSnowDrops;
        }

        this.mIsShaking = false;
    }

    private void updateSnowIces() {
        long lastDrawTime;

        if (!this.mDrawIce && this.mLastDrawTime - this.mStartTime >= 3000f) {
            this.mDrawIce = true;
            this.mStartTime = this.mLastDrawTime;
        }

        if (this.mDrawIce) {
            if (this.mLastDrawTime <= 0) {
                lastDrawTime = SystemClock.uptimeMillis();
            } else {
                lastDrawTime = this.mLastDrawTime;
            }

            float alpha = (lastDrawTime - this.mStartTime) / 10000f;

            if (alpha > 0.8f) {
                alpha = 0.8f;
            }

            for (int i = 0; i < 1; ++i) {
                this.mGLSnowIces[i].setAlpha(alpha);
            }
        }
    }

    private void updateSnowflakes(long deltaTime) {
        float delta = deltaTime / 16.666666f;
        this.setSlideState(this.getAcceleration(), SystemClock.uptimeMillis());
        this.getSlideSpeed();
        long baseSlideTime = this.getBaseSlideTime();
        long currentSlideTime = this.getCurrSlideTime();

        if (this.mSnowflakeSlideState == SLIDE_SCREEN_LEFT_STATE) {
            if (currentSlideTime - baseSlideTime > SLIDE_LAST_TIME) {
                this.resetSlideState();
            } else {
                this.setSlideSpeed(this.calculateSlideDecSpeed(1));
            }
        } else {
            if (this.mSnowflakeSlideState == SLIDE_SCREEN_RIGHT_STATE) {
                if (currentSlideTime - baseSlideTime > SLIDE_LAST_TIME) {
                    this.resetSlideState();
                } else {
                    this.setSlideSpeed(this.calculateSlideDecSpeed(2));
                }
            }
        }

        this.mCurrentPageIcons = this.mIconCallBack.getIconNum();

        int isSteadyState;
        if (!this.mIconCallBack.isSteadyState() || this.mIconCallBack.isToggleBarOpen()
                || this.mIconCallBack.isInScrollState() || this.mIconCallBack.isIconOnDrag()
                || this.mCurrentPageIcons == 0) {
            isSteadyState = 0;
        } else {
            isSteadyState = 1;
        }

        float xDistance;
        for (int i = 0; i < this.mGLSnowFlakes.size(); ++i) {
            GLSnowFlake snowFlake = this.mGLSnowFlakes.get(i);

            switch (snowFlake.mSnowState) {
            case DELCELERATE_ON_ICON:
                if (isSteadyState == 0) {
                    snowFlake.mSnowState = 3;
                    --this.mDelcelerateAndStaticSnows;
                    snowFlake.mDelcelerateAllPeriod = 0f;
                    continue;
                }

                Vector3f position = snowFlake.getPosition();
                position.set(snowFlake.getDelcelerateStartPoint());
                float distance = Math.abs(snowFlake.getDelcelerateDistanceY(deltaTime));

                if (snowFlake.getDspeed().x > 0f) {
                    xDistance = distance;
                } else {
                    xDistance = -distance;
                }

                position.x += Math.abs(snowFlake.getDspeed().x / snowFlake.getDspeed().y) * xDistance;
                position.y -= distance;
                position.z = 0f;
                this.mRspeed.set(snowFlake.getRspeed());
                this.mRspeed.scale(0.5f);
                snowFlake.getRotation().add(this.mRspeed);

                if (distance >= snowFlake.getWidth() * 3f) {
                    snowFlake.mSnowState = STATIC_ON_ICON;
                    this.changeToStaticIconState(snowFlake, this.mWidth, this.mHeight, (int) deltaTime);
                    ++this.mStaticIconSnows;
                    --this.mFlyingSnows;
                    if (this.mFlyingSnows < 100) {
                        this.mGLSnowFlakes.add(this.generateSingleSnowflake(this.mWidth, this.mHeight));
                    }
                }
                break;
            case STATIC_ON_ICON:
                if (isSteadyState == 0) {
                    snowFlake.mSnowState = FLYING_WILL_NOT_STATIC_ON_ICON;
                    snowFlake.mDelcelerateAllPeriod = 0f;
                    ++this.mFlyingSnows;
                    --this.mStaticIconSnows;
                    --this.mDelcelerateAndStaticSnows;
                }
                break;
            case NORMAL_FLYING:
            case FLYING_WILL_NOT_STATIC_ON_ICON:
                this.mPosition.set(snowFlake.getPosition());
                this.mDspeed.set(snowFlake.getDspeed());
                snowFlake.mAngleSpeedX += 0.01f * delta;
                this.mDspeed.x *= Math.sin(snowFlake.mAngleSpeedX)
                        * (3 - (this.mPosition.y + this.mHeight / 2f) * 3f / this.mHeight) * 0.7f;
                snowFlake.getPosition().add(this.mDspeed);
                this.mPosition.set(snowFlake.getPosition());

                if (this.mPosition.y >= -this.mHeight / 2f) {
                    if (this.mPosition.x > this.mWidth || this.mPosition.x < -this.mWidth) {
                        this.mPosition.x = RandomUtil.floatRange(-this.mWidth, -this.mWidth * 0.5f);
                        if (RandomUtil.flipCoin()) {
                            this.mPosition.x = -this.mPosition.x;
                        }
                    }
                } else {
                    if (this.mFlyingSnows > 100) {
                        this.mGLSnowFlakes.remove(i);
                        --this.mFlyingSnows;
                        --i;
                        continue;
                    }

                    this.mPosition.x = RandomUtil.floatRange(-this.mWidth / 2f, this.mWidth / 2f);
                    this.mPosition.y = RandomUtil.floatRange(this.mHeight * 0.5f, this.mHeight * 0.5f + 30f);
                    this.mPosition.z = 0f;
                    snowFlake.mSnowState = NORMAL_FLYING;
                }

                snowFlake.getPosition().set(this.mPosition);
                this.mRspeed.set(snowFlake.getRspeed());
                this.mRspeed.scale(delta);
                snowFlake.getRotation().add(this.mRspeed);

                if (snowFlake.mSnowState != NORMAL_FLYING) {
                    if (snowFlake.mSnowState == FLYING_WILL_NOT_STATIC_ON_ICON && isSteadyState != 0) {
                        this.changeToInitFlyingState(snowFlake, this.mWidth, this.mHeight);
                    }
                }

                break;
            }
        }
    }
}
