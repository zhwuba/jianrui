package com.wb.launcher3.weatherIcon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.os.HandlerThread;

public class ThunderAnimation extends AnimationThread {
    private static final int FRAME_TIME_MS = 130;
    public static boolean CHANGE_COLOR = false;

    ArrayList<WeatherIconDrawInfo> drawInfoList = new ArrayList<WeatherIconDrawInfo>();
    public int flag = 0;

    public ThunderAnimation() {
        super();

        this.mFrameTime = FRAME_TIME_MS;
        this.mEnableCircled = true;
    }

    public void getAllIconInfo(List<WeatherIcon> weatherIcons, ArrayList<WeatherIconDrawInfo> result) {
        if (result != null) {
            result.clear();

            for (int i = 0; i < weatherIcons.size(); ++i) {
                result.add(weatherIcons.get(i).getDrawInfo());
            }
        }
    }

    @Override
    public int getAnimationMaxFrame() {
        return 0;
    }

    @Override
    public WeatherIconController getIconController() {
        return WeatherIconController.getInstance();
    }

    public ArrayList<WeatherIconDrawInfo> getRandomIconInfo(ArrayList<WeatherIcon> weatherIcons) {
        if (weatherIcons.size() <= 0) {
            return null;
        }

        Random random = new Random();
        ArrayList<WeatherIconDrawInfo> result = new ArrayList<WeatherIconDrawInfo>();
        int randomInt = random.nextInt(3);

        if (randomInt != 0) {
            if (randomInt == 1) {
                result.add(weatherIcons.get(random.nextInt(weatherIcons.size())).getDrawInfo());
            } else {
                int randomInt2 = random.nextInt(weatherIcons.size());
                result.add(weatherIcons.get(randomInt2).getDrawInfo());
                int randomInt3 = random.nextInt(weatherIcons.size());

                if (randomInt2 != randomInt3) {
                    result.add(weatherIcons.get(randomInt3).getDrawInfo());
                }
            }
        }

        return result;
    }

    @Override
    public ArrayList<WeatherIconDrawInfo> invokeFrame(int frame) {
        ArrayList<WeatherIcon> weatherIcons = this.getIconController().getRegisterIcons();

        if (!ThunderAnimation.CHANGE_COLOR) {
            if (this.flag == 0) {
                if (this.drawInfoList != null) {
                    this.drawInfoList.clear();
                }
            } else {
                this.getAllIconInfo(weatherIcons, this.drawInfoList);
            }

            this.flag = 0;
        } else {
            if (this.flag == 0) {
                if (this.drawInfoList != null) {
                    this.drawInfoList.clear();
                }

                this.drawInfoList = this.getRandomIconInfo(weatherIcons);
                ++this.flag;
            }
        }

        return this.drawInfoList;
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
