package com.wb.launcher3.weatherIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.os.HandlerThread;
import android.os.SystemClock;

public class SnowAnimation extends HashMapAnimation {
    class SnowAnimationHashMap extends AnimationHashMap {
        private HashMap<Integer, Integer> mHashMap;

        public SnowAnimationHashMap() {
            this.mHashMap = null;
        }

        @Override
        public HashMap<Integer, Integer> getHashMap() {
            return this.mHashMap;
        }

        @Override
        public ArrayList<WeatherIconDrawInfo> invokeFrameInHashMap(int key, Object value) {
            ArrayList<WeatherIcon> icons = WeatherIconController.getInstance().getRegisterIcons();
            ArrayList<WeatherIconDrawInfo> result = new ArrayList<WeatherIconDrawInfo>();

            Iterator<WeatherIcon> iterator = icons.iterator();

            while (iterator.hasNext()) {
                SnowIconDrawInfo drawInfo = this.updateSnowInfo(iterator.next(), key, value);

                if (drawInfo != null) {
                    result.add(drawInfo);
                }
            }

            if (result.size() <= 0) {
                result = null;
            }

            return result;
        }

        protected boolean isInvokeFrame(int cellY, int value) {
            int v3 = 4;
            int v2 = 2;
            boolean result = true;
            if (((value & 1) != 1 || cellY != 0) && ((value & 2) != v2 || 1 != cellY)
                    && ((value & 4) != v3 || v2 != cellY) && ((value & 8) != 8 || 3 != cellY)
                    && ((value & 16) != 16 || v3 != cellY)) {
                result = false;
            }

            return result;
        }

        public void setHashMap(HashMap<Integer, Integer> map) {
            this.mHashMap = map;
        }

        protected SnowIconDrawInfo updateSnowInfo(WeatherIcon icon, int key, Object value) {
            SnowIconDrawInfo snowIconDrawInfo = null;
            WeatherIconDrawInfo drawInfo = icon.getDrawInfo();

            if (drawInfo == null || !(drawInfo instanceof SnowIconDrawInfo)) {
                snowIconDrawInfo = null;
            } else {
                snowIconDrawInfo = (SnowIconDrawInfo) drawInfo;
                int cellY = snowIconDrawInfo.getCellY();

                if (snowIconDrawInfo.getIsDockBar()) {
                    cellY = 4;
                }

                if (!this.isInvokeFrame(cellY, ((Integer) value).intValue())
                        || !this.updateSnowInfoInner(icon, key, value)) {
                    snowIconDrawInfo = null;
                }
            }

            return snowIconDrawInfo;
        }

        protected boolean updateSnowInfoInner(WeatherIcon icon, int key, Object value) {
            boolean result = false;
            mTime = SystemClock.uptimeMillis();

            if (mTime - mBeginTime > 7000) {
                SnowIconDrawInfo drawInfo = (SnowIconDrawInfo) icon.getDrawInfo();

                if (drawInfo != null) {
                    float thickness = drawInfo.getThickness();
                    float maxThickness = drawInfo.getMaxThickness();
                    float thicknessPlus = maxThickness / 30f;
                    thickness += thicknessPlus;
                    int downAlpha = drawInfo.getDownAlpha();
                    int upAlpha = (int) ((255f * thickness / maxThickness) + 50);

                    if (thickness > 12f * thicknessPlus) {
                        downAlpha = (int) ((thickness - 11f * thicknessPlus) * 600f / maxThickness);
                    }

                    drawInfo.setUpAlpha(upAlpha);
                    drawInfo.setDownAlpha(downAlpha);

                    if (thickness >= maxThickness) {
                        return result;
                    }

                    result = drawInfo.setThickness(thickness);
                }
            }

            return result;
        }
    }

