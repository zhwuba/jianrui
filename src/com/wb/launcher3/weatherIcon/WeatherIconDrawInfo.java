package com.wb.launcher3.weatherIcon;

import android.graphics.Rect;

public class WeatherIconDrawInfo {
    protected Rect mCellRect = new Rect();
    protected Rect mIconRect = new Rect();

    protected int mCellX = 0;
    protected int mCellY = 0;

    protected WeatherIcon mIcon;
    protected int mIconDrawablePadding = 0;
    protected int mIconPaddingTop = 0;
    protected int mIconHeight = 0;
    protected int mIconWidth = 0;

    protected boolean mIsDockBar = false;

    public WeatherIconDrawInfo(WeatherIcon icon) {
        this.mIcon = icon;
    }

    public WeatherIconDrawInfo(WeatherIconDrawInfo iconDrawInfo) {
        if (iconDrawInfo == null) {
            return;
        }

        this.mIcon = iconDrawInfo.mIcon;
        this.mCellX = iconDrawInfo.mCellX;
        this.mCellY = iconDrawInfo.mCellY;

        if (iconDrawInfo.mCellRect != null) {
            this.mCellRect.set(iconDrawInfo.mCellRect);
        }

        if (iconDrawInfo.mIconRect != null) {
            this.mIconRect.set(iconDrawInfo.mIconRect);
        }

        this.mIconWidth = iconDrawInfo.mIconWidth;
        this.mIconHeight = iconDrawInfo.mIconHeight;
        this.mIconPaddingTop = iconDrawInfo.mIconPaddingTop;
        this.mIconDrawablePadding = iconDrawInfo.mIconDrawablePadding;
        this.mIsDockBar = iconDrawInfo.mIsDockBar;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WeatherIconDrawInfo)) {
            return false;
        }

        WeatherIconDrawInfo drawInfo = (WeatherIconDrawInfo) object;

        if (this == drawInfo) {
            return true;
        }

        if (this.mIcon != null && !this.mIcon.equals(drawInfo.mIcon)) {
            return false;
        }

        if (this.mCellX != drawInfo.mCellX) {
            return false;
        }

        if (this.mCellY != drawInfo.mCellY) {
            return false;
        }

        if (this.mCellRect != null && this != (drawInfo) && this.mCellRect.equals(drawInfo.mCellRect)) {
            return false;
        }

        if (this.mIconWidth != drawInfo.mIconWidth) {
            return false;
        }

        if (this.mIconHeight != drawInfo.mIconHeight) {
            return false;
        }

        if (this.mIconPaddingTop != drawInfo.mIconPaddingTop) {
            return false;
        }

        if (this.mIconDrawablePadding != drawInfo.mIconDrawablePadding) {
            return false;
        }

        if (this.mIsDockBar != drawInfo.mIsDockBar) {
            return false;
        }

        return true;
    }

    public Rect getCellRect() {
        return this.mCellRect;
    }

    public int getCellX() {
        return this.mCellX;
    }

    public int getCellY() {
        return this.mCellY;
    }

    public int getDrawablePadding() {
        return this.mIconDrawablePadding;
    }

    public int getIconHeight() {
        return this.mIconHeight;
    }

    public WeatherIcon getIconInstance() {
        return this.mIcon;
    }

    public Rect getIconRect() {
        return this.mIconRect;
    }

    public int getIconWidth() {
        return this.mIconWidth;
    }

    public boolean getIsDockBar() {
        return this.mIsDockBar;
    }

    public int getPaddingTop() {
        return this.mIconPaddingTop;
    }

    public void setCellRect(Rect rect) {
        if (rect != null) {
            this.mCellRect.set(rect);
        }
    }

    public void setCellXY(int cellX, int cellY) {
        if (cellX < 0) {
            cellX = this.mCellX;
        }

        this.mCellX = cellX;

        if (cellY < 0) {
            cellY = this.mCellY;
        }

        this.mCellY = cellY;
    }

    public void setDrawablePadding(int drawablePadding) {
        this.mIconDrawablePadding = drawablePadding;
    }

    public void setIconRect(Rect rect) {
        if (rect != null) {
            this.mIconRect.set(rect);
        }
    }

    public void setIconWH(int width, int height) {
        if (width <= 0) {
            width = this.mIconWidth;
        }

        this.mIconWidth = width;

        if (height <= 0) {
            height = this.mIconHeight;
        }

        this.mIconHeight = height;
    }

    public void setIsDockBar(boolean isDockBar) {
        this.mIsDockBar = isDockBar;
    }

    public void setPaddingTop(int paddingTop) {
        this.mIconPaddingTop = paddingTop;
    }

    public void updateDrawInfo(WeatherIconDrawInfo newInfo) {
        if (newInfo == null) {
            return;
        }

        this.mCellX = newInfo.mCellX;
        this.mCellY = newInfo.mCellY;

        if (newInfo.mCellRect != null) {
            this.mCellRect.set(newInfo.mCellRect);
        }

        if (newInfo.mIconRect != null) {
            this.mIconRect.set(newInfo.mIconRect);
        }

        this.mIconWidth = newInfo.mIconWidth;
        this.mIconHeight = newInfo.mIconHeight;
        this.mIconPaddingTop = newInfo.mIconPaddingTop;
        this.mIconDrawablePadding = newInfo.mIconDrawablePadding;
        this.mIsDockBar = newInfo.mIsDockBar;
    }
}
