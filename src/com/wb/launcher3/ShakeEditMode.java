package com.wb.launcher3;

public interface ShakeEditMode {
    
    public void enterEditMode();
    public void exitEditMode();
    
    public boolean isRemoveable();
 
    public void setRippleInfo(int direction, float angle);
    public int getDirection();
    public float getInitAngle();
    
    public void shakeOnceTime();
}