package com.wb.launcher3.mic;

public class HuffThread extends Thread {
    public abstract interface IMicCallback {
        public abstract void detectSound(float arg0);
    }

    private String control;
    private boolean mIsAlive;
    private IMicCallback mMicCallback;
    private MicroPhone mMicroPhone;

    public HuffThread() {
        super();

        this.mIsAlive = true;
        this.control = "";

        synchronized (this.control) {
            this.mMicroPhone = MicroPhone.getInstance();
            this.mMicroPhone.openMic();
        }
    }

    public void exit() {
        this.mIsAlive = false;
    }

    @Override
    public void run() {
        synchronized (this.control) {
            while (this.mIsAlive) {
                this.runLogic();
            }

            this.mMicroPhone.closeMic();
        }
    }

    protected void runLogic() {
        float noiseData = this.mMicroPhone.getNoiseData();

        if (noiseData > 3000f && this.mMicCallback != null) {
            this.mMicCallback.detectSound(noiseData);
        }

        try {
            Thread.sleep(35);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMicCallback(IMicCallback callback) {
        this.mMicCallback = callback;
    }
}
