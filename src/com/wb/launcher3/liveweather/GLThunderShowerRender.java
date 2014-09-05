package com.wb.launcher3.liveweather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLU;
import android.os.SystemClock;
import android.util.Log;

import com.dutils.math.RandomUtil;
import com.dutils.math.Vector3f;
import com.wb.launcher3.weatherIcon.ThunderAnimation;
import com.wb.launcher3.R;

public class GLThunderShowerRender extends LiveWeatherGLRender {
    class ScreenRainDropsAtts {
        Vector3f dspeed;
        Vector3f position;
        Vector3f rotation;
        Vector3f scale;
        float timePeriod;

        ScreenRainDropsAtts(GLThunderShowerRender arg4) {
            this.dspeed = new Vector3f();
            this.position = new Vector3f();
            this.timePeriod = 0f;
            this.rotation = new Vector3f();
            this.scale = new Vector3f(0.5f, 0.5f, 1f);
        }
    }

    class SmallIconDropAttr {
        Vector3f dspeed;
        Vector3f endPosition;
        Vector3f halfPosition;
        Vector3f position;
        Vector3f rotation;

        SmallIconDropAttr(GLThunderShowerRender arg2) {
            this.dspeed = new Vector3f();
            this.position = new Vector3f();
            this.endPosition = new Vector3f();
            this.halfPosition = new Vector3f();
            this.rotation = new Vector3f();
        }
    }

    private static final float AMPLITUDE = 0f;
    private static final float DEFAULT_HEIGHT = 0f;
    private static final int EDGE_RESERVED = 12;
    private static final int ICON_DROP_NUM = 8;
    private static final int ICON_DROP_TYPES = 1;
    private static final int ICON_RAINDROP_HEIGHT = 56;
    private static final int ICON_RAINDROP_WIDTH = 40;
    private static final int LEVEL_NUM = 3;
    private static final int LIGHTNING_TYPES = 5;
    private static final float MIN_INTERVAL_TIME = 0f;
    private static final int RAINDROP_NUM = 35;
    private static final int RAINDROP_TYPES = 2;
    private static final int SCREEN_DROP_NUM = 4;
    private static final int SCREEN_RAINDROP_TYPES = 3;
    private static final float SIZE_CHANGE_ZONE = 0f;
    private static final float SMALL_ICON_DROP_ACCELERATION = 0f;
    private static final int SMALL_ICON_DROP_NUM = 1;
    private static final int SMALL_ICON_DROP_TYPES = 1;
    private static final float SMALL_ICON_DROP_X_OFFSET_ADJUST_RATIO = 0f;
    private static final float TIME_PERIOD = 0f;
    private static final float TIME_PERIOD_MAX = 0f;
    private static final float TIME_PERIOD_MIN = 0f;
    private static final int WORKSPACE_PAGEDVIEW_NUM = 9;

    Vector3f accele;
    private int iconHeight;
    private float iconRainHeight;
    private float iconRainWidth;
    private boolean isFreeFalling;
    private Vector3f mAccele;
    private GLLightning mBgLightning;
    private GLLightning mCurLightning;
    private int mCurLightningId;
    private GLLightning[] mGLLightnings;
    private ArrayList<GLIconRaindrop> mGLRainNowIcondrops;
    private GLScreenRaindrop[] mGLRainScreendrops;
    private GLRaindrop[] mGLRaindrops;
    private ArrayList<GLIconSmallRaindrop> mGLSmallIconDrops;
    private HashMap<Integer, ArrayList<Integer>> mHas;
    private int[] mIconDropTextureAsserts;
    private TextureInfo[] mIconDropTextureInfos;
    private boolean mIsActionDown;
    private boolean mIsLighting;
    private float mLastLightingTime;
    private float mLastOffset;
    Vector3f mLightningPosition;
    private int[] mLightningRes;
    private TextureInfo[] mLightningTextureInfos;
    private float mMaxAlpha;
    private float[] mMaxAlphaArray;
    private Random mRandom;
    private int[] mScreenDropTextureAsserts;
    private TextureInfo[] mScreenDropTextureInfos;
    private ScreenRainDropsAtts mScreenRainDropsAtts;
    private SmallIconDropAttr mSmallIconDropAttr;
    private int[] mSmallIconDropTextureAsserts;
    private TextureInfo[] mSmallIconDropTextureInfos;
    private int mSmallIconNum;
    private int[] mTextureAsserts;
    private TextureInfo[] mTextureInfos;
    private Vector3f[] mTimeLevels;
    Vector3f temLevel;
    Vector3f temSpeed;
    Vector3f tempScreenDropScale;
    Vector3f tempposition;
    Vector3f tempspeed;
    private float xOffset;

