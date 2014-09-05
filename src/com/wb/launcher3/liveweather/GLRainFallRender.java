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
import com.wb.launcher3.R;

public class GLRainFallRender extends LiveWeatherGLRender {
    class ScreenRainDropsAtts {
        Vector3f dspeed;
        Vector3f position;
        Vector3f rotation;
        float timePeriod;

        ScreenRainDropsAtts(GLRainFallRender arg2) {
            this.dspeed = new Vector3f();
            this.position = new Vector3f();
            this.timePeriod = 0f;
            this.rotation = new Vector3f();
        }
    }

    class SmallIconDropAttr {
        Vector3f dspeed;
        Vector3f endPosition;
        Vector3f halfPosition;
        Vector3f position;
        Vector3f rotation;

        SmallIconDropAttr(GLRainFallRender arg2) {
            this.dspeed = new Vector3f();
            this.position = new Vector3f();
            this.endPosition = new Vector3f();
            this.halfPosition = new Vector3f();
            this.rotation = new Vector3f();
        }
    }

    private static final float AMPLITUDE = 0f;
    private static final int BOTTOM_SPRAY_NUM = 50;
    private static final float DEFAULT_HEIGHT = 0f;
    private static final float DOCKBAR_HEIGHT_SCALE = 0.88f;
    private static final int EDGE_RESERVED = 12;
    private static final int ICON_DROP_NUM = 8;
    private static final int ICON_DROP_TYPES = 1;
    private static final int ICON_RAINDROP_HEIGHT = 56;
    private static final int ICON_RAINDROP_WIDTH = 40;
    public static final int ICON_RAIN_SPRAY_BALL = 0;
    public static final int ICON_RAIN_SPRAY_BALL_TIME = 240;
    public static final int ICON_RAIN_SPRAY_HALFBALL_LEFT = 1;
    public static final int ICON_RAIN_SPRAY_HALFBALL_RIGHT = 2;
    public static final int ICON_RAIN_SPRAY_HALFBALL_TIME = 480;
    private static final int ICON_SPRAY_NUM = 200;
    private static final int ICON_SPRAY_TYPES = 3;
    private static final int RAINDROP_NUM = 35;
    private static final int RAINDROP_TYPES = 2;
    private static final int SCREEN_DROP_NUM = 2;
    private static final int SCREEN_RAINDROP_TYPES = 3;
    private static final float SIZE_CHANGE_ZONE = 0f;
    private static final float SMALL_ICON_DROP_ACCELERATION = 0f;
    private static final int SMALL_ICON_DROP_NUM = 1;
    private static final int SMALL_ICON_DROP_TYPES = 1;
    private static final float SMALL_ICON_DROP_X_OFFSET_ADJUST_RATIO = 0f;
    private static final float SMALL_SPRAY_ROTATE_SPEED = 0.003272f;
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
    private HashMap<Integer, int[]> mGLIconInfo;
    private ArrayList<GLRainSpray> mGLRainNowBottomSprays;
    private ArrayList<GLRainSpray> mGLRainNowBottomSpraysSparing;
    private ArrayList<GLRainSpray> mGLRainNowIconSprays;
    private ArrayList<GLRainSpray> mGLRainNowIconSpraysSparing;
    private ArrayList<GLIconRaindrop> mGLRainNowIcondrops;
    private GLScreenRaindrop[] mGLRainScreendrops;
    private GLSmallRaindrop[] mGLRaindrops;
    private ArrayList<GLIconSmallRaindrop> mGLSmallIconDrops;
    private HashMap<Integer, ArrayList<Integer>> mHas;
    private int[] mIconDropTextureAsserts;
    private TextureInfo[] mIconDropTextureInfos;
    private int[] mIconSprayTextureAsserts;
    private float mLastOffset;
    private boolean mNeedUpdateInfo;
    private Random mRandom;
    private int[] mScreenDropTextureAsserts;
    private TextureInfo[] mScreenDropTextureInfos;
    private ScreenRainDropsAtts mScreenRainDropsAtts;
    private SmallIconDropAttr mSmallIconDropAttr;
    private int[] mSmallIconDropTextureAsserts;
    private TextureInfo[] mSmallIconDropTextureInfos;
    private int mSmallIconNum;
    private TextureInfo[] mSmallIconSprayTextureInfos;
    private long mSpraysBeginTime;
    private int[] mTextureAsserts;
    private TextureInfo[] mTextureInfos;
    Vector3f temSpeed;
    Vector3f tempposition;
    Vector3f tempspeed;
    private float xOffset;

