package com.wb.launcher3.weatherIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.wb.launcher3.FastBitmapDrawable;
import com.wb.launcher3.ShortcutAndWidgetContainer;
import com.wb.launcher3.liveweather.LiveWeatherGLView;

public class WeatherIconController {
    class IconEdgeSupporter {
        static final int COLUMN_COUNT = 5;//4;
        static final int LINE_COUNT = 5;

        private int[] mColumn;
        private int mColumnWidth;
        private int[] mDockbarColumn;
        HashMap<Integer, WeatherIcon> mIconHashMap;
        private int[] mLine;
        private int mLineHeight;

        public IconEdgeSupporter() {
            this.mLine = new int[] { -1, -1, -1, -1, -1 };
            this.mColumn = new int[] { -1, -1, -1, -1 ,-1};
            this.mDockbarColumn = new int[] { -1, -1, -1, -1, -1 };
            this.mLineHeight = 0;
            this.mColumnWidth = 0;
            this.mIconHashMap = new HashMap<Integer, WeatherIcon>();
        }

        public void clearIconInfo(WeatherIcon weatherIcon) {
            WeatherIconDrawInfo iconDrawInfo = weatherIcon.getDrawInfo();

            if (iconDrawInfo != null) {
                int cellY = iconDrawInfo.getCellY();
                int cellX = iconDrawInfo.getCellX();

                if (iconDrawInfo.getIsDockBar()) {
                    cellY = 4;
                    this.mDockbarColumn[cellX] = -1;
                }

                this.mIconHashMap.remove(cellY * 4 + cellX);
            }
        }

        public void clearWorkspaceInfo() {
            for (int i = 0; i < 4; ++i) {
                this.mLine[i] = -1;
            }

            for (int i = 0; i < 4; ++i) {
                this.mColumn[i] = -1;
            }
        }

        protected void dumpLineColumn() {
            Log.d(WeatherIconController.TAG, "dumpLineCow line :" + this.mLine[0] + " " + this.mLine[1] + " "
                    + this.mLine[2] + " " + this.mLine[3] + " " + this.mLine[4]);
            Log.d(WeatherIconController.TAG, "dumpLineCow column :" + this.mColumn[0] + " " + this.mColumn[1] + " "
                    + this.mColumn[2] + " " + this.mColumn[3]);
            Log.d(WeatherIconController.TAG, "dumpLineCow dockbarColumn :" + this.mDockbarColumn[0] + " "
                    + this.mDockbarColumn[1] + " " + this.mDockbarColumn[2] + " " + this.mDockbarColumn[3]);
        }

        protected Rect getIconRect(WeatherIconDrawInfo weatherIconDrawInfo) {
            Rect cellRect = weatherIconDrawInfo.getCellRect();
            int iconWidth = weatherIconDrawInfo.getIconWidth();
            int iconHeight = weatherIconDrawInfo.getIconHeight();
            int paddingTop = weatherIconDrawInfo.getPaddingTop();

            Rect result = new Rect();

            if (cellRect != null) {
                result.left = cellRect.left + (cellRect.width() - iconWidth) / 2;
                result.right = result.left + iconWidth;
                result.top = cellRect.top + paddingTop;
                result.bottom = result.top + iconHeight;
            } else {
                result = null;
            }

            return result;
        }

        public void getLineCowInfo(WeatherIcon weatherIcon) {
            WeatherIconDrawInfo drawInfo = weatherIcon.getDrawInfo();

            if (drawInfo != null) {
                int cellY = drawInfo.getCellY();
                int cellX = drawInfo.getCellX();
                boolean isDockBar = drawInfo.getIsDockBar();

                if (isDockBar) {
                    cellY = 4;
                }

                Rect cellRect = drawInfo.getCellRect();

                if (cellRect != null) {
                    this.mLine[cellY] = cellRect.top;

                    if (isDockBar) {
                        this.mDockbarColumn[cellX] = cellRect.left;
                    } else {
                        this.mColumn[cellX] = cellRect.left;
                    }

                    this.mLineHeight = cellRect.height();
                    this.mColumnWidth = cellRect.width();

                    Rect iconRect = this.getIconRect(drawInfo);

                    if (iconRect != null) {
                        drawInfo.setIconRect(iconRect);
                    }
                } else {
                    Log.w(WeatherIconController.TAG, "getLineCowInfo getCellRect for weatherIcon:" + weatherIcon
                            + " is null !");
                }

                this.mIconHashMap.put(cellY * 4 + cellX, weatherIcon);
            }
        }