    GLThunderShowerRender(Context context, LiveWeatherGLView liveWeatherGLView) {
        super(context, liveWeatherGLView);

        this.mLastOffset = 0f;
        this.mHas = new HashMap<Integer, ArrayList<Integer>>();
        this.iconRainWidth = 0f;
        this.iconRainHeight = 0f;
        this.iconHeight = 0;
        this.mRandom = new Random();
        this.mAccele = new Vector3f(0f, -10f, 0f);
        this.mGLRainNowIcondrops = new ArrayList<GLIconRaindrop>();
        this.mGLSmallIconDrops = new ArrayList<GLIconSmallRaindrop>();
        this.mTextureInfos = new TextureInfo[2];
        this.mScreenDropTextureInfos = new TextureInfo[SCREEN_RAINDROP_TYPES];
        this.mIconDropTextureInfos = new TextureInfo[ICON_DROP_TYPES];
        this.mSmallIconDropTextureInfos = new TextureInfo[SMALL_ICON_DROP_TYPES];
        this.isFreeFalling = false;
        this.xOffset = 0f;
        this.tempposition = null;
        this.tempspeed = new Vector3f();
        this.accele = new Vector3f();
        this.temSpeed = new Vector3f();
        this.tempScreenDropScale = new Vector3f();
        this.mTextureAsserts = new int[] { R.drawable.raindrop_0, R.drawable.raindrop_1 };
        this.mScreenDropTextureAsserts = new int[] { R.drawable.ts_screen_drop1, R.drawable.ts_screen_drop2,
                R.drawable.ts_screen_drop3 };
        this.mSmallIconDropTextureAsserts = new int[] { R.drawable.small_icon_rain };
        this.mIconDropTextureAsserts = new int[] { R.drawable.drops_icon1 };
        this.mLightningRes = new int[] { R.drawable.flash_a, R.drawable.flash_b, R.drawable.flash_c,
                R.drawable.flash_d, R.drawable.flash_bg };
        this.mLightningTextureInfos = new TextureInfo[LIGHTNING_TYPES];
        this.mLightningPosition = new Vector3f(0f, 0f, 0f);
        this.mCurLightningId = 0;
        this.mIsLighting = false;
        this.mIsActionDown = false;
        this.mTimeLevels = null;
        this.temLevel = new Vector3f();
        this.mMaxAlphaArray = new float[] { 1f, 0.85f, 0.7f };
        this.mMaxAlpha = 1f;
        this.mSmallIconDropAttr = new SmallIconDropAttr(this);
        this.mScreenRainDropsAtts = new ScreenRainDropsAtts(this);
        this.mGLRaindrops = new GLRaindrop[35];
        this.mSmallIconNum = this.getSmallIconNum();
        this.mGLRainScreendrops = new GLScreenRaindrop[4];

        for (int i = 0; i < RAINDROP_NUM; ++i) {
            this.mGLRaindrops[i] = new GLRaindrop();
        }

        for (int i = 0; i < SCREEN_DROP_NUM; ++i) {
            this.mGLRainScreendrops[i] = new GLScreenRaindrop();
        }

        this.mGLLightnings = new GLLightning[LIGHTNING_TYPES];
        for (int i = 0; i < LIGHTNING_TYPES; ++i) {
            this.mGLLightnings[i] = new GLLightning();
        }

        this.mTimeLevels = new Vector3f[LEVEL_NUM];
        this.mTimeLevels[0] = new Vector3f(0.3f, 0.5f, 0.8f);
        this.mTimeLevels[1] = new Vector3f(0.2f, 0.4f, 0.6f);
        this.mTimeLevels[2] = new Vector3f(0.1f, 0.4f, 0.5f);
    }

    private synchronized void changeSmallIconDropStateToFreeFall() {
        for (int i = 0; i < this.mSmallIconNum; ++i) {
            this.mGLSmallIconDrops.get(i).setIsFreeFall(true);
        }
    }

    private boolean checkLevelValid(Vector3f level) {
        if (level == null) {
            return false;
        }

        if (level.x < 0f || level.y <= level.x || level.z <= level.y || level.z >= 1f) {
            return false;
        }

        return true;
    }

    private float distanceY(float y1, float y2) {
        return Math.abs(y2 - y1);
    }

    private GLIconRaindrop generateRaindrop(int index, int width, int height, float positionX, float positionY,
            float distance) {
        GLIconRaindrop raindrop = new GLIconRaindrop();

        Vector3f speed = new Vector3f();
        Vector3f position = new Vector3f();
        Vector3f rotation = new Vector3f();

        speed.x = 0f;
        speed.y = RandomUtil.floatRange(-width * 0.001f, -width * 0.002f);
        speed.z = 0f;

        position.x = positionX;
        position.y = positionY;
        position.z = 0f;

        rotation.x = 0f;
        rotation.y = 0f;
        rotation.z = 0f;

        raindrop.distance = distance;
        raindrop.setAlpha(0f);
        raindrop.scale = 1f;
        raindrop.setDspeed(speed);
        raindrop.setPosition(position);
        raindrop.setInitialSpeed(speed);
        raindrop.setRotation(rotation);
        raindrop.setInitialPosition(position);
        raindrop.mFrameCount = 0;
        raindrop.setAccele(this.mAccele);
        raindrop.setWidth(this.iconRainWidth / 2f);
        raindrop.setHeight(this.iconRainHeight / 2f);
        raindrop.isConstantSpeedEnd = false;
        raindrop.timeSpend = (int) (distance / this.iconHeight / 2f * 60f);

        if (raindrop.timeSpend < 10) {
            raindrop.timeSpend = 30;
        }

        raindrop.buildMesh();
        raindrop.setTextureId(this.mIconDropTextureInfos[0].mId);
        raindrop.mIndex = index;

        return raindrop;
    }

    private float getAlpha(float time, float timePeriod) {
        if (time > timePeriod * 0.4f) {
            if (time > timePeriod * 0.4f && time <= 0.65f * timePeriod) {
                return 1 - (time - timePeriod * 0.4f) / (0.25f * timePeriod);
            }

            return 0;
        }

        return 1;
    }

    private float getLightningAlpha(float time, float timePeriod, GLLightning lightning) {
        if (lightning == null) {
            return 0;
        }

        this.temLevel = lightning.getTimeLevel();
        if (!this.checkLevelValid(this.temLevel)) {
            Log.e("GLThunderShowerRender", "getLightningAlpha --- temLevel is unvalid, return ! ");
            return 0;
        }

        if (time <= this.temLevel.x * timePeriod) {
            return time / (this.temLevel.x * timePeriod);
        }

        if (time > this.temLevel.x * timePeriod && time <= this.temLevel.y * timePeriod) {
            return 1 - (time - this.temLevel.x * timePeriod) / ((this.temLevel.y - this.temLevel.x) * timePeriod);
        }

        if (time > this.temLevel.y * timePeriod && time <= this.temLevel.z * timePeriod) {
            return (time - this.temLevel.y * timePeriod) / ((this.temLevel.z - this.temLevel.y) * timePeriod);
        }

        if (time > this.temLevel.z * timePeriod && time <= timePeriod) {
            return 1 - (time - this.temLevel.z * timePeriod) / ((1 - this.temLevel.z) * timePeriod);
        }

        return 0;
    }