    GLRainFallRender(Context context, LiveWeatherGLView liveWeatherGLView) {
        super(context, liveWeatherGLView);

        this.mLastOffset = 0f;
        this.mHas = new HashMap<Integer, ArrayList<Integer>>();
        this.iconRainWidth = 0f;
        this.iconRainHeight = 0f;
        this.iconHeight = 0;
        this.mRandom = new Random();
        this.mAccele = new Vector3f(0f, -10f, 0f);
        this.mGLRainNowIcondrops = new ArrayList<GLIconRaindrop>();
        this.mGLRainNowIconSprays = new ArrayList<GLRainSpray>();
        this.mGLRainNowBottomSprays = new ArrayList<GLRainSpray>();
        this.mGLRainNowIconSpraysSparing = new ArrayList<GLRainSpray>();
        this.mGLRainNowBottomSpraysSparing = new ArrayList<GLRainSpray>();
        this.mGLSmallIconDrops = new ArrayList<GLIconSmallRaindrop>();
        this.mGLIconInfo = new HashMap<Integer, int[]>();
        this.mTextureInfos = new TextureInfo[RAINDROP_TYPES];
        this.mScreenDropTextureInfos = new TextureInfo[SCREEN_RAINDROP_TYPES];
        this.mIconDropTextureInfos = new TextureInfo[ICON_DROP_TYPES];
        this.mSmallIconDropTextureInfos = new TextureInfo[SMALL_ICON_DROP_TYPES];
        this.mSmallIconSprayTextureInfos = new TextureInfo[ICON_SPRAY_TYPES];
        this.isFreeFalling = false;
        this.mNeedUpdateInfo = true;
        this.mSpraysBeginTime = SystemClock.uptimeMillis();
        this.xOffset = 0f;
        this.tempposition = null;
        this.tempspeed = new Vector3f();
        this.accele = new Vector3f();
        this.temSpeed = new Vector3f();
        this.mTextureAsserts = new int[] { R.drawable.raindrop_0, R.drawable.raindrop_1 };
        this.mScreenDropTextureAsserts = new int[] { R.drawable.drops_screen1, R.drawable.drops_screen2,
                R.drawable.drops_screen3 };
        this.mSmallIconDropTextureAsserts = new int[] { R.drawable.small_icon_rain };
        this.mIconDropTextureAsserts = new int[] { R.drawable.drops_icon1 };
        this.mIconSprayTextureAsserts = new int[] { R.drawable.sprays_icon1, R.drawable.sprays_icon2,
                R.drawable.sprays_icon3 };
        this.mSmallIconDropAttr = new SmallIconDropAttr(this);
        this.mScreenRainDropsAtts = new ScreenRainDropsAtts(this);
        this.mGLRaindrops = new GLSmallRaindrop[RAINDROP_NUM];
        this.mSmallIconNum = this.getSmallIconNum();
        this.mGLRainScreendrops = new GLScreenRaindrop[SCREEN_DROP_NUM];
        this.mSpraysBeginTime = SystemClock.uptimeMillis();

        for (int i = 0; i < RAINDROP_NUM; i++) {
            this.mGLRaindrops[i] = new GLSmallRaindrop();
        }

        for (int i = 0; i < SCREEN_DROP_NUM; i++) {
            this.mGLRainScreendrops[i] = new GLScreenRaindrop();
        }

        for (int i = 0; i < ICON_SPRAY_NUM; i++) {
            this.mGLRainNowIconSpraysSparing.add(new GLRainSpray());
        }

        for (int i = 0; i < BOTTOM_SPRAY_NUM; i++) {
            this.mGLRainNowBottomSpraysSparing.add(new GLRainSpray());
        }
    }

    private void changeSmallIconDropStateToFreeFall() {
        synchronized (this) {
            for (int i = 0; i < this.mSmallIconNum; ++i) {
                this.mGLSmallIconDrops.get(i).setIsFreeFall(true);
            }
        }
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
        raindrop.timeSpend = (int) (distance / this.iconHeight / 2 * 60f);
        if (raindrop.timeSpend < 10) {
            raindrop.timeSpend = 30;
        }

        raindrop.buildMesh();
        raindrop.setTextureId(this.mIconDropTextureInfos[0].mId);
        raindrop.mIndex = index;
        return raindrop;
    }

    private float getAlpha(float time, float timePeriod) {
        if (time >= timePeriod * 0.5f) {
            if (time - timePeriod > -timePeriod * 3f / 8f) {
                return 0;
            } else {
                return 1 - (time - timePeriod * 0.5f) / (0.125f * timePeriod);
            }
        }

        return 1;
    }

    @Override
    public float getOffset() {
        synchronized (this.mLock) {
            if (this.mOffset >= 1f || this.mOffset <= -1f) {
                this.mIsUpdatedOffset = true;
                this.mOffset *= 0.9f;
                return this.mOffset;
            } else {
                this.mOffset = 0f;
                this.mIsUpdatedOffset = true;
                return 0;
            }
        }
    }

    private float getScreenDropSpeed(float time, float timePeriod) {
        if (time < 0.75f * timePeriod) {
            return (float) (-4 * (Math.sin(6.283185 / timePeriod * time) + 1) * this.mHeight / 1920);
        }

        return 0;
    }

    private float getSmallIconDropAlpha() {
        return 1f;
    }

    private float getSmallIconDropSpeed() {
        return -1.2f;
    }

    private int getSmallIconNum() {
        int num = 1;

        if (this.mIconCallBack != null) {
            num = this.mIconCallBack.getIconNum();
        }

        if (num == 0) {
            num = 1;
        }

        return num;
    }

    private void initRaindrops(int width, int height) {
        for (int i = 0; i < RAINDROP_NUM; ++i) {
            this.mGLRaindrops[i].setRainDropAlgorithm(width, height);

            if (i < 20) {
                this.mGLRaindrops[i].setTagRainDropDepth(0);
            } else if (i < 40) {
                this.mGLRaindrops[i].setTagRainDropDepth(2);
            } else {
                this.mGLRaindrops[i].setTagRainDropDepth(1);
            }

            float v1 = 0.095f * ((height));
            int v2 = this.mGLRaindrops[i].randomTextureValue();
            this.mGLRaindrops[i].setWidth(0.0075f * ((width)));
            this.mGLRaindrops[i].setHeight(v1);
            this.mGLRaindrops[i].setAdaptRotateHeight(v1);
            this.mGLRaindrops[i].buildMesh();
            this.mGLRaindrops[i].setTextureId(this.mTextureInfos[v2].mId);
        }

        for (int i = 0; i < SCREEN_DROP_NUM; ++i) {
            this.initScreenRaindrops(i, width, height);
        }
    }

