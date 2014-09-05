package com.wb.launcher3.weatherIcon;

public class SnowIconDrawInfo extends WeatherIconDrawInfo {
    protected int mDownAlpha = 0;
    protected float mMaxThickness = 0;
    protected float mThickness = 0;
    protected int mUpAlpha = 0;

    public SnowIconDrawInfo(WeatherIcon icon) {
        super(icon);
    }

    public SnowIconDrawInfo(WeatherIconDrawInfo iconDrawInfo) {
        super(iconDrawInfo);

        if (!(iconDrawInfo instanceof SnowIconDrawInfo)) {
            return;
        }

        SnowIconDrawInfo snowIconDrawInfo = (SnowIconDrawInfo) iconDrawInfo;

        this.mMaxThickness = snowIconDrawInfo.mMaxThickness;
        this.mThickness = snowIconDrawInfo.mThickness;
        this.mUpAlpha = snowIconDrawInfo.mUpAlpha;
        this.mDownAlpha = snowIconDrawInfo.mDownAlpha;
    }

    public void drawSnow() {
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if (object instanceof SnowIconDrawInfo) {
            SnowIconDrawInfo snowDrawInfo = (SnowIconDrawInfo) object;

            if (this.mMaxThickness == snowDrawInfo.mMaxThickness && this.mThickness == snowDrawInfo.mThickness
                    && this.mUpAlpha == snowDrawInfo.mUpAlpha && this.mDownAlpha == snowDrawInfo.mDownAlpha) {
                result = super.equals(object);
            }
        }

        return result;
    }

    public int getDownAlpha() {
        return this.mDownAlpha;
    }

    public float getMaxThickness() {
        return this.mMaxThickness;
    }

    public float getThickness() {
        return this.mThickness;
    }

    public int getUpAlpha() {
        return this.mUpAlpha;
    }

    public void setDownAlpha(int alpha) {
        this.mDownAlpha = alpha;
    }

    public void setMaxThickness(float maxThickness) {
        this.mMaxThickness = maxThickness;
    }

    public boolean setThickness(float thickness) {
        float newThickness = Math.min(thickness, mMaxThickness);

        if (newThickness != this.mThickness) {
            this.mThickness = newThickness;
            return true;
        }

        return false;
    }

    public void setUpAlpha(int alpha) {
        this.mUpAlpha = alpha;
    }

    @Override
    public String toString() {
        return "SnowIconDrawInfo x:" + this.mCellX + " y:" + this.mCellY + " cellRect:" + this.mCellRect + " iconRect"
                + this.mIconRect + " mIconWidth:" + this.mIconWidth + " mIconHeight:" + this.mIconHeight
                + " mIconPaddingTop:" + this.mIconPaddingTop + " mIconDrawablePadding:" + this.mIconDrawablePadding
                + " mIsDockBar:" + this.mIsDockBar + " mThickness:" + this.mThickness + " mMaxThickness:"
                + this.mMaxThickness + " mUpAlpha:" + this.mUpAlpha + " mDownAlpha:" + this.mDownAlpha;
    }
}