    @Override
    public float getOffset() {
        synchronized (this.mLock) {
            this.mIsUpdatedOffset = true;
        }

        return this.mOffset;
    }

    private Vector3f getScreenDropScale(float time, float timePeriod) {
        this.tempScreenDropScale.z = 1;
        if (time <= timePeriod * 0.03f) {
            this.tempScreenDropScale.x = 0.5f * time / (timePeriod * 0.03f) + 0.5f;
            this.tempScreenDropScale.y = 0.5f * time / (timePeriod * 0.03f) + 0.5f;
        } else if (time > timePeriod * 0.03f && time <= 0.65f * timePeriod) {
            this.tempScreenDropScale.x = 1 - 0.15f * (time - timePeriod * 0.03f) / (timePeriod * 0.62f);
            this.tempScreenDropScale.y = (time - timePeriod * 0.03f) * 0.5f / (timePeriod * 0.62f) + 1;
        } else {
            this.tempScreenDropScale.x = 0.85f;
            this.tempScreenDropScale.y = 1.5f;
        }

        return this.tempScreenDropScale;
    }

    private float getScreenDropSpeed(float time, float timePeriod) {
        if (time < 0.75f * timePeriod) {
            return (float) (-1.5f * (Math.sin(2 * Math.PI / timePeriod * time) + 1) * this.mHeight / 1920f);
        } else {
            return 0;
        }
    }

    private float getSmallIconDropAlpha() {
        return 1f;
    }

    private float getSmallIconDropSpeed() {
        return -1.2f;
    }

    private int getSmallIconNum() {
        int smallIconNum = 1;

        if (this.mIconCallBack != null) {
            smallIconNum = this.mIconCallBack.getIconNum();
        }

        if (smallIconNum == 0) {
            smallIconNum = 1;
        }

        return smallIconNum;
    }

    private void initLightnings(int width, int height) {
        for (int i = 0; i < LIGHTNING_TYPES; ++i) {
            this.initOneLightning(i, width, height);
        }

        this.mCurLightningId = RandomUtil.intRange(0, LIGHTNING_TYPES - 1);
        this.mCurLightning = this.mGLLightnings[this.mCurLightningId];
        this.mLastLightingTime = (SystemClock.uptimeMillis());
        this.mBgLightning = this.mGLLightnings[LIGHTNING_TYPES - 1];
    }

    private void initOneLightning(int whichone, int width, int height) {
        float lightningWidth = this.mLightningTextureInfos[whichone].mWidth / 1920f * height;
        float lightningHeight = this.mLightningTextureInfos[whichone].mHeight / 1920f * height;
        int levelIndex = RandomUtil.intRange(0, 3);

        if (whichone == 0) {
            this.mLightningPosition.x = RandomUtil.floatRange(-width * 0.2f, width * 0.2f);
            this.mLightningPosition.y = RandomUtil.floatRange(0f, height * 0.1f);
            lightningWidth = width;
            lightningHeight = height;
        } else if (whichone == 1) {
            this.mLightningPosition.x = RandomUtil.floatRange(-width * 0.35f, -width * 0.25f);
            this.mLightningPosition.y = RandomUtil.floatRange(0f, height * 0.25f);
            lightningWidth *= 3f;
            lightningHeight *= 3f;
        } else if (whichone == 2) {
            this.mLightningPosition.x = RandomUtil.floatRange(width * 0.2f, width * 0.3f);
            this.mLightningPosition.y = RandomUtil.floatRange(height * 0.1f, height * 0.3f);
            lightningWidth *= 4f;
            lightningHeight *= 4f;
        } else if (whichone == 3) {
            this.mLightningPosition.x = RandomUtil.floatRange(-width * 0.2f, 0f);
            this.mLightningPosition.y = RandomUtil.floatRange(height * 0.35f, height * 0.4f);
            lightningWidth *= 2f;
            lightningHeight *= 3f;
        } else if (whichone == 4) {
            this.mLightningPosition.x = 0f;
            this.mLightningPosition.y = 0f;
            lightningWidth = width;
            lightningHeight = height;
        }

        this.mGLLightnings[whichone].setPosition(this.mLightningPosition);
        this.mGLLightnings[whichone].setWidth(lightningWidth);
        this.mGLLightnings[whichone].setHeight(lightningHeight);
        this.mGLLightnings[whichone].setTimePeriod(500f);
        this.mGLLightnings[whichone].setTime(0f);
        this.mGLLightnings[whichone].setAlpha(0f);
        this.mGLLightnings[whichone].setTimeLevel(this.mTimeLevels[levelIndex]);
        this.mGLLightnings[whichone].buildMesh();
        this.mGLLightnings[whichone].setTextureId(this.mLightningTextureInfos[whichone].mId);
    }