    private void initScreenRaindrops(int whichone, int width, int height) {
        int textureIndex;

        if (this.mIconCallBack != null && !this.mIconCallBack.isToggleBarOpen()) {
            this.mScreenRainDropsAtts.dspeed.x = 0f;
            this.mScreenRainDropsAtts.dspeed.y = -4f;
            this.mScreenRainDropsAtts.dspeed.z = 0f;
            this.mScreenRainDropsAtts.position.x = RandomUtil.floatRange(width / 2 * (whichone + 0.1f) - 0.5f * width,
                    width / 2 * (whichone + 0.9f) - 0.5f * width);
            this.mScreenRainDropsAtts.position.y = RandomUtil.floatRange(height * 0.6f, height * 0.8f);
            this.mScreenRainDropsAtts.position.z = 2f;
            this.mScreenRainDropsAtts.timePeriod = RandomUtil.floatRange(9600f, 17280f);
            this.mScreenRainDropsAtts.rotation.x = 0f;
            this.mScreenRainDropsAtts.rotation.y = 0f;
            this.mScreenRainDropsAtts.rotation.z = -this.mScreenRainDropsAtts.dspeed.x * 0.5f;

            if (RandomUtil.flipCoin()) {
                textureIndex = RandomUtil.intRange(1, 3);
            } else {
                textureIndex = RandomUtil.intRange(0, 3);
            }

            this.mGLRainScreendrops[whichone].setAlpha(1f);
            this.mGLRainScreendrops[whichone].setDspeed(this.mScreenRainDropsAtts.dspeed);
            this.mGLRainScreendrops[whichone].setPosition(this.mScreenRainDropsAtts.position);
            this.mGLRainScreendrops[whichone].setTimePeriod(this.mScreenRainDropsAtts.timePeriod);
            this.mGLRainScreendrops[whichone].setTime(0f);
            this.mGLRainScreendrops[whichone].setRotation(this.mScreenRainDropsAtts.rotation);
            this.mGLRainScreendrops[whichone].setImageHeight(this.mScreenDropTextureInfos[textureIndex].mHeight);
            this.mGLRainScreendrops[whichone].setWidth(this.mScreenDropTextureInfos[textureIndex].mWidth / 1920f
                    * height);
            this.mGLRainScreendrops[whichone].setHeight(this.mScreenDropTextureInfos[textureIndex].mHeight / 1920f
                    * height);
            this.mGLRainScreendrops[whichone].buildMesh();
            this.mGLRainScreendrops[whichone].setTextureId(this.mScreenDropTextureInfos[textureIndex].mId);
        }
    }

