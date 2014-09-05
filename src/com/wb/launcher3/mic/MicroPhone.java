package com.wb.launcher3.mic;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class MicroPhone {
    private static final int MAX_FILESIZE_BYTES = 900000;
    protected static final int MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED = 801;
    private static final float MIC_DB_MAX = 0f;

    private boolean huffFlag;
    private MediaRecorder mMediaRecorder;
    private MediaRecorder.OnInfoListener mOnInfoListener;
    private static MicroPhone microPhoneInstance = null;
    private float noiseData;

    private MicroPhone() {
        super();

        this.mOnInfoListener = new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                switch (what) {
                case MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED: {
                    MicroPhone.this.closeMic();
                    MicroPhone.this.openMic();
                    break;
                }
                }
            }
        };
    }

    public void closeMic() {
        this.stopMediaRecorder();
    }

    public boolean detectHuff() {
        this.detectNoiseData();
        return this.huffFlag;
    }

    public void detectNoiseData() {
        int maxAmplitude = 0;

        if (this.mMediaRecorder != null) {
            maxAmplitude = this.mMediaRecorder.getMaxAmplitude();
            this.noiseData = maxAmplitude;
        }

        if (10f * Math.log(maxAmplitude) >= 100f) {
            this.huffFlag = true;
        } else {
            this.huffFlag = false;
        }
    }

    public static synchronized MicroPhone getInstance() {
        if (microPhoneInstance == null) {
            microPhoneInstance = new MicroPhone();
        }

        return microPhoneInstance;
    }

    public float getNoiseData() {
        this.detectNoiseData();
        return this.noiseData;
    }

    public void openMic() {
        this.setupMediaRecorder();
        this.startMediaRecorder();
    }

    private void setupMediaRecorder() {
        if (this.mMediaRecorder == null) {
            this.mMediaRecorder = new MediaRecorder();
            this.mMediaRecorder.setOnInfoListener(this.mOnInfoListener);
        }

        if (this.mMediaRecorder == null) {
            Log.e("MicroPhoneTools", "CAN NOT new a MediaRecorder instance, please check the permission!!");
            return;
        }

        try {
            this.mMediaRecorder.setAudioSource(1);
            this.mMediaRecorder.setOutputFormat(1);
            this.mMediaRecorder.setAudioEncoder(1);
            this.mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().toString() + "/test.amr");
            this.mMediaRecorder.setMaxFileSize(900000);
        } catch (Exception e) {
            Log.e("MicroPhoneTools", "setAudioSource failed");
            e.printStackTrace();
        }

        try {
            this.mMediaRecorder.prepare();
        } catch (Exception e) {
            Log.e("MicroPhoneTools", "Could not prepare MediaRecorder: " + e.toString());
            this.mMediaRecorder.release();
            this.mMediaRecorder = null;
        }
    }

    private void startMediaRecorder() {
        if (this.mMediaRecorder == null) {
            return;
        }

        try {
            this.mMediaRecorder.start();
        } catch (Exception e) {
            Log.e("MicroPhoneTools", "Could start MediaRecorder: " + e.toString());
            this.mMediaRecorder.release();
            this.mMediaRecorder = null;
        }
    }

    private void stopMediaRecorder() {
        if (this.mMediaRecorder != null) {
            this.mMediaRecorder.stop();
            this.mMediaRecorder.release();
            this.mMediaRecorder = null;
        }
    }
}