    private void initRaindrops(int width, int height) {
        for (int i = 0; i < 35; ++i) {
            this.mGLRaindrops[i].setRainDropAlgorithm(width, height);

            if (i < 10) {
                this.mGLRaindrops[i].setTagRainDropDepth(0);
            } else if (i < 20) {
                this.mGLRaindrops[i].setTagRainDropDepth(2);
            } else {
                this.mGLRaindrops[i].setTagRainDropDepth(1);
            }

            float raindropHeight = 0.133f * height;
            int texutreIndex = this.mGLRaindrops[i].randomTextureValue();
            this.mGLRaindrops[i].setWidth(0.015f * width);
            this.mGLRaindrops[i].setHeight(raindropHeight);
            this.mGLRaindrops[i].setAdaptRotateHeight(raindropHeight);
            this.mGLRaindrops[i].buildMesh();
            this.mGLRaindrops[i].setTextureId(this.mTextureInfos[texutreIndex].mId);
        }

        for (int i = 0; i < 4; ++i) {
            this.initScreenRaindrops(i, width, height);
        }
    }

    private void initScreenRaindrops(int whichone, int width, int height) {
        int textureIndex;
        float baseHeight = 1920f;

        if (this.mIconCallBack != null && !this.mIconCallBack.isToggleBarOpen()) {
            this.mScreenRainDropsAtts.dspeed.x = 0f;
            this.mScreenRainDropsAtts.dspeed.y = -1.5f;
            this.mScreenRainDropsAtts.dspeed.z = 0f;
            this.mScreenRainDropsAtts.position.x = RandomUtil.floatRange(-width * 0.45f, width * 0.45f);
            this.mScreenRainDropsAtts.position.y = RandomUtil.floatRange(-height * 0.15f, height * 0.45f);
            this.mScreenRainDropsAtts.position.z = 2f;
            this.mScreenRainDropsAtts.timePeriod = RandomUtil.floatRange(3072f, 3456f);
            this.mScreenRainDropsAtts.rotation.x = 0f;
            this.mScreenRainDropsAtts.rotation.y = 0f;
            this.mScreenRainDropsAtts.rotation.z = -this.mScreenRainDropsAtts.dspeed.x * 0.5f;

            if (RandomUtil.flipCoin()) {
                textureIndex = RandomUtil.intRange(0, SCREEN_RAINDROP_TYPES);
            } else {
                textureIndex = RandomUtil.intRange(0, SCREEN_RAINDROP_TYPES);
            }

            this.mGLRainScreendrops[whichone].setAlpha(1f);
            this.mGLRainScreendrops[whichone].setScale(this.mScreenRainDropsAtts.scale);
            this.mGLRainScreendrops[whichone].setDspeed(this.mScreenRainDropsAtts.dspeed);
            this.mGLRainScreendrops[whichone].setPosition(this.mScreenRainDropsAtts.position);
            this.mGLRainScreendrops[whichone].setTimePeriod(this.mScreenRainDropsAtts.timePeriod);
            this.mGLRainScreendrops[whichone].setTime(0f);
            this.mGLRainScreendrops[whichone].setRotation(this.mScreenRainDropsAtts.rotation);
            this.mGLRainScreendrops[whichone].setImageHeight(this.mScreenDropTextureInfos[textureIndex].mHeight);
            this.mGLRainScreendrops[whichone].setWidth(((this.mScreenDropTextureInfos[textureIndex].mWidth))
                    / baseHeight * height);
            this.mGLRainScreendrops[whichone].setHeight(((this.mScreenDropTextureInfos[textureIndex].mHeight))
                    / baseHeight * height);
            this.mGLRainScreendrops[whichone].buildMesh();
            this.mGLRainScreendrops[whichone].setTextureId(this.mScreenDropTextureInfos[textureIndex].mId);
        }
    }

    private void initSmallIconDrop(int whichone, int width, int height) {
        this.mSmallIconDropAttr.dspeed.x = 0f;
        this.mSmallIconDropAttr.dspeed.y = -54f;
        this.mSmallIconDropAttr.dspeed.z = 0f;
        this.mSmallIconDropAttr.position.x = RandomUtil.floatRange(width * -0.5f, width * 0.5f);
        this.mSmallIconDropAttr.position.y = RandomUtil.floatRange(height * -0.5f, height * 0.5f);
        this.mSmallIconDropAttr.position.z = 0f;

        int x = (int) (this.mSmallIconDropAttr.position.x + width * 0.5f);
        int y = -(int) (this.mSmallIconDropAttr.position.y - height * 0.5f);
        
        Log.i("myl","zhangwuba --------- initSmallIconDrop mIconCallBack = " + mIconCallBack);

        if (this.mIconCallBack == null) {
            return;
        }

        boolean isIconDropInvalid;
        int[] edge = this.mIconCallBack.getEdgeForPos(x, y);

        if (edge == null || edge[0] == 0 || edge[1] == 0 || edge[2] == 0 || edge[3] == 0) {
            isIconDropInvalid = true;
        } else {
            this.mSmallIconDropAttr.endPosition.x = this.mSmallIconDropAttr.position.x;
            this.mSmallIconDropAttr.endPosition.y = this.mSmallIconDropAttr.position.y - edge[3] + 4f
                    + this.mSmallIconDropTextureInfos[0].mHeight;
            this.mSmallIconDropAttr.endPosition.z = this.mSmallIconDropAttr.position.z;
            this.mSmallIconDropAttr.halfPosition.x = this.mSmallIconDropAttr.position.x;
            this.mSmallIconDropAttr.halfPosition.y = (this.mSmallIconDropAttr.endPosition.y + this.mSmallIconDropAttr.position.y) / 2f;
            this.mSmallIconDropAttr.halfPosition.z = this.mSmallIconDropAttr.position.z;

            if (edge[0] >= 12) {
                if (edge[2] < 12) {
                    this.mSmallIconDropAttr.position.x -= 12f;
                } else {
                    this.mSmallIconDropAttr.position.x += 12f;
                }
            }

            if (this.mSmallIconDropAttr.endPosition.y <= this.mSmallIconDropAttr.position.y) {
                isIconDropInvalid = false;
            } else {
                isIconDropInvalid = true;
            }
        }

        this.mSmallIconDropAttr.rotation.x = 0f;
        this.mSmallIconDropAttr.rotation.y = 0f;
        this.mSmallIconDropAttr.rotation.z = -this.mSmallIconDropAttr.dspeed.x * 0.5f;

        GLIconSmallRaindrop raindrop = this.mGLSmallIconDrops.get(whichone);
        raindrop.setAlpha(0f);
        raindrop.setDspeed(this.mSmallIconDropAttr.dspeed);
        raindrop.setIsFreeFall(false);
        raindrop.setIsIconDropInvalid(isIconDropInvalid);
        raindrop.setPosition(this.mSmallIconDropAttr.position);
        raindrop.setEndPosition(this.mSmallIconDropAttr.endPosition);
        raindrop.setHalfPosition(this.mSmallIconDropAttr.halfPosition);
        raindrop.setScale(0.8f);
        raindrop.setRotation(this.mSmallIconDropAttr.rotation);
        raindrop.setWidth(this.mSmallIconDropTextureInfos[0].mWidth * 0.8f / 1920f * height);
        raindrop.setHeight(this.mSmallIconDropTextureInfos[0].mHeight * 0.8f / 1920f * height);
        raindrop.buildMesh();
        raindrop.setTextureId(this.mSmallIconDropTextureInfos[0].mId);
    }