    private void initSmallIconDrop(int whichone, int width, int height) {
        boolean isValid;
        float scale = 0.8f;
        this.mSmallIconDropAttr.dspeed.x = 0f;
        this.mSmallIconDropAttr.dspeed.y = -54f;
        this.mSmallIconDropAttr.dspeed.z = 0f;
        this.mSmallIconDropAttr.position.x = RandomUtil.floatRange(width * -0.5f, width * 0.5f);
        this.mSmallIconDropAttr.position.y = RandomUtil.floatRange(height * -0.5f, height * 0.5f);
        this.mSmallIconDropAttr.position.z = 0f;
        int x = (int) (this.mSmallIconDropAttr.position.x + width * 0.5f);
        int y = -(int) (this.mSmallIconDropAttr.position.y - height * 0.5f);

        if (this.mIconCallBack != null) {
            int[] edge = this.mIconCallBack.getEdgeForPos(x, y);
            if (edge == null || edge[0] == 0 || edge[1] == 0 || edge[2] == 0 || edge[3] == 0) {
                isValid = true;
            } else {
                this.mSmallIconDropAttr.endPosition.x = this.mSmallIconDropAttr.position.x;
                this.mSmallIconDropAttr.endPosition.y = this.mSmallIconDropAttr.position.y - edge[3] + 4f
                        + this.mSmallIconDropTextureInfos[0].mHeight;
                this.mSmallIconDropAttr.endPosition.z = this.mSmallIconDropAttr.position.z;
                this.mSmallIconDropAttr.halfPosition.x = this.mSmallIconDropAttr.position.x;
                this.mSmallIconDropAttr.halfPosition.y = (this.mSmallIconDropAttr.endPosition.y + this.mSmallIconDropAttr.position.y) / 2f;
                this.mSmallIconDropAttr.halfPosition.z = this.mSmallIconDropAttr.position.z;
                if (edge[0] < 12) {
                    this.mSmallIconDropAttr.position.x += 12f;
                } else if (edge[2] < 12) {
                    this.mSmallIconDropAttr.position.x -= 12f;
                }

                if (this.mSmallIconDropAttr.endPosition.y <= this.mSmallIconDropAttr.position.y) {
                    isValid = false;
                } else {
                    isValid = true;
                }
            }

            this.mSmallIconDropAttr.rotation.x = 0f;
            this.mSmallIconDropAttr.rotation.y = 0f;
            this.mSmallIconDropAttr.rotation.z = -this.mSmallIconDropAttr.dspeed.x * 0.5f;

            GLIconSmallRaindrop raindrop = this.mGLSmallIconDrops.get(whichone);
            raindrop.setAlpha(0f);
            raindrop.setDspeed(this.mSmallIconDropAttr.dspeed);
            raindrop.setIsFreeFall(false);
            raindrop.setIsIconDropInvalid(isValid);
            raindrop.setPosition(this.mSmallIconDropAttr.position);
            raindrop.setEndPosition(this.mSmallIconDropAttr.endPosition);
            raindrop.setHalfPosition(this.mSmallIconDropAttr.halfPosition);
            raindrop.setScale(scale);
            raindrop.setRotation(this.mSmallIconDropAttr.rotation);
            raindrop.setWidth(this.mSmallIconDropTextureInfos[0].mWidth * scale / 1920f * height);
            raindrop.setHeight(this.mSmallIconDropTextureInfos[0].mHeight * scale / 1920f * height);
            raindrop.buildMesh();
            raindrop.setTextureId(this.mSmallIconDropTextureInfos[0].mId);
        }
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

    private void initSprayData(GLRainSpray spray, long startTime, int type, float width, float height, float genHeight,
            Vector3f position) {
        switch (type) {
        case 0:
            break;
        case 1:
        case 2:
            spray.setStartPosition(position);
            float speedY = 2f * genHeight / 480f;
            float speedX = type == 1 ? -0.5f * speedY : 0.5f * speedY;
            spray.setDspeed(new Vector3f(speedX, speedY, 0f));
            spray.mAccelerate = -2f * genHeight / 480f / 480f;
            float speedZ = type == 1 ? -1f : 1f;
            spray.setRspeed(new Vector3f(0f, 0f, speedZ * SMALL_SPRAY_ROTATE_SPEED));
            speedZ = type == 1 ? 0.25f : -0.25f;
            spray.mStartRotation = (float) (speedZ * Math.PI);
            break;
        }

        spray.mStartTime = startTime;
        spray.setWidth(1.2f * width);
        spray.setHeight(1.2f * height);
        spray.buildMesh();
        spray.setTextureId(this.mSmallIconSprayTextureInfos[type].mId);
        spray.setPosition(position);
        spray.setAlpha(0f);
        spray.mType = type;
    }

    private void initTexture(GL10 gl) {
        int i;

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

        for (i = 0; i < ICON_SPRAY_TYPES; ++i) {
            this.mSmallIconSprayTextureInfos[i] = this.loadTexture2(gl, this.mIconSprayTextureAsserts[i]);
        }
    }

    private boolean isWorkspaceStatic() {
        if (this.mIconCallBack == null) {
            Log.e("GLRainFallRender", "error mIconCallBack = null");
            return false;
        } else if ((this.mIconCallBack.isSteadyState()) && !this.mIconCallBack.isToggleBarOpen()
                && !this.mIconCallBack.isInScrollState()) {
            return true;
        }

        return false;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        int renderDelay;
        long uptime = SystemClock.uptimeMillis();

        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        float offset = this.getOffset();

        for (int i = 0; i < this.mGLSmallIconDrops.size(); ++i) {
            GLIconSmallRaindrop rainDrop = this.mGLSmallIconDrops.get(i);

            if (!rainDrop.getIsIconDropInvalid()) {
                rainDrop.onDraw(gl, this.mGLAlpha);
            }
        }

        for (int i = 0; i < RAINDROP_NUM; ++i) {
            if (this.checkInViewport(this.mGLRaindrops[i], this.mWidth / 2f, this.mHeight / 2f)) {
                this.mGLRaindrops[i].setSmallRainScreenOffsetX(offset);
                this.mGLRaindrops[i].onDraw(gl, this.mGLAlpha);
            }
        }

        for (int i = 0; i < SCREEN_DROP_NUM; ++i) {
            if (this.checkInViewport(this.mGLRainScreendrops[i], this.mWidth / 2f, this.mHeight / 2f)) {
                this.mGLRainScreendrops[i].onDraw(gl, this.mGLAlpha);
            }
        }

        for (int i = 0; i < this.mGLRainNowIconSprays.size(); ++i) {
            this.mGLRainNowIconSprays.get(i).onDraw(gl, this.mGLAlpha);
        }

        for (int i = 0; i < this.mGLRainNowBottomSprays.size(); ++i) {
            this.mGLRainNowBottomSprays.get(i).onDraw(gl, this.mGLAlpha);
        }

        int iconRainDropNum = this.mGLRainNowIcondrops.size();
        ArrayList<Rect> v6 = null;
        if (this.mIconCallBack != null) {
            ArrayList<Rect> iconRectList = this.mIconCallBack.getIconRects();
            if (iconRectList != null) {
                int iconRectNum = iconRectList.size();

                if (iconRainDropNum < 8 && iconRectNum != 0 && this.isWorkspaceStatic() && !this.isFreeFalling) {
                    int randomIdex = RandomUtil.intRange(0, iconRectNum);
                    Rect iconRect = iconRectList.get(randomIdex);
                    this.iconHeight = iconRect.bottom - iconRect.top;
                    int randomX = RandomUtil.intRange(iconRect.left, iconRect.right);
                    int randomY = RandomUtil.intRange(iconRect.top, iconRect.bottom);
                    int[] reversedXY = this.changeXYReverse(randomX, randomY);
                    int[] edge = this.mIconCallBack.getEdgeForPos(randomX, randomY);

                    if (edge != null && RandomUtil.intRange(0, (int) (3000f / (iconRectNum * 45)) + 1) < 1) {
                        int v21 = 1;
                        ArrayList<Integer> v11 = this.mHas.get(Integer.valueOf(randomIdex));
                        if (edge[0] < this.iconRainWidth / 2f || edge[1] < this.iconRainHeight / 2f
                                || edge[2] < this.iconRainWidth / 2f || edge[3] < this.iconRainHeight / 2f) {
                            v21 = 0;
                        }

                        if (v11 != null && v21 == 1) {
                            for (int i = 0; i < v11.size(); ++i) {
                                if (reversedXY[0] > v11.get(i).intValue() - this.iconRainWidth
                                        && reversedXY[0] < v11.get(i).intValue() + this.iconRainWidth) {
                                    v21 = 0;
                                    break;
                                }
                            }
                        }

                        if (v21 != 0) {
                            this.mGLRainNowIcondrops.add(this.generateRaindrop(randomIdex, this.mWidth, this.mHeight,
                                    reversedXY[0], reversedXY[1], edge[3]));
                            if (v11 == null) {
                                ArrayList<Integer> v11_1 = new ArrayList<Integer>();
                                v11_1.add(Integer.valueOf(reversedXY[0]));
                                this.mHas.put(Integer.valueOf(randomIdex), v11_1);
                            } else {
                                v11.add(Integer.valueOf(reversedXY[0]));
                            }

                            v6 = iconRectList;
                            ++iconRainDropNum;
                        }
                    }
                }
            }

            v6 = iconRectList;
        }

        for (int i = 0; i < iconRainDropNum; ++i) {
            this.mGLRainNowIcondrops.get(i).onDraw(gl, this.mGLAlpha);
        }

        long drawtime = this.updateDrawTime(45);
        this.updateSmallIcondrops(drawtime, offset);
        this.updateScreenRaindrops(drawtime);
        this.updateIconRaindrops(drawtime, offset);

        if (uptime - this.mSpraysBeginTime > 1400) {
            this.updateIconRainSprays(v6, drawtime, uptime);
            this.updateBottomRainSprays(drawtime, uptime);
        }

        this.mLastOffset = this.getOffset();
        renderDelay = Math.max(0, (int) (45 - (SystemClock.uptimeMillis() - uptime)));

        this.requestRenderDelayed(renderDelay);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.initSmallIconList();

        if (this.mWidth != width || this.mHeight != height) {
            if (this.isWorkspaceStatic() && !this.mIconCallBack.isIconOnDrag()) {
                for (int i = 0; i < this.mGLSmallIconDrops.size(); ++i) {
                    this.initSmallIconDrop(i, width, height);
                }
            }

            this.initRaindrops(width, height);
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
            int i = this.mGLSmallIconDrops.size();
            for (int j = 0; j < i; j++)
                this.mGLSmallIconDrops.remove(-1 + (i - j));
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

    private void updateBottomRainSprays(long deltaTime, long time) {
        int type;
        int[] reversedXY;
        float genHeight;
        GLRainSpray spray;

        for (int i = 0; i < this.mGLRainNowBottomSprays.size(); ++i) {
            spray = this.mGLRainNowBottomSprays.get(i);
            switch (spray.mType) {
            case 0:
                if (time - spray.mStartTime <= ICON_RAIN_SPRAY_BALL_TIME) {
                    spray.updateData(time - spray.mStartTime, ICON_RAIN_SPRAY_BALL_TIME);
                } else {
                    this.mGLRainNowBottomSprays.remove(i);
                    this.mGLRainNowBottomSpraysSparing.add(spray);
                    --i;
                }
                break;
            case 1:
            case 2:
                if (time - spray.mStartTime <= ICON_RAIN_SPRAY_HALFBALL_TIME) {
                    spray.updateData(time - spray.mStartTime, ICON_RAIN_SPRAY_HALFBALL_TIME);
                } else {
                    this.mGLRainNowBottomSprays.remove(i);
                    this.mGLRainNowBottomSpraysSparing.add(spray);
                    --i;
                }
                break;
            }
        }

        //*/zhangwuba modify 2014-8-18 for our lancher
        if (/*!this.mIconCallBack.isToggleBarOpen() && !this.mIconCallBack.isCurrentPrivatePage()
                && */this.mGLRainNowBottomSpraysSparing.size() > 1) {
            float sprayWidth = (this.mSmallIconSprayTextureInfos[0].mWidth * this.mWidth / 1080);
            float sprayHeight = (this.mSmallIconSprayTextureInfos[0].mHeight * this.mHeight / 1920);
            float width = (this.mSmallIconSprayTextureInfos[1].mWidth * this.mWidth / 1080);
            float height = (this.mSmallIconSprayTextureInfos[1].mHeight * this.mHeight / 1920);
            float v19 = 0f;
            while (sprayWidth / 2f + v19 < this.mWidth) {
                if (this.mGLRainNowBottomSpraysSparing.size() <= 1) {
                    break;
                }

                genHeight = RandomUtil.floatRange(sprayHeight, 5f * sprayHeight);
                v19 += RandomUtil.floatRange(sprayWidth / 2f, (this.mWidth - sprayWidth / 2f) / 2f);
                reversedXY = this.changeXYReverse(((int) v19), ((int) (((this.mHeight)) - genHeight)));
                spray = this.mGLRainNowBottomSpraysSparing.get(0);
                this.initSprayData(spray, time, 0, sprayWidth, sprayHeight, genHeight, new Vector3f(reversedXY[0],
                        reversedXY[1], 0f));
                this.mGLRainNowBottomSprays.add(spray);
                this.mGLRainNowBottomSpraysSparing.remove(0);
            }

            float x = RandomUtil.floatRange(2f * width, this.mWidth - 2f * width);
            if (width / 2f + x >= this.mWidth) {
                return;
            }

            genHeight = RandomUtil.floatRange(height, 2f * height);

            if (RandomUtil.flipCoin()) {
                type = 1;
            } else {
                type = 2;
            }

            reversedXY = this.changeXYReverse(((int) x), this.mHeight);
            spray = this.mGLRainNowBottomSpraysSparing.get(0);
            this.initSprayData(spray, time, type, width, height, genHeight, new Vector3f(reversedXY[0], reversedXY[1],
                    0f));
            this.mGLRainNowBottomSprays.add(spray);
            this.mGLRainNowBottomSpraysSparing.remove(0);
        }
    }

    private void updateIconRainSprays(ArrayList iconRectList, long deltaTime, long time) {
        int type;
        int[] v19;
        float v2;
        float v5;
        int v4;
        float v9;
        int v26;
        GLRainSpray spray;
        int v27;

        if (iconRectList != null) {
            if (!this.isWorkspaceStatic() || this.mIconCallBack.isIconOnDrag()) {
                v27 = 0;
            } else {
                v27 = 1;
            }

            int iconNum = this.mIconCallBack.getIconNum();
            if (!this.isWorkspaceStatic() || (this.mIconCallBack.isIconOnDrag()) || iconNum != this.mGLIconInfo.size()) {
                this.mNeedUpdateInfo = true;
            }

            if (this.mNeedUpdateInfo && v27 != 0) {
                for (int i = 0; i < this.mIconCallBack.getIconNum(); ++i) {
                    this.mGLIconInfo.put(Integer.valueOf(i), this.mIconCallBack.getUpperEdgeForIcon(i));
                }

                this.mNeedUpdateInfo = false;
            }

            for (int i = 0; i < this.mGLRainNowIconSprays.size(); ++i) {
                spray = this.mGLRainNowIconSprays.get(i);
                switch (spray.mType) {
                case 0:
                    if (time - spray.mStartTime > ICON_RAIN_SPRAY_BALL_TIME) {
                        spray.updateData(time - spray.mStartTime, ICON_RAIN_SPRAY_BALL_TIME);
                    } else {
                        this.mGLRainNowIconSprays.remove(i);
                        this.mGLRainNowIconSpraysSparing.add(spray);
                        --i;
                    }
                    break;
                case 1:
                case 2:
                    if (time - spray.mStartTime > ICON_RAIN_SPRAY_HALFBALL_TIME) {
                        spray.updateData(time - spray.mStartTime, ICON_RAIN_SPRAY_HALFBALL_TIME);
                    } else {
                        this.mGLRainNowIconSprays.remove(i);
                        this.mGLRainNowIconSpraysSparing.add(spray);
                        --i;
                    }
                    break;
                }
            }

            if (this.mNeedUpdateInfo) {
                return;
            }

            float width1 = (this.mSmallIconSprayTextureInfos[0].mWidth * this.mWidth / 1080);
            float height1 = (this.mSmallIconSprayTextureInfos[0].mHeight * this.mHeight / 1920);
            float width2 = (this.mSmallIconSprayTextureInfos[1].mWidth * this.mWidth / 1080);
            float height2 = (this.mSmallIconSprayTextureInfos[1].mHeight * this.mHeight / 1920);
            iconNum = iconRectList.size();
            for (int i = 0; i < iconNum; ++i) {
                int[] iconInfo = this.mGLIconInfo.get(Integer.valueOf(i));
                if (iconInfo != null) {
                    Rect rect = (Rect) iconRectList.get(i);
                    int iconWidth = rect.right - rect.left;

                    if (rect.bottom > this.mHeight * DOCKBAR_HEIGHT_SCALE) {
                        v26 = 1;
                    } else {
                        v26 = 0;
                    }

                    if (this.mGLRainNowIconSpraysSparing.size() <= 1) {
                        continue;
                    }

                    float v20 = RandomUtil.floatRange(width1 / 2f, iconWidth - width1 / 2f);
                    if (width1 / 2f + v20 < iconInfo.length) {
                        v9 = RandomUtil.floatRange(height1, 5f * height1);
                        //Log.i("myl","zhangwuba ----------- v21 = " + v20 + " width1 / 2f = " + (width1 / 2f) 
                        		//+ " v21 - width2 / 2f = " + (v20 - width1 / 2f));
                        if (((int) (v20 - width1 / 2f)) > 0
                                && (int)v20 > 0 && (int)(width1 / 2f) > 0
                                && iconInfo[((int) v20)] - iconInfo[((int) (v20 - width1 / 2f))] < v9
                                && (iconInfo[(int) (width1 / 2f + v20)] - iconInfo[((int) v20)]) < v9
                                && iconInfo[(int) v20] != -1) {
                            v4 = (int) (rect.left + v20);
                            v5 = iconInfo[(int) v20] + rect.top;
                            if (v26 != 0) {
                                v2 = v9 - height1 / 1920f * this.mHeight;
                            } else {
                                v2 = -v9 + height1 / 1920f * this.mHeight;
                            }

                            v19 = this.changeXYReverse(v4, ((int) (v2 + v5)));
                            spray = this.mGLRainNowIconSpraysSparing.get(0);
                            this.initSprayData(spray, time, 0, width1, height1, v9, new Vector3f((v19[0]),
                                    (v19[1]), 0f));
                            this.mGLRainNowIconSprays.add(spray);
                            this.mGLRainNowIconSpraysSparing.remove(0);
                        }
                    }

                    float v21 = RandomUtil.floatRange(width2 / 2f, (iconWidth * 6));
                    if (width2 / 2f + v21 >= ((iconInfo.length))) {
                        continue;
                    }

                    v9 = RandomUtil.floatRange(height2, 2.5f * height2);
                    if (v21 + v9 >= ((iconInfo.length))
                            || ((iconInfo[((int) v21)] - iconInfo[((int) (v21 + v9))])) < v9) {
                        if (v21 - v9 >= 0f && ((iconInfo[((int) v21)] - iconInfo[((int) (v21 - v9))])) >= v9
                                || !RandomUtil.flipCoin()) {
                            type = 2;
                        } else {
                            type = 1;
                        }
                    } else {
                        if (v21 - v9 >= 0f && ((iconInfo[((int) v21)] - iconInfo[((int) (v21 - v9))])) >= v9) {
                            continue;
                        }

                        type = 1;
                    }
                    //Log.i("myl","zhangwuba ----------- v21 = " + v21 + " width2 / 2f = " + (width2 / 2f) 
                    		//+ " v21 - width2 / 2f = " + (v21 - width2 / 2f));

                    if (((int) (v21 - width2 / 2f)) > 0
                            && ((int) v21) > 0 && ((int) width2 / 2f) > 0
                            &&((iconInfo[((int) v21)] - iconInfo[((int) (v21 - width2 / 2f))])) < v9
                            && ((iconInfo[((int) (width2 / 2f + v21))] - iconInfo[((int) v21)])) < v9
                            && iconInfo[((int) v21)] != -1) {
                        v4 = ((int) (((rect.left)) + v21));
                        v5 = (iconInfo[((int) v21)] + rect.top);
                        if (v26 != 0) {
                            v2 = v9;
                        } else {
                            v2 = width2 / 2f;
                        }

                        v19 = this.changeXYReverse(v4, ((int) (v2 + v5)));
                        spray = this.mGLRainNowIconSpraysSparing.get(0);
                        this.initSprayData(spray, time, type, width2, height2, v9, new Vector3f(
                                (v19[0]), (v19[1]), 0f));
                        this.mGLRainNowIconSprays.add(spray);
                        this.mGLRainNowIconSpraysSparing.remove(0);
                    }
                }
            }
        }
    }

    private void updateIconRaindrops(long deltaTime, float xSpeed) {
        for (int i = 0; i < this.mGLRainNowIcondrops.size(); ++i) {
            if (this.mIconCallBack.isIconOnDrag()) {
                this.mGLRainNowIcondrops.clear();
                break;
            }

            if (!this.isWorkspaceStatic() && this.xOffset != 0f) {
                this.isFreeFalling = true;
                if (Math.abs(xSpeed) <= 30f) {
                    this.xOffset = 0f;
                } else {
                    this.xOffset = xSpeed / Math.abs(xSpeed) * 15f;
                }
            }

            GLIconRaindrop rainDrop = this.mGLRainNowIcondrops.get(i);
            this.tempposition = rainDrop.getPosition();
            this.tempspeed.set(rainDrop.getDspeed());
            this.temSpeed = rainDrop.getInitialSpeed();
            if (this.distanceY(this.tempposition.y, rainDrop.getInitialPosition().y) >= rainDrop.distance
                    || rainDrop.isConstantSpeedEnd) {
                if (rainDrop.scale >= 2f || this.isFreeFalling) {
                    if (rainDrop.isConstantSpeedEnd) {
                        rainDrop.isConstantSpeedEnd = false;
                    }

                    Vector3f tempPosition = rainDrop.getTempPostion();
                    ++rainDrop.mFrameCount;
                    this.accele.set(rainDrop.getAccele());
                    if (!this.isFreeFalling) {
                        this.tempspeed.set(0f, 0f, 0f);
                    } else {
                        this.tempspeed.set(this.tempposition.x - tempPosition.x, 0f, 0f);
                    }

                    this.accele.scale(rainDrop.mFrameCount * rainDrop.mFrameCount / 2f);
                    this.tempspeed.add(this.accele);
                    this.tempspeed.add(tempPosition);
                    this.tempposition.set(this.tempspeed);
                    if (this.isFreeFalling) {
                        this.tempposition.x += this.xOffset;
                        rainDrop.getRotation().z = (float) Math.atan(this.xOffset
                                / (this.accele.y * rainDrop.mFrameCount));
                    }
                } else {
                    rainDrop.setTempPostion(this.tempposition);
                    rainDrop.mSrcAlpha += 0.01f;
                    rainDrop.scale += 0.01f;
                    rainDrop.setWidth(this.iconRainWidth / 2f * rainDrop.scale);
                    rainDrop.setHeight(this.iconRainHeight / 2f * rainDrop.scale);
                    rainDrop.buildMesh();
                    rainDrop.mFrameCount = 0;
                }
            } else {
                ++rainDrop.mFrameCount;
                float delta = 2f * rainDrop.distance / (-rainDrop.getDspeed().y * rainDrop.timeSpend);
                this.tempspeed.scale(rainDrop.mFrameCount * delta);
                this.accele.set(0f, rainDrop.getDspeed().y * rainDrop.getDspeed().y / (2f * rainDrop.distance), 0f);
                this.accele.scale(rainDrop.mFrameCount * delta * rainDrop.mFrameCount * delta / 2f);
                this.tempspeed.add(this.accele);
                this.tempspeed.add(rainDrop.getInitialPosition());
                this.tempposition.set(this.tempspeed);

                if (rainDrop.mFrameCount == rainDrop.timeSpend) {
                    rainDrop.isConstantSpeedEnd = true;
                }
            }

            if (this.tempposition.y < ((-this.mHeight / 2)) || !this.isWorkspaceStatic()
                    && rainDrop.mSrcAlpha == 0f) {
                this.mGLRainNowIcondrops.remove(i);
                ArrayList<Integer> a = this.mHas.get(Integer.valueOf(rainDrop.mIndex));
                if (a != null) {
                    for (int j = 0; j < a.size(); ++j) {
                        if (a.get(j).equals(Integer.valueOf((int) rainDrop.getInitialPosition().x))) {
                            a.remove(j);
                            break;
                        }
                    }
                }
                --i;
            }
        }

        if (this.mGLRainNowIcondrops.size() != 0) {
            return;
        }

        if (!this.isFreeFalling) {
            return;
        }

        this.mHas.clear();
        this.isFreeFalling = false;
        this.xOffset = 0f;
    }

    private void updateScreenRaindrops(long deltaTime) {
        for (int i = 0; i < SCREEN_DROP_NUM; ++i) {
            GLScreenRaindrop screenRainDrop = this.mGLRainScreendrops[i];
            Vector3f position = screenRainDrop.getPosition();

            if (position.y - screenRainDrop.getImageHeight() > 0.5 * this.mHeight
                    && (this.mIconCallBack == null || this.mIconCallBack.isToggleBarOpen())) {
                this.initScreenRaindrops(i, this.mWidth, this.mHeight);
                continue;
            }

            float timePeriod = screenRainDrop.getTimePeriod();
            float time = screenRainDrop.getTime() + deltaTime;

            screenRainDrop.setTime(time);
            screenRainDrop.getAlpha();

            if (time > 0.625f * timePeriod) {
                this.initScreenRaindrops(i, this.mWidth, this.mHeight);
            } else {
                screenRainDrop.setAlpha(this.getAlpha(time, timePeriod));
                Vector3f speed = screenRainDrop.getDspeed();
                speed.x = 0f;
                speed.y = this.getScreenDropSpeed(time, timePeriod);
                speed.z = 0f;
                position.add(speed);
                speed.set(speed);
            }
        }
    }

    private void updateSmallIcondrops(long deltaTime, float xSpeed) {
        if (this.isWorkspaceStatic()) {
            this.initSmallIconList();
        }

        for (int i = 0; i < this.mGLSmallIconDrops.size(); ++i) {
            GLIconSmallRaindrop smallRainDrop = this.mGLSmallIconDrops.get(i);

            if (this.mIconCallBack != null && this.mIconCallBack.isIconOnDrag()) {
                smallRainDrop.setAlpha(0f);
                smallRainDrop.setIsFreeFall(true);
            } else {
                if (smallRainDrop.getIsIconDropInvalid() && this.isWorkspaceStatic()) {
                    this.initSmallIconDrop(i, this.mWidth, this.mHeight);
                    continue;
                }

                Vector3f smallIconDropPosition = smallRainDrop.getPosition();
                Vector3f smallIconDropEndPosition = smallRainDrop.getEndPosition();
                Vector3f smallIconDropHalfPosition = smallRainDrop.getHalfPosition();
                float smallIconDropAlpha = smallRainDrop.getAlpha();
                boolean isFreeFall = smallRainDrop.getIsFreeFall();
                Vector3f smallIconDropSpeed = smallRainDrop.getDspeed();

                if (((!isFreeFall) && ((xSpeed > 2.1F) || (xSpeed < -2.1F)))
                        || ((!isWorkspaceStatic()) && (!isFreeFall))) {
                    smallRainDrop.setIsFreeFall(true);
                    smallRainDrop.setAlpha(1f);
                    isFreeFall = true;
                    if (Math.abs(xSpeed) > 40f) {
                        smallIconDropSpeed.x = xSpeed / Math.abs(xSpeed) * 13f;
                    } else {
                        smallIconDropSpeed.x = 0.3f * xSpeed;
                    }

                    smallIconDropSpeed.y = 0f;
                }

                if (!isFreeFall) {
                    smallIconDropSpeed.x = 0f;
                }

                if (isFreeFall) {
                    smallIconDropSpeed.y += -54f;
                    smallIconDropSpeed.z = 0f;
                    smallIconDropPosition.add(smallIconDropSpeed);
                    smallIconDropSpeed.set(smallIconDropSpeed);
                    smallRainDrop.setDspeed(smallIconDropSpeed);
                    smallRainDrop.setAlpha(smallIconDropAlpha - 0.017f);
                    if (smallIconDropAlpha < -1f && this.isWorkspaceStatic()) {
                        this.initSmallIconDrop(i, this.mWidth, this.mHeight);
                    }
                    continue;
                }

                if (smallIconDropSpeed.y >= 0f || smallIconDropPosition.y <= smallIconDropHalfPosition.y) {
                    if (smallIconDropSpeed.y < 0f && smallIconDropPosition.y < smallIconDropHalfPosition.y
                            && smallIconDropPosition.y > smallIconDropEndPosition.y) {
                        smallRainDrop.setAlpha(1f);
                        smallRainDrop
                                .setScale(((float) (0.8 + 0.2 * (((smallIconDropHalfPosition.y - smallIconDropPosition.y) / (smallIconDropHalfPosition.y - smallIconDropEndPosition.y))))));
                        smallRainDrop.setWidth(this.iconRainWidth * smallRainDrop.getScale());
                        smallRainDrop.setHeight(this.iconRainHeight * smallRainDrop.getScale());
                        smallRainDrop.buildMesh();
                        smallIconDropSpeed.y += 0.05f;
                        if (smallIconDropSpeed.y > 0f) {
                            smallIconDropSpeed.y = 0f;
                        }

                        smallIconDropSpeed.z = 0f;
                        smallIconDropPosition.add(smallIconDropSpeed);
                        smallIconDropSpeed.set(smallIconDropSpeed);
                        smallRainDrop.setDspeed(smallIconDropSpeed);
                    } else if (smallIconDropSpeed.y != 0f && smallIconDropPosition.y >= smallIconDropEndPosition.y) {
                        smallIconDropSpeed.y = this.getSmallIconDropSpeed();
                        smallIconDropSpeed.z = 0f;
                        smallIconDropPosition.add(smallIconDropSpeed);
                        smallIconDropSpeed.set(smallIconDropSpeed);
                        smallRainDrop.setDspeed(smallIconDropSpeed);
                    } else {
                        smallRainDrop.setAlpha(smallIconDropAlpha - 0.02f);
                        if (smallIconDropAlpha < 0f && this.isWorkspaceStatic()) {
                            this.initSmallIconDrop(i, this.mWidth, this.mHeight);
                        }
                    }
                } else {
                    smallRainDrop.setAlpha(1f - (smallIconDropPosition.y - smallIconDropHalfPosition.y)
                            / (smallIconDropHalfPosition.y - smallIconDropEndPosition.y));
                }
            }
        }
    }
}