        public WeatherIcon getWeatherIconInHashMap(Integer integer) {
            return this.mIconHashMap.get(integer.intValue());
        }

        public int[] reckonIconEdge(int x, int y) {
            int[] result;

            int index = this.reckonInCell(x, y);

            if (index >= 0) {
                result = this.reckonInIcon(x, y, this.mIconHashMap.get(index));
            } else {
                result = null;
            }

            return result;
        }

        public int[] reckonInBitmap(int x, int y, Bitmap bitmap) {
            int[] result = null;

            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                if (x < width && y < height) {
                    int[] pixelArray = new int[width * height];
                    bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);
                    result = new int[] { 0, 0, 0, 0 };

                    int i = 0;
                    for (i = x; i > 0; --i) {
                        if (pixelArray[y * width + i] >>> 24 == 0) {
                            result[0] = x - i;
                            break;
                        }
                    }

                    if (i == 0) {
                        result[0] = x;
                    }

                    for (i = x + 1; i < width; ++i) {
                        if (pixelArray[y * width + i] >>> 24 == 0) {
                            result[2] = i - x - 1;
                            break;
                        }
                    }

                    if (i == width) {
                        result[2] = width - x;
                    }

                    for (i = y; i > 0; --i) {
                        if (pixelArray[i * width + x] >>> 24 == 0) {
                            result[1] = y - i;
                            break;
                        }
                    }

                    if (i == 0) {
                        result[1] = y;
                    }

                    for (i = y + 1; i < height; ++i) {
                        if (pixelArray[i * width + x] >>> 24 == 0) {
                            result[3] = i - y - 1;
                            break;
                        }
                    }

                    if (i == height) {
                        result[3] = height - y;
                    }
                }
            }