    private void initSmallIconList() {
        int smallIconDropNum = this.mGLSmallIconDrops.size();
        int smallIconNum = this.getSmallIconNum();

        if (smallIconDropNum != smallIconNum) {
            if (smallIconDropNum < smallIconNum) {
                for (int i = 0; i < smallIconNum - smallIconDropNum; ++i) {
                    this.mGLSmallIconDrops.add(new GLIconSmallRaindrop());
                }
            } else {
                for (int i = smallIconDropNum; i > smallIconNum; --i) {
                    this.mGLSmallIconDrops.remove(i - 1);
                }
            }
        }
    }

    private void initTexture(GL10 gl) {
        int i = 0;
        for (i = 0; i < RAINDROP_TYPES; ++i) {
            this.mTextureInfos[i] = this.loadTexture2(gl, this.mTextureAsserts[i]);
        }

        for (i = 0; i < SCREEN_RAINDROP_TYPES; ++i) {
            this.mScreenDropTextureInfos[i] = this.loadTexture2(gl, this.mScreenDropTextureAsserts[i]);
        }

        for (i = 0; i < ICON_DROP_TYPES; ++i) {
            this.mIconDropTextureInfos[i] = this.loadTexture2(gl, this.mIconDropTextureAsserts[i]);
        }

        for (i = 0; i < SMALL_ICON_DROP_TYPES; ++i) {
            this.mSmallIconDropTextureInfos[i] = this.loadTexture2(gl, this.mSmallIconDropTextureAsserts[i]);
        }

        for (i = 0; i < LIGHTNING_TYPES; ++i) {
            this.mLightningTextureInfos[i] = this.loadTexture2(gl, this.mLightningRes[i]);
        }
    }

