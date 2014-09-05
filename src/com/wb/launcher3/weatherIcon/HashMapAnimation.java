package com.wb.launcher3.weatherIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

public abstract class HashMapAnimation extends AnimationThread {
    private static final String TAG = "HashMapAnimation";

    public abstract class AnimationHashMap {
        public abstract HashMap<Integer, Integer> getHashMap();

        public abstract ArrayList<WeatherIconDrawInfo> invokeFrameInHashMap(int key, Object value);
    }

    public abstract ArrayList<AnimationHashMap> getAnimationList();

    @Override
    public ArrayList<WeatherIconDrawInfo> invokeFrame(int frame) {
        ArrayList<WeatherIconDrawInfo> result = null;
        Iterator<AnimationHashMap> iterator = this.getAnimationList().iterator();

        while (iterator.hasNext()) {
            AnimationHashMap animationHashmap = iterator.next();
            Object value = animationHashmap.getHashMap().get(frame);

            if (value == null) {
                continue;
            }

            ArrayList<WeatherIconDrawInfo> drawInfos = animationHashmap.invokeFrameInHashMap(frame, value);

            if (result == null) {
                result = drawInfos;
                continue;
            }

            if (drawInfos != null) {
                result.addAll(drawInfos);
                continue;
            }

            Log.w(HashMapAnimation.TAG, "invokeFrame the animationHashMap:" + animationHashmap
                    + "invokeFrameInHashMap return null !");
        }

        return result;
    }
}