            return result;
        }

        public int reckonInCell(int x, int y) {
            int[] colArray;
            int line = -1;
            int col = -1;

            for (int i = 0; i < LINE_COUNT; ++i) {
                if (this.mLine[i] >= 0 && y >= this.mLine[i] && y < this.mLine[i] + this.mLineHeight) {
                    line = i;
                    break;
                }
            }

            if (line < 0) {
                return -1;
            }

            if (line < 4) {
                colArray = this.mColumn;
            } else {
                colArray = this.mDockbarColumn;
            }

            for (int i = 0; i < COLUMN_COUNT; ++i) {
                if (colArray[i] >= 0 && x >= colArray[i] && x < colArray[i] + this.mColumnWidth) {
                    col = i;
                    break;
                }
            }

            if (col < 0) {
                return -1;
            }

            int index = line * 4 + col;
            if (this.mIconHashMap.get(index) != null) {
                return index;
            }

            return -1;
        }

        public int[] reckonInIcon(int x, int y, WeatherIcon weatherIcon) {
            int[] result = null;
            WeatherIconDrawInfo drawInfo = weatherIcon.getDrawInfo();

            if (drawInfo != null) {
                Rect iconRect = drawInfo.getIconRect();

                if (iconRect != null) {
                    FastBitmapDrawable drawable = weatherIcon.getFastBitmapDrawable();
                    Bitmap bitmap = null;

                    if (drawable != null) {
                        bitmap = drawable.getBitmap();
                    }

                    result = this.reckonInRect(x, y, iconRect, bitmap);
                }
            }

            return result;
        }

        public int[] reckonInRect(int x, int y, Rect rect, Bitmap bitmap) {
            if (rect.contains(x, y)) {
                return this.reckonInBitmap(x - rect.left, y - rect.top, bitmap);
            } else {
                return null;
            }
        }

        public int[] upperEdgeForIcon(int index) {
            if (index < 0 || index >= WeatherIconController.this.mWeatherIconList.size()) {
                return null;
            } else {
                return this.upperEdgeForIcon(WeatherIconController.this.mWeatherIconList.get(index));
            }
        }

        public int[] upperEdgeForIcon(WeatherIcon weatherIcon) {
            int[] result = null;
            WeatherIconDrawInfo drawInfo = weatherIcon.getDrawInfo();

            if (drawInfo == null) {
                return null;
            }

            if (this.getIconRect(drawInfo) == null) {
                return null;
            }

            FastBitmapDrawable drawable = weatherIcon.getFastBitmapDrawable();
            Bitmap bitmap = null;

            if (drawable != null) {
                bitmap = drawable.getBitmap();
            }

            if (bitmap == null) {
                return null;
            }

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            result = new int[width];
            for (int i = 0; i < width; ++i) {
                result[i] = -1;
                for (int j = 0; j < height; ++j) {
                    if (bitmap.getPixel(i, j) != 0) {
                        result[i] = j;
                        break;
                    }
                }
            }

            return result;
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                switch (msg.what) {
                case 0:
                    WeatherIconController.this.mapWeatherIconAnimation(WeatherIconController.this.mWeatherType);

                    try {
                        Iterator<WeatherIcon> iterator = WeatherIconController.this.mWeatherIconList.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().initDrawInfo(WeatherIconController.this.mWeatherType);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    WeatherIconController.this.startAnimation();
                    break;
                case 3:
                    WeatherIconController.this.resumeAnimation();
                    break;
                }
            }
        }
    }

    class OnIconActionRunnable implements Runnable {
        int mIndex;
        int mWeathertype;

        public OnIconActionRunnable(int index, int weathertype) {
            this.mIndex = index;
            this.mWeathertype = weathertype;
        }

        @Override
        public void run() {
            WeatherIconDrawInfo drawInfo = WeatherIconController.this.mWeatherIconList.get(this.mIndex).getDrawInfo();

            if (drawInfo != null && (drawInfo instanceof SnowIconDrawInfo)) {
                SnowIconDrawInfo snowDrawInfo = (SnowIconDrawInfo) drawInfo;

                float thickness = snowDrawInfo.getThickness();
                float maxThickness = snowDrawInfo.getMaxThickness();
                float thicknessPlus = maxThickness / 30f;

                thickness -= maxThickness / 4f;

                if (thickness <= 0f) {
                    thickness = 0f;
                }

                snowDrawInfo.getUpAlpha();
                snowDrawInfo.getDownAlpha();

                int upAlpha = 255f * thickness / maxThickness + thickness > 0 ? 50 : 0;
                int downAlpha;

                if (thickness > 12f * thicknessPlus) {
                    downAlpha = (int) ((thickness - 11f * thicknessPlus) * 600f / maxThickness);
                } else {
                    downAlpha = 0;
                }

                snowDrawInfo.setUpAlpha(upAlpha);
                snowDrawInfo.setDownAlpha(downAlpha);
                snowDrawInfo.setThickness(thickness);

                ArrayList<WeatherIconDrawInfo> list = new ArrayList<WeatherIconDrawInfo>();
                list.add(drawInfo);
                WeatherIconController.this.drawIcons(list);
            }
        }
    }

    class PageSwitchRunnable implements Runnable {
        public PageSwitchRunnable(WeatherIconController arg1) {
            super();
        }

        @Override
        public void run() {
            WeatherIconController.this.mIsPageSwitchRuning = true;
            WeatherIconController.this.pauseAnimation();
            Log.i("myl","zhangwuba ------- onPageSwitch PageSwitchRunnable = 222222 ");

            synchronized (mWeatherIconList) {
                WeatherIconController.this.clearWorkSpaceRegisterIconsInner();
                if (WeatherIconController.this.mNewPage instanceof ViewGroup) {
                    View newPage = mNewPage;

                    if (newPage != null) {
                        int childCount = ((ViewGroup) newPage).getChildCount();
                        Log.i("myl","zhangwuba ------- onPageSwitch PageSwitchRunnable childCount =  " +childCount);
                        for (int i = 0; i < childCount; ++i) {
                        	ViewGroup container = (ViewGroup) ((ViewGroup) newPage).getChildAt(i);
                        	 Log.i("myl","zhangwuba ------- onPageSwitch PageSwitchRunnable container =  " +container);
                            int childnum = container.getChildCount();
                            Log.i("myl","zhangwuba ------- onPageSwitch PageSwitchRunnable childnum =  " +childnum);
                            for(int j = 0; j < childnum; j++){
                            	View child = container.getChildAt(j);
                            	 Log.i("myl","zhangwuba ------- onPageSwitch PageSwitchRunnable child =  " +child);
                            	if (child instanceof WeatherIcon) {
                                    WeatherIconController.this.registerIconInner((WeatherIcon) child, true);
                                }
                            }
        
                        }
                    }
                }
            }

            WeatherIconController.this.checkDockBarIconsRect();
            Message message = new Message();
            message.what = MSG_RESUME;
            WeatherIconController.this.mHandler.sendMessageDelayed(message, 1000);
            WeatherIconController.this.mIsPageSwitchRuning = false;
        }
    }

    private static final int MSG_INIT = 0;
    private static final int MSG_RESUME = 3;
    private static final String TAG = WeatherIconController.class.getSimpleName();
    protected int mDockbarSize;
    private MyHandler mHandler;
    protected int mHideFlag;
    private IconEdgeSupporter mIconEdgeSupporter;
    private static WeatherIconController mInstance = null;
    private boolean mIsPageSwitchRuning;
    protected IconListener mListener;
    protected View mNewPage;
    protected int mNewPageIndex;
    protected WeatherIconAnimation mWeatherIconAnimation;
    protected ArrayList<WeatherIcon> mWeatherIconList;
    protected int mWeatherType;
    private static HandlerThread sAnimationThread = new HandlerThread("launcher-weatherIconAnimation");
    private static HandlerThread sPageSwitchThread = new HandlerThread("launcher-pageSwitch");
    private static Handler sPageSwitchWorker;

    private WeatherIconController() {
        super();
        this.mWeatherType = LiveWeatherGLView.LIVE_WEATHER_TYPE_NONE;
        this.mWeatherIconList = new ArrayList<WeatherIcon>();
        this.mWeatherIconAnimation = null;
        this.mListener = null;
        this.mNewPage = null;
        this.mNewPageIndex = 0;
        this.mDockbarSize = 0;
        this.mHideFlag = 0;
        this.mHandler = new MyHandler();
        this.mIconEdgeSupporter = new IconEdgeSupporter();
        this.mIsPageSwitchRuning = false;

        WeatherIconController.sAnimationThread.start();
        WeatherIconController.sPageSwitchThread.start();
        sPageSwitchWorker = new Handler(WeatherIconController.sPageSwitchThread.getLooper());
    }

    protected void checkDockBarIconsRect() {
        for (int i = 0; i < this.mDockbarSize; ++i) {
            WeatherIcon weatherIcon = this.mWeatherIconList.get(i);
            WeatherIconDrawInfo drawInfo = weatherIcon.getDrawInfo();

            if (drawInfo != null && drawInfo.getCellRect() == null && (weatherIcon instanceof View)) {
                Rect rect = new Rect();
                if (((View) weatherIcon).getGlobalVisibleRect(rect)) {
                    drawInfo.setCellRect(rect);
                    this.mIconEdgeSupporter.getLineCowInfo(weatherIcon);
                }
            }
        }
    }

    public void clearRegisterIcons() {
        synchronized (this.mWeatherIconList) {
            this.mWeatherIconList.clear();
        }

        this.mDockbarSize = 0;

        if (this.mListener != null) {
            this.mListener.onClearIcons();
        }
    }

    protected void clearWorkSpaceRegisterIconsInner() {
        for (int i = this.mWeatherIconList.size() - 1; i >= this.mDockbarSize; --i) {
            WeatherIcon weatherIcon = this.mWeatherIconList.get(i);

            if (this.mListener != null) {
                this.mListener.onUnregisterIcon(weatherIcon);
            }

            this.mIconEdgeSupporter.clearIconInfo(weatherIcon);
            weatherIcon.removeDrawInfo();
            this.mWeatherIconList.remove(i);
        }

        this.mIconEdgeSupporter.clearWorkspaceInfo();
    }

    protected void drawIcons(ArrayList<WeatherIconDrawInfo> iconDrawInfos) {
        if (iconDrawInfos != null) {
            Iterator<WeatherIconDrawInfo> iterator = iconDrawInfos.iterator();

            while (iterator.hasNext()) {
                WeatherIconDrawInfo drawInfo = iterator.next();
                WeatherIcon weatherIcon = null;

                if (drawInfo != null) {
                    weatherIcon = drawInfo.getIconInstance();
                }

                if (weatherIcon != null) {
                    weatherIcon.onDrawIcon(drawInfo);
                }
            }
        }
    }

    protected void dumpIconRects() {
        ArrayList<Rect> iconRects = this.getIconRects();

        if (iconRects != null) {
            for (int i = 0; i < iconRects.size(); ++i) {
                Log.i(WeatherIconController.TAG, "dumpIconRects i:" + i + " rect:" + iconRects.get(i));
            }
        }
    }

    @Override
    public void finalize() throws Throwable {
        this.mWeatherIconList.clear();
        this.mWeatherIconList = null;
    }

    public View getCurrPageInWorkspace() {
        return this.mNewPage;
    }

    public int[] getEdgeForPos(int x, int y) {
        return this.mIconEdgeSupporter.reckonIconEdge(x, y);
    }

    public int getHideFlag() {
        return this.mHideFlag;
    }

    public WeatherIconDrawInfo getIconInfo(int index, int weathertype) {
        WeatherIconDrawInfo drawInfo = mWeatherIconList.get(index).getDrawInfo();

        if (drawInfo == null) {
            return null;
        }

        return drawInfo;
    }

    public int getIconNum() {
        return this.mWeatherIconList.size();
    }

    public ArrayList<Rect> getIconRects() {
        ArrayList<Rect> result = null;

        if (this.mIsPageSwitchRuning) {
            return null;
        }

        if (this.getIconNum() > 0) {
            result = new ArrayList<Rect>();

            for (int i = 0; i < this.mWeatherIconList.size(); ++i) {
                WeatherIconDrawInfo drawInfo = this.mWeatherIconList.get(i).getDrawInfo();

                if (drawInfo != null) {
                    Rect iconRect = drawInfo.getIconRect();

                    if (iconRect != null) {
                        result.add(iconRect);
                    }
                }
            }
        }

        return result;
    }

    public static WeatherIconController getInstance() {
        if (WeatherIconController.mInstance == null) {
            WeatherIconController.mInstance = new WeatherIconController();
        }

        return WeatherIconController.mInstance;
    }

    public ArrayList<WeatherIcon> getRegisterIcons() {
        return this.mWeatherIconList;
    }

    public int[] getUpperEdgeForIcon(int index) {
        return this.mIconEdgeSupporter.upperEdgeForIcon(index);
    }

    public int getWeatherType() {
        return this.mWeatherType;
    }

    public void hideAnimation() {
        ++this.mHideFlag;

        if (this.mWeatherIconAnimation == null) {
            return;
        }

        synchronized (this.mWeatherIconList) {
            this.mWeatherIconAnimation.onHideAnimation();
        }
    }

    protected void mapWeatherIconAnimation(int weatherType) {
        WeatherIconAnimation animation = null;

        switch (weatherType) {
        case 200:
            this.mWeatherIconAnimation = animation;
            break;
        case 201:
            this.mWeatherIconAnimation = animation;
            break;
        case 202: {
            this.mWeatherIconAnimation = new SnowAnimation();
            break;
        }
        case 203:
        case 204:
        case 205:
        case 206:
            this.mWeatherIconAnimation = animation;
            break;
        case 208:
            this.mWeatherIconAnimation = new ThunderAnimation();
            break;
        }
    }

    public void onIconAction(int index, int weathertype) {
        this.pauseAnimation();
        Message message = new Message();
        message.what = 3;
        this.mHandler.sendMessageDelayed(message, 2000);
        WeatherIconController.sPageSwitchWorker.post(new OnIconActionRunnable(index, weathertype));
    }

    public void onPageSwitch(View newPage, int newPageIndex) {
    	Log.i("myl","zhangwuba ------- onPageSwitch newPage = " + newPage + " newPageIndex = " + newPageIndex);
        if (200 == this.mWeatherType) {
            return;
        }

        if (this.mNewPage == newPage && this.mNewPageIndex == newPageIndex) {
            return;
        }

        this.mNewPage = newPage;
        this.mNewPageIndex = newPageIndex;
        Log.i("myl","zhangwuba ------- onPageSwitch newPage = 222222 ");
        WeatherIconController.sPageSwitchWorker.post(new PageSwitchRunnable(this));
    }

    public void onPreDrawIcons(ArrayList<WeatherIconDrawInfo> iconDrawInfos) {
        this.drawIcons(iconDrawInfos);
    }

    public void pauseAnimation() {
        this.mHandler.removeMessages(3);

        if (this.mWeatherIconAnimation == null) {
            return;
        }

        this.mWeatherIconAnimation.onPauseAnimation();
    }

    public void reLoadWeatherIcon() {
        if (this.mWeatherType == 203) {
            WeatherIconController.sPageSwitchWorker.post(new PageSwitchRunnable(this));
        }
    }

    public boolean reckonPosInIcon(int x, int y) {
        boolean result = false;
        int index = this.mIconEdgeSupporter.reckonInCell(x, y);

        if (index >= 0) {
            WeatherIcon weatherIcon = this.mIconEdgeSupporter.getWeatherIconInHashMap(Integer.valueOf(index));

            if (weatherIcon != null) {
                WeatherIconDrawInfo drawInfo = weatherIcon.getDrawInfo();

                if (drawInfo != null) {
                    Rect rect = drawInfo.getIconRect();
                    if (rect != null) {
                        result = rect.contains(x, y);
                    }
                }
            }
        }

        return result;
    }

    public void registerIcon(WeatherIcon weatherIcon) {
        this.registerIconInner(weatherIcon, false);
    }

    public void registerIconForDockbar(WeatherIcon weatherIcon) {
        synchronized (this.mWeatherIconList) {
            this.mWeatherIconList.add(weatherIcon);
        }

        weatherIcon.initDrawInfo(this.mWeatherType);
        WeatherIconDrawInfo drawInfo = weatherIcon.getDrawInfo();

        if (drawInfo != null) {
            drawInfo.setIsDockBar(true);
        }

        this.mIconEdgeSupporter.getLineCowInfo(weatherIcon);
        ++this.mDockbarSize;

        if (this.mListener != null) {
            this.mListener.onRegisterIcon(weatherIcon);
        }
    }

    protected void registerIconInner(WeatherIcon weatherIcon, boolean hasLock) {
    	 Log.i("myl","zhangwuba ------- onPageSwitch registerIconInner child");
        if (!hasLock) {
            synchronized (this.mWeatherIconList) {
                this.mWeatherIconList.add(weatherIcon);
            }
        } else {
            this.mWeatherIconList.add(weatherIcon);
        }

        weatherIcon.initDrawInfo(this.mWeatherType);
        WeatherIconDrawInfo drawInfo = weatherIcon.getDrawInfo();

        if (drawInfo != null) {
            drawInfo.setIsDockBar(false);
        }

        this.mIconEdgeSupporter.getLineCowInfo(weatherIcon);

        if (this.mListener != null) {
            this.mListener.onRegisterIcon(weatherIcon);
        }
    }

    public void resetHideFlag() {
        this.mHideFlag = 0;
    }

    public void resumeAnimation() {
        if (this.mWeatherIconAnimation != null) {
            this.mWeatherIconAnimation.onResumeAnimation();
        }
    }

    public void setIconListener(IconListener listener) {
        this.mListener = listener;
    }

    public void setWeatherType(int newVal) {
        if (newVal == this.mWeatherType) {
            return;
        }

        this.mWeatherType = newVal;
        this.stopAnimation();

        Message message = new Message();
        message.what = 0;
        this.mHandler.sendMessage(message);
    }

    public void showAnimation() {
        --this.mHideFlag;

        if (this.mHideFlag < 0) {
            this.mHideFlag = 0;
        }

        if (this.mWeatherIconAnimation != null) {
            synchronized (this.mWeatherIconList) {
                this.mWeatherIconAnimation.onShowAnimation();
            }
        }
    }

    public void startAnimation() {
        if (this.mWeatherIconAnimation != null) {
            this.mWeatherIconAnimation.onStartAnimation(WeatherIconController.sAnimationThread);
        }
    }

    public void stopAnimation() {
        if (this.mWeatherIconAnimation == null) {
            return;
        }

        this.mWeatherIconAnimation.onStopAnimation();
    }

    public void unregisterIcon(WeatherIcon weatherIcon) {
        this.unregisterIconInner(weatherIcon, false);
    }

    public void unregisterIconForDockbar(WeatherIcon weatherIcon) {
        synchronized (this.mWeatherIconList) {
            int i = this.mWeatherIconList.indexOf(weatherIcon);

            if (i < 0) {
                Log.e(TAG, "unregisterIcon weatherIcon is not registered");
                return;
            }

            this.mWeatherIconList.remove(i);

            if (this.mListener != null) {
                this.mListener.onUnregisterIcon(weatherIcon);
            }

            this.mIconEdgeSupporter.clearIconInfo(weatherIcon);
            --this.mDockbarSize;
        }
    }

    protected void unregisterIconInner(WeatherIcon weatherIcon, boolean hasLock) {
        int index;

        if (!hasLock) {
            synchronized (this.mWeatherIconList) {
                index = this.mWeatherIconList.indexOf(weatherIcon);

                if (index < 0) {
                    Log.w(WeatherIconController.TAG, "unregisterIcon weatherIcon is not registered");
                } else {
                    this.mWeatherIconList.remove(index);

                    if (this.mListener != null) {
                        this.mListener.onUnregisterIcon(weatherIcon);
                    }

                    this.mIconEdgeSupporter.clearIconInfo(weatherIcon);
                }
            }
        } else {
            index = this.mWeatherIconList.indexOf(weatherIcon);

            if (index < 0) {
                Log.w(WeatherIconController.TAG, "unregisterIcon weatherIcon is not registered");
            } else {
                this.mWeatherIconList.remove(index);

                if (this.mListener != null) {
                    this.mListener.onUnregisterIcon(weatherIcon);
                }

                this.mIconEdgeSupporter.clearIconInfo(weatherIcon);
            }
        }
    }

    public boolean updateIcon(WeatherIcon weatherIcon, WeatherIconDrawInfo newInfo) {
        synchronized (this.mWeatherIconList) {
            if (this.mWeatherIconList.indexOf(weatherIcon) < 0) {
                Log.e(TAG, "updateIcon weatherIcon is not registered");
                return false;
            }

            this.mIconEdgeSupporter.clearIconInfo(weatherIcon);
            WeatherIconDrawInfo localWeatherIconDrawInfo = weatherIcon.getDrawInfo();

            if (localWeatherIconDrawInfo != null) {
                localWeatherIconDrawInfo.updateDrawInfo(newInfo);
            }

            this.mIconEdgeSupporter.getLineCowInfo(weatherIcon);

            if (this.mListener != null) {
                this.mListener.onUpdateIcon(weatherIcon);
            }

            return true;
        }
    }
}