    private static final int FRAME_TIME_MS = 300;
    private static final int IGNORE_FRAM = 0;
    private static final int INVOKE_LINE_0 = 1;
    private static final int INVOKE_LINE_1 = 2;
    private static final int INVOKE_LINE_2 = 4;
    private static final int INVOKE_LINE_3 = 8;
    private static final int INVOKE_LINE_4 = 16;
    private static final int KEY_FRAME_LINE_0 = 2;
    private static final int KEY_FRAME_LINE_1 = 3;
    private static final int KEY_FRAME_LINE_2 = 3;
    private static final int KEY_FRAME_LINE_3 = 4;
    private static final int KEY_FRAME_LINE_4 = 5;
    private static final int KEY_FRAME_NUM = 30;
    private static final String TAG = SnowAnimation.class.getSimpleName();

    protected ArrayList<AnimationHashMap> mAnimationHashMapList;
    public long mBeginTime;
    public long mTime;
    protected static HashMap<Integer, Integer> sAnimationHashMapIncrease = new HashMap<Integer, Integer>();
    protected static HashMap<Integer, Integer> sAnimationHashMapIncreaseForDcokbar = new HashMap<Integer, Integer>();

    static {
        for (int i = 1; i <= 75; ++i) {
            SnowAnimation.sAnimationHashMapIncrease.put(i * 2, 1);
        }

        SnowAnimation.initHashMapLineFrame(SnowAnimation.sAnimationHashMapIncrease, 3, 2);
        SnowAnimation.initHashMapLineFrame(SnowAnimation.sAnimationHashMapIncrease, 3, 4);
        SnowAnimation.initHashMapLineFrame(SnowAnimation.sAnimationHashMapIncrease, 4, 8);
        SnowAnimation.initHashMapLineFrame(SnowAnimation.sAnimationHashMapIncreaseForDcokbar, 5, 16);
    }

    public SnowAnimation() {
        super();
        this.mAnimationHashMapList = new ArrayList<AnimationHashMap>();
        this.mFrameTime = 300;
        this.mBeginTime = SystemClock.uptimeMillis();
        this.mEnableCircled = true;

        SnowAnimationHashMap map1 = new SnowAnimationHashMap();
        map1.setHashMap(sAnimationHashMapIncrease);

        SnowAnimationHashMap map2 = new SnowAnimationHashMap();
        map2.setHashMap(sAnimationHashMapIncreaseForDcokbar);

        this.mAnimationHashMapList.add(map1);
        this.mAnimationHashMapList.add(map2);
    }

    @Override
    public ArrayList<AnimationHashMap> getAnimationList() {
        return this.mAnimationHashMapList;
    }

    @Override
    public int getAnimationMaxFrame() {
        return 150;
    }

    @Override
    public WeatherIconController getIconController() {
        return WeatherIconController.getInstance();
    }

    private static void initHashMapLineFrame(HashMap<Integer, Integer> map, int lineFrame, int lineValue) {
        for (int i = 1; i <= 150 / lineFrame; ++i) {
            int key = i * lineFrame;
            Object value = map.get(key);

            if (value == null) {
                map.put(key, lineValue);
            } else {
                map.put(key, ((Integer) value).intValue() + lineValue);
            }
        }
    }

    @Override
    public void onHideAnimation() {
        Iterator<WeatherIcon> iterator = WeatherIconController.getInstance().getRegisterIcons().iterator();

        while (iterator.hasNext()) {
            iterator.next().onDrawIcon(null);
        }
    }

    @Override
    public void onShowAnimation() {
        Iterator<WeatherIcon> iterator = WeatherIconController.getInstance().getRegisterIcons().iterator();

        while (iterator.hasNext()) {
            iterator.next().onDrawIcon(null);
        }
    }

    @Override
    public void onStartAnimation(HandlerThread workThread) {
        super.onStartAnimation(workThread);

        WeatherIconController iconController = this.getIconController();

        if (iconController.getHideFlag() > 0) {
            iconController.pauseAnimation();
        }
    }
}