    private boolean isWorkspaceStatic() {
        if (this.mIconCallBack == null) {
            Log.e("GLThunderShowerRender", "error mIconCallBack = null");
            return false;
        } else if (this.mIconCallBack.isSteadyState() && !this.mIconCallBack.isToggleBarOpen()
                && !this.mIconCallBack.isInScrollState()) {
            return true;
        }

        return true;//false; ///zhangwuba modify temp
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        int delayTime;
        long beginTime = SystemClock.uptimeMillis();

        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        float offset = this.getOffset();
        float vpWidth = (this.mWidth / 2);
        float vpHeight = (this.mHeight / 2);

        int i;
        for (i = 0; i < this.mGLSmallIconDrops.size(); ++i) {
            if (!this.mGLSmallIconDrops.get(i).getIsIconDropInvalid()) {
                this.mGLSmallIconDrops.get(i).onDraw(gl, this.mGLAlpha);
            }
        }

        for (i = 0; i < RAINDROP_NUM; ++i) {
            if (this.checkInViewport(this.mGLRaindrops[i], vpWidth, vpHeight)) {
                this.mGLRaindrops[i].setScreenOffsetX(offset);
                this.mGLRaindrops[i].onDraw(gl, this.mGLAlpha);
            }
        }

        for (i = 0; i < SCREEN_DROP_NUM; ++i) {
            if (this.checkInViewport(this.mGLRainScreendrops[i], vpWidth, vpHeight)) {
                this.mGLRainScreendrops[i].onDraw(gl, this.mGLAlpha);
            }
        }

        int rainDropNum = this.mGLRainNowIcondrops.size();
        if (this.mIconCallBack != null) {
            ArrayList iconRects = this.mIconCallBack.getIconRects();

            if (iconRects != null) {
                int iconRectnum = iconRects.size();

                if (rainDropNum < 8 && iconRectnum != 0 && this.isWorkspaceStatic() && !this.isFreeFalling) {
                    int iconRectIndex = RandomUtil.intRange(0, iconRectnum);
                    Rect iconRect = (Rect) iconRects.get(iconRectIndex);
                    this.iconHeight = iconRect.bottom - iconRect.top;
                    int x = RandomUtil.intRange(iconRect.left, iconRect.right);
                    int y = RandomUtil.intRange(iconRect.top, iconRect.bottom);
                    int[] reversedXY = this.changeXYReverse(x, y);
                    int[] edge = this.mIconCallBack.getEdgeForPos(x, y);

                    if (edge != null && RandomUtil.intRange(0, (int) (3000f / (iconRectnum * 45) + 1)) < 1) {
                        int isToGenerate = 1;
                        ArrayList<Integer> alist = this.mHas.get(Integer.valueOf(iconRectIndex));
                        if (edge[0] < this.iconRainWidth / 2f || edge[1] < this.iconRainHeight / 2f
                                || edge[2] < this.iconRainWidth / 2f || edge[3] < this.iconRainHeight / 2f) {
                            isToGenerate = 0;
                        }

                        if (alist != null && isToGenerate == 1) {
                            for (i = 0; i < alist.size(); ++i) {
                                if (reversedXY[0] > alist.get(i).intValue() - this.iconRainWidth
                                        && reversedXY[0] < alist.get(i).intValue() + this.iconRainWidth) {
                                    isToGenerate = 0;
                                    break;
                                }
                            }
                        }

                        if (isToGenerate != 0) {
                            this.mGLRainNowIcondrops.add(this.generateRaindrop(iconRectIndex, this.mWidth,
                                    this.mHeight, reversedXY[0], reversedXY[1], edge[3]));
                            if (alist == null) {
                                ArrayList<Integer> newlist = new ArrayList<Integer>();
                                newlist.add(Integer.valueOf(reversedXY[0]));
                                this.mHas.put(Integer.valueOf(iconRectIndex), newlist);
                            } else {
                                alist.add(Integer.valueOf(reversedXY[0]));
                            }

                            ++rainDropNum;
                        }
                    }
                }
            }
        }

        for (i = 0; i < rainDropNum; ++i) {
            this.mGLRainNowIcondrops.get(i).onDraw(gl, this.mGLAlpha);
        }

        if (!this.mIsLighting || this.mCurLightning == null) {
            ThunderAnimation.CHANGE_COLOR = false;
        } else {
            ThunderAnimation.CHANGE_COLOR = true;
            this.mBgLightning.onDraw(gl, this.mGLAlpha);
            this.mCurLightning.onDraw(gl, this.mGLAlpha);
        }

        long deltaTime = this.updateDrawTime(45);
        this.updateSmallIcondrops(deltaTime, offset);
        this.updateScreenRaindrops(deltaTime);
        this.updateIconRaindrops(deltaTime, offset);
        this.updateCurrentLightning(deltaTime, offset);
        this.mLastOffset = this.getOffset();

        delayTime = (int) Math.max(0, 45 - (SystemClock.uptimeMillis() - beginTime));
        this.requestRenderDelayed((delayTime));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.initSmallIconList();
        if (this.mWidth != width || this.mHeight != height) {
            if (this.isWorkspaceStatic() /*
                                          * &&
                                          * !this.mIconCallBack.isIconOnDrag()
                                          */) {
                for (int i = 0; i < this.mGLSmallIconDrops.size(); ++i) {
                    this.initSmallIconDrop(i, width, height);
                }
            }

            this.initRaindrops(width, height);
            this.initLightnings(width, height);
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
        this.iconRainWidth = this.mContext.getResources().getDisplayMetrics().density / 3f * ICON_RAINDROP_WIDTH;
        this.iconRainHeight = this.mContext.getResources().getDisplayMetrics().density / 3f * ICON_RAINDROP_HEIGHT;
    }

    public void removeSmallIconDrops() {
        if (this.mGLSmallIconDrops != null) {
            int smallIconDropNum = this.mGLSmallIconDrops.size();

            for (int i = 0; i < smallIconDropNum; ++i) {
                this.mGLSmallIconDrops.remove(smallIconDropNum - i - 1);
            }
        }
    }

    private void resetCurLightningData() {
        if (this.mCurLightning != null) {
            int timeLevelIndex = RandomUtil.intRange(0, LEVEL_NUM);
            if (this.mCurLightningId == 0) {
                this.mLightningPosition.x = RandomUtil.floatRange(-this.mWidth * 0.2f, this.mWidth * 0.2f);
                this.mLightningPosition.y = RandomUtil.floatRange(0f, this.mHeight * 0.1f);
            } else if (this.mCurLightningId == 1) {
                this.mLightningPosition.x = RandomUtil.floatRange(-this.mWidth * 0.35f, -this.mWidth * 0.25f);
                this.mLightningPosition.y = RandomUtil.floatRange(0f, this.mHeight * 0.25f);
            } else if (this.mCurLightningId == 2) {
                this.mLightningPosition.x = RandomUtil.floatRange(this.mWidth * 0.2f, this.mWidth * 0.3f);
                this.mLightningPosition.y = RandomUtil.floatRange(this.mHeight * 0.1f, this.mHeight * 0.3f);
            } else if (this.mCurLightningId == 3) {
                this.mLightningPosition.x = RandomUtil.floatRange(-this.mWidth * 0.2f, 0f);
                this.mLightningPosition.y = RandomUtil.floatRange(this.mHeight * 0.35f, this.mHeight * 0.4f);
            }

            this.mCurLightning.setPosition(this.mLightningPosition);
            this.mCurLightning.setTimeLevel(this.mTimeLevels[timeLevelIndex]);
        }
    }

    public void resetRenderOffset() {
        for (int i = 0; i < RAINDROP_NUM; ++i) {
            this.mGLRaindrops[i].resetScreenOffsetX();
        }
    }

    @Override
    public void setOffset(float offset) {
        super.setOffset(offset * 1f);
    }

    private void updateCurrentLightning(long deltaTime, float xSpeed) {
        if (this.mCurLightning != null) {
            float timePeriod = this.mCurLightning.getTimePeriod();
            float time = this.mCurLightning.getTime() + deltaTime;
            this.mCurLightning.setTime(time);

            if (time > timePeriod) {
                this.resetCurLightningData();
                this.mCurLightning.setAlpha(0f);
                this.mCurLightning.setTime(0f);
                this.mIsActionDown = false;
                this.mCurLightning = null;
                this.mIsLighting = false;
                this.mCurLightningId = RandomUtil.intRange(0, 4);
                this.mLastLightingTime = SystemClock.uptimeMillis();

                int tempAlphaIndex = RandomUtil.intRange(0, 3);
                if (tempAlphaIndex < this.mMaxAlphaArray.length) {
                    this.mMaxAlpha = this.mMaxAlphaArray[tempAlphaIndex];
                }
            } else {
                this.mCurLightning.setAlpha(this.mMaxAlpha
                        * this.getLightningAlpha(time, timePeriod, this.mCurLightning));
            }

            if (time > timePeriod * 0.2f) {
                this.mBgLightning.setAlpha(0f);
                return;
            }

            if (this.mCurLightningId != 0) {
                this.mBgLightning.setAlpha(0.2f);
                return;
            }

            this.mBgLightning.setAlpha(0.4f);

            return;
        }

        int passwd = 0;
        if (SystemClock.uptimeMillis() - this.mLastLightingTime <= 3000f) {
            passwd = 0;
        } else {
            passwd = 1;
        }

        if ((RandomUtil.flipCoin() && passwd != 0) || this.mIsActionDown) {
            this.mIsActionDown = false;
            if (this.mCurLightningId >= 4) {
                this.mCurLightningId = 0;
            }

            this.mCurLightning = this.mGLLightnings[this.mCurLightningId];
            this.mIsLighting = true;
        }
    }

    @Override
    public void updateEmptyAreaMotionEvent() {
        this.mIsActionDown = true;
    }

    private void updateIconRaindrops(long deltaTime, float xSpeed) {
        for (int i = 0; i < this.mGLRainNowIcondrops.size(); ++i) {
            if (this.mIconCallBack.isIconOnDrag()) {
                this.mGLRainNowIcondrops.clear();
                break;
            } else {
                if (!this.isWorkspaceStatic() && this.xOffset == 0f) {
                    this.isFreeFalling = true;
                    if (Math.abs(xSpeed) > 30f) {
                        this.xOffset = xSpeed / Math.abs(xSpeed) * 15f;
                    } else {
                        this.xOffset = 0f;
                    }
                }

                GLIconRaindrop iconRaindrop = this.mGLRainNowIcondrops.get(i);
                this.tempposition = iconRaindrop.getPosition();
                this.tempspeed.set(iconRaindrop.getDspeed());
                this.temSpeed = iconRaindrop.getInitialSpeed();

                if (this.distanceY(this.tempposition.y, iconRaindrop.getInitialPosition().y) >= iconRaindrop.distance
                        || (iconRaindrop.isConstantSpeedEnd)) {
                    if (iconRaindrop.scale < 2f && !this.isFreeFalling) {
                        iconRaindrop.setTempPostion(this.tempposition);
                        iconRaindrop.mSrcAlpha += 0.01f;
                        iconRaindrop.scale += 0.01f;
                        iconRaindrop.setWidth(this.iconRainWidth / 2f * iconRaindrop.scale);
                        iconRaindrop.setHeight(this.iconRainHeight / 2f * iconRaindrop.scale);
                        iconRaindrop.buildMesh();
                        iconRaindrop.mFrameCount = 0;
                    } else {

                        if (iconRaindrop.isConstantSpeedEnd) {
                            iconRaindrop.isConstantSpeedEnd = false;
                        }

                        Vector3f tempPosition = iconRaindrop.getTempPostion();
                        ++iconRaindrop.mFrameCount;
                        this.accele.set(iconRaindrop.getAccele());

                        if (this.isFreeFalling) {
                            this.tempspeed.set(this.tempposition.x - tempPosition.x, 0f, 0f);
                        } else {
                            this.tempspeed.set(0f, 0f, 0f);
                        }

                        this.accele.scale((iconRaindrop.mFrameCount * iconRaindrop.mFrameCount) / 2f);
                        this.tempspeed.add(this.accele);
                        this.tempspeed.add(tempPosition);
                        this.tempposition.set(this.tempspeed);

                        if (this.isFreeFalling) {
                            this.tempposition.x += this.xOffset;
                            iconRaindrop.getRotation().z = (float) Math.atan(this.xOffset
                                    / (this.accele.y * iconRaindrop.mFrameCount));
                        }
                    }
                } else {
                    ++iconRaindrop.mFrameCount;
                    float delta = 2f * iconRaindrop.distance / (-iconRaindrop.getDspeed().y * iconRaindrop.timeSpend);
                    this.tempspeed.scale(iconRaindrop.mFrameCount * delta);
                    this.accele.set(0f, iconRaindrop.getDspeed().y * iconRaindrop.getDspeed().y
                            / (2f * iconRaindrop.distance), 0f);
                    this.accele.scale(iconRaindrop.mFrameCount * delta * iconRaindrop.mFrameCount * delta / 2f);
                    this.tempspeed.add(this.accele);
                    this.tempspeed.add(iconRaindrop.getInitialPosition());
                    this.tempposition.set(this.tempspeed);
                    if (iconRaindrop.mFrameCount == iconRaindrop.timeSpend) {
                        iconRaindrop.isConstantSpeedEnd = true;
                    }
                }

                if (this.tempposition.y < -this.mHeight / 2f || !this.isWorkspaceStatic()
                        && iconRaindrop.mSrcAlpha == 0f) {
                    this.mGLRainNowIcondrops.remove(i);
                    ArrayList<Integer> alist = this.mHas.get(Integer.valueOf(iconRaindrop.mIndex));
                    if (alist != null) {
                        for (int j = 0; j < alist.size(); ++j) {
                            if (alist.get(j).equals(Integer.valueOf((int) (iconRaindrop.getInitialPosition().x)))) {
                                alist.remove(j);
                                break;
                            }
                        }
                    }
                    --i;
                }
            }
        }

        if (this.mGLRainNowIcondrops.size() == 0 && this.isFreeFalling) {
            this.mHas.clear();
            this.isFreeFalling = false;
            this.xOffset = 0f;
        }
    }

    private void updateScreenRaindrops(long deltaTime) {
        for (int i = 0; i < SCREEN_DROP_NUM; ++i) {
            GLScreenRaindrop screenRainDrop = this.mGLRainScreendrops[i];
            Vector3f position = screenRainDrop.getPosition();
            float timePeriod = screenRainDrop.getTimePeriod();
            float time = screenRainDrop.getTime() + ((deltaTime));
            screenRainDrop.setTime(time);
            screenRainDrop.getAlpha();

            if (time > 1f * timePeriod) {
                if (RandomUtil.intRange(0, 10) > 7) {
                    this.initScreenRaindrops(i, this.mWidth, this.mHeight);
                }
                continue;
            }

            screenRainDrop.setAlpha(this.getAlpha(time, timePeriod));
            screenRainDrop.setScale(this.getScreenDropScale(time, timePeriod));
            Vector3f speed = screenRainDrop.getDspeed();
            speed.x = 0f;
            speed.y = this.getScreenDropSpeed(time, timePeriod);
            speed.z = 0f;
            position.add(speed);
            speed.set(speed);
        }
    }

    private void updateSmallIcondrops(long deltaTime, float xSpeed) {
        if (this.isWorkspaceStatic()) {
            this.initSmallIconList();
        }

        for (int i = 0; i < this.mGLSmallIconDrops.size(); ++i) {
            GLIconSmallRaindrop iconSmallRaindrop = this.mGLSmallIconDrops.get(i);

            if (this.mIconCallBack.isIconOnDrag()) {
                iconSmallRaindrop.setAlpha(0f);
                iconSmallRaindrop.setIsFreeFall(true);
                continue;
            }

            if (iconSmallRaindrop.getIsIconDropInvalid() && this.isWorkspaceStatic()) {
                this.initSmallIconDrop(i, this.mWidth, this.mHeight);
                continue;
            }

            Vector3f raindropPosition = iconSmallRaindrop.getPosition();
            Vector3f raindropEndPosition = iconSmallRaindrop.getEndPosition();
            Vector3f raindropHalfPosition = iconSmallRaindrop.getHalfPosition();
            float raindropAlpha = iconSmallRaindrop.getAlpha();
            boolean isFreeFall = iconSmallRaindrop.getIsFreeFall();
            Vector3f raindropSpeed = iconSmallRaindrop.getDspeed();

            if (!isFreeFall && (xSpeed > 2.1f || xSpeed < -2.1f) || (!this.isWorkspaceStatic() && !isFreeFall)) {
                iconSmallRaindrop.setIsFreeFall(true);
                iconSmallRaindrop.setAlpha(1f);
                isFreeFall = true;

                if (Math.abs(xSpeed) <= 40f) {
                    raindropSpeed.x = 0.3f * xSpeed;
                } else {
                    raindropSpeed.x = xSpeed / Math.abs(xSpeed) * 13f;
                }

                raindropSpeed.y = 0f;
            }

            if (!isFreeFall) {
                raindropSpeed.x = 0f;
            }

            if (isFreeFall) {
                raindropSpeed.y += -54f;
                raindropSpeed.z = 0f;
                raindropPosition.add(raindropSpeed);
                raindropSpeed.set(raindropSpeed);
                iconSmallRaindrop.setDspeed(raindropSpeed);
                iconSmallRaindrop.setAlpha(raindropAlpha - 0.017f);

                if (raindropAlpha < -1f && this.isWorkspaceStatic()) {
                    this.initSmallIconDrop(i, this.mWidth, this.mHeight);
                }
                continue;
            }

            if (raindropSpeed.y >= 0f || raindropPosition.y <= raindropHalfPosition.y) {
                if (raindropSpeed.y < 0f && raindropPosition.y < raindropHalfPosition.y
                        && raindropPosition.y > raindropEndPosition.y) {
                    iconSmallRaindrop.setAlpha(1f);
                    iconSmallRaindrop.setScale(0.8f + 0.2f * (raindropHalfPosition.y - raindropPosition.y)
                            / (raindropHalfPosition.y - raindropEndPosition.y));
                    iconSmallRaindrop.setWidth(this.iconRainWidth * iconSmallRaindrop.getScale());
                    iconSmallRaindrop.setHeight(this.iconRainHeight * iconSmallRaindrop.getScale());
                    iconSmallRaindrop.buildMesh();
                    raindropSpeed.y += 0.05f;

                    if (raindropSpeed.y > 0f) {
                        raindropSpeed.y = 0f;
                    }

                    raindropSpeed.z = 0f;
                    raindropPosition.add(raindropSpeed);
                    raindropSpeed.set(raindropSpeed);
                    iconSmallRaindrop.setDspeed(raindropSpeed);
                    continue;
                }

                if (raindropSpeed.y == 0f || raindropPosition.y < raindropEndPosition.y) {
                    iconSmallRaindrop.setAlpha(raindropAlpha - 0.02f);
                    if (raindropAlpha < 0f && this.isWorkspaceStatic()) {
                        this.initSmallIconDrop(i, this.mWidth, this.mHeight);
                    }
                    continue;
                }
            } else {
                iconSmallRaindrop.setAlpha(1f - (raindropPosition.y - raindropHalfPosition.y)
                        / (raindropHalfPosition.y - raindropEndPosition.y));
            }

            raindropSpeed.y = this.getSmallIconDropSpeed();
            raindropSpeed.z = 0f;
            raindropPosition.add(raindropSpeed);
            raindropSpeed.set(raindropSpeed);
            iconSmallRaindrop.setDspeed(raindropSpeed);
        }
    }
}
