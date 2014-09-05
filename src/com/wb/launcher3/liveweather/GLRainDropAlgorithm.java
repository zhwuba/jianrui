package com.wb.launcher3.liveweather;

import java.nio.FloatBuffer;
import java.util.Random;

import com.dutils.math.Matrix4f;
import com.dutils.math.Vector3f;

public class GLRainDropAlgorithm {
    private static final float CONSTANTVALUE = 0f;
    private static final float MULTIPLIER = 0.2f;
    private static final Vector3f ORIGIN = Vector3f.ZERO;;
    private static final Random RAND = new Random();
    private static final Vector3f YAXIS = new Vector3f(0f, -1f, 0f);;
    private Vector3f mAccele;
    private Vector3f mDeltaVec;
    private float mFadeLevel;
    private Vector3f mGravityForce;
    private float mHorizontalBound;
    private Vector3f mPosition;
    private float mQuality;
    private Vector3f mRotateCenterPt;
    private Matrix4f mRotateMat;
    private float mSlideTime;
    private Vector3f mSpeed;
    private Vector3f mSpeed0;
    private boolean mSpeedFire;
    private float mSpeedIterator;
    private int mTagRainDropDepth;
    private float mTime;
    private float mTimeBase;
    private float mTransition;
    private Matrix4f mTranslateMat0;
    private Matrix4f mTranslateMat1;
    private float mVerticalBound;
    private float preTransition;

    private GLRainDropAlgorithm(float horizontalBound, float verticalBound, float time, boolean isSmallRain) {
        this.mPosition = new Vector3f();
        this.mSpeed0 = new Vector3f();
        this.mSpeed = new Vector3f();
        this.mAccele = new Vector3f();
        this.mRotateCenterPt = new Vector3f(0f, 0f, 0f);
        this.mDeltaVec = Vector3f.TEMP;
        this.mTranslateMat0 = new Matrix4f();
        this.mTranslateMat1 = new Matrix4f();
        this.mRotateMat = new Matrix4f();
        this.mGravityForce = new Vector3f(0f, -500f, 0f);
        this.mQuality = 1f;
        this.mFadeLevel = 1f;
        this.mTimeBase = 0f;
        this.mTime = 0f;
        this.mHorizontalBound = 0f;
        this.mVerticalBound = 0f;
        this.mTransition = 0f;
        this.preTransition = 0f;
        this.mTagRainDropDepth = 0;
        this.mSpeedFire = false;
        this.mSpeedIterator = 1f;
        this.mSlideTime = 0f;
        this.mHorizontalBound = horizontalBound;
        this.mVerticalBound = verticalBound;
        this.resetParameters(time, isSmallRain, isSmallRain, false);
    }

    private void clearAllParameters(float time) {
        GLRainDropAlgorithm.RAND.setSeed(System.nanoTime());
        this.mPosition.x = (this.randomFactor() - 0.5f) * this.mHorizontalBound;
        this.mPosition.y = this.mVerticalBound * 0.5f;
        this.speedAttenuationFire();
        this.setRainDropDepth(0.5f, 0.3f, 0.1f, -0.2f, -0.3f, -0.4f);
        this.mSpeed0.set(Vector3f.ZERO);
        this.setSmallRainDropSpeedY();
        this.mSpeed.set(this.mSpeed0);
        this.mGravityForce.set(0f, -10f, 0f);
        this.mAccele.set(this.mGravityForce.x / this.mQuality, this.mGravityForce.y / this.mQuality,
                this.mGravityForce.z / this.mQuality);
        this.setRainDropFadeLevel();
        this.mTimeBase = time;
        this.mTime = 0f;
        this.mTransition = 0f;
        this.mSlideTime = 0f;
    }

    private float getAngle(Vector3f vec1, Vector3f vec2) {
        return ((float) Math
                .acos((vec1.dot(vec2) / (Vector3f.distance(vec1, GLRainDropAlgorithm.ORIGIN) * Vector3f
                        .distance(vec2, GLRainDropAlgorithm.ORIGIN)))));
    }

    public static GLRainDropAlgorithm getInstance(float horizontalBound, float verticalBound, boolean isSmallRain) {
        return new GLRainDropAlgorithm(horizontalBound, verticalBound, 2f * System.nanoTime() / 1000000000, isSmallRain);
    }

    public float getRainDropFadeLevel() {
        return this.mFadeLevel;
    }

    public FloatBuffer glRainDropTransform() {
        float angle = this.getAngle(GLRainDropAlgorithm.YAXIS, this.mSpeed);
        if (this.mSpeed.x >= 0f) {
            angle = -angle;
        }

        this.mTranslateMat0.setIdentity();
        this.mTranslateMat1.setIdentity();
        this.mRotateMat.setIdentity();
        this.mTranslateMat0.setTranslation(this.mPosition.x, this.mPosition.y, this.mPosition.z);
        this.mRotateMat.rotate(angle, 0f, 0f, 1f);
        this.mTranslateMat0.mul(this.mRotateMat);
        this.mTranslateMat1.setTranslation(-this.mRotateCenterPt.x, -this.mRotateCenterPt.y, -this.mRotateCenterPt.z);
        this.mTranslateMat0.mul(this.mTranslateMat1);
        return this.mTranslateMat0.asFloatBuffer();
    }

    private void horizontalForce(boolean isSmallRain) {
        if (Math.abs(this.mTransition) < 0.01f) {
            this.mTransition = 0f;
        } else {
            this.mTransition = this.horizontalForceKernel(this.mTransition);
        }

        float absTransition = Math.abs(this.mTransition);
        if (absTransition > 300f) {
            absTransition = this.mTransition / absTransition * 300f;
        } else {
            absTransition = this.mTransition;
        }

        if (isSmallRain) {
            this.mGravityForce.x = absTransition;
        } else {
            this.mGravityForce.x = -10f + absTransition;
        }
    }

    private float horizontalForceKernel(float preVal) {
        return 0.2f * preVal;
    }

    private float[] nextAccel() {
        return new float[] { this.mAccele.x, this.mAccele.y, this.mAccele.z };
    }

    private float[] nextPosition() {
        return new float[] { this.mPosition.x, this.mPosition.y, this.mPosition.z };
    }

    private float[] nextSpeed() {
        return new float[] { this.mSpeed.x, this.mSpeed.y, this.mSpeed.z };
    }

    private float randomFactor() {
        return GLRainDropAlgorithm.RAND.nextFloat();
    }

    public float randomScaleValue(float amplitude) {
        return this.randomFactor() * amplitude;
    }

    public int randomTextureValue() {
        return GLRainDropAlgorithm.RAND.nextInt(2);
    }

    private void resetParameters(float time, boolean shouldUpYParam, boolean isSmallRain, boolean isUpdate) {
        float v1 = 0.5f;

        GLRainDropAlgorithm.RAND.setSeed(System.nanoTime());
        this.mPosition.x = (this.randomFactor() - v1) * this.mHorizontalBound;

        if (shouldUpYParam) {
            this.mPosition.y = (this.randomFactor() + v1) * this.mVerticalBound;
            this.speedAttenuationFire();
        } else if (isUpdate) {
            this.mPosition.y = this.mVerticalBound * v1;
        } else {
            this.mPosition.y = (this.randomFactor() + v1) * this.mVerticalBound * 1.2f;
        }

        this.setRainDropDepth(v1, 0.3f, 0.1f, -0.2f, -0.3f, -0.4f);
        float absTransition = Math.abs(this.mTransition);
        if (absTransition > 150f) {
            absTransition = this.mTransition / absTransition * 150f;
        } else {
            absTransition = this.mTransition;
        }

        this.mSpeed0.set(absTransition, 0f, 0f);
        if (isSmallRain) {
            this.setSmallRainDropSpeedY();
            this.mGravityForce.set(0f, -10f, 0f);
        } else {
            this.setRainDropSpeedY();
            this.mGravityForce.set(0f, -500f, 0f);
        }

        this.mSpeed.set(this.mSpeed0);
        this.horizontalForce(isSmallRain);
        this.mAccele.set(this.mGravityForce.x / this.mQuality, this.mGravityForce.y / this.mQuality,
                this.mGravityForce.z / this.mQuality);
        this.setRainDropFadeLevel();
        this.mTimeBase = time;
        this.mTime = 0f;
    }

    public void resetScreenOffsetX() {
        this.speedAttenuationFire();
        this.mTransition = 0f;
        this.preTransition = 0f;
    }

    public void setAdaptRotateHeight(float randomHeight) {
        this.mRotateCenterPt.set(0f, randomHeight / 2f, 0f);
    }

    private void setRainDropDepth(float d5, float d4, float d3, float d2, float d1, float d0) {
        float v0 = 1.2f * this.mVerticalBound;

        switch (this.mTagRainDropDepth) {
        case 0:
            this.mPosition.z = ((d1 - d0) * this.randomFactor() + d0) * v0;
            break;
        case 1:
            this.mPosition.z = ((d3 - d2) * this.randomFactor() + d2) * v0;
            break;
        default:
            this.mPosition.z = ((d5 - d4) * this.randomFactor() + d4) * v0;
            break;
        }
    }

    private void setRainDropFadeLevel() {
        switch (this.mTagRainDropDepth) {
        case 0:
            this.mFadeLevel = 0.4f + 0.1f * this.randomFactor();
            break;
        case 1:
            this.mFadeLevel = 0.5f + 0.15f * this.randomFactor();
            break;
        default:
            this.mFadeLevel = 0.6f + 0.2f * this.randomFactor();
            break;
        }
    }

    private void setRainDropSpeedY() {
        float v4 = 0.2f;
        float v3 = 0.3f;

        switch (this.mTagRainDropDepth) {
        case 0:
            this.mSpeed0.y = -(this.randomFactor() * v3 + v3) * this.mVerticalBound;
            break;
        case 1:
            this.mSpeed0.y = -(0.4f + this.randomFactor() * v3) * this.mVerticalBound
                    - this.speedAttenuationExec(0.5f * this.mVerticalBound, v4);
            break;
        default:
            this.mSpeed0.y = -(0.6f + this.randomFactor() * v4) * this.mVerticalBound;
            break;
        }
    }

    public void setScreenOffsetX(float transition) {
        if (transition != this.preTransition) {
            this.mTransition += transition;
        }

        this.preTransition = transition;
    }

    private void setSmallRainDropSpeedY() {
        float v3 = 0.2f;

        switch (this.mTagRainDropDepth) {
        case 0:
            this.mSpeed0.y = -(0.1f * this.randomFactor() + v3) * this.mVerticalBound;
            break;
        case 1:
            this.mSpeed0.y = -(this.randomFactor() * v3 + v3) * this.mVerticalBound;
            break;
        default:
            this.mSpeed0.y = -(0.25f + 0.11f * this.randomFactor()) * this.mVerticalBound;
            break;
        }
    }

    public void setSmallRainScreenOffsetX(float transition) {
        if (transition != this.preTransition) {
            this.mTransition += transition;
            if (this.mSlideTime <= 0f) {
                this.mSlideTime = 2f * System.nanoTime() / 1000000000;
            }
        }

        this.preTransition = transition;
        this.mGravityForce.x = 0.8f * transition;
        this.mSpeed0.x = 0.6f * transition;
        this.mAccele.set(this.mGravityForce.x / this.mQuality, this.mGravityForce.y / this.mQuality,
                this.mGravityForce.z / this.mQuality);
    }

    public void setTagRainDropDepth(int tagRainDropDepth) {
        this.mTagRainDropDepth = tagRainDropDepth;
    }

    private float speedAttenuationExec(float amplitude, float attenu) {
        if (this.mSpeedFire) {
            this.mSpeedIterator = ((float) (((this.mSpeedIterator)) * Math.exp(((-attenu)))));
            if (this.mSpeedIterator < 0.01f) {
                this.mSpeedIterator = 1f;
                this.mSpeedFire = false;
            }

            return amplitude * this.mSpeedIterator;
        } else {
            return 0;
        }
    }

    private void speedAttenuationFire() {
        this.mSpeedFire = true;
        this.mSpeedIterator = 1f;
    }

    public void updateRainDropInfo(float curTime) {
        if (this.mPosition.y < -this.mVerticalBound / 2f) {
            this.resetParameters(curTime, false, false, true);
        }

        float deltaTime = curTime - this.mTimeBase - this.mTime;
        float v1 = 0.5f * (deltaTime * deltaTime + this.mTime * 2f * deltaTime);

        this.mDeltaVec.x = this.mSpeed0.x * deltaTime + this.mAccele.x * v1;
        this.mDeltaVec.y = this.mSpeed0.y * deltaTime + this.mAccele.y * v1;
        this.mDeltaVec.z = this.mSpeed0.z * deltaTime + this.mAccele.z * v1;
        this.mPosition.add(this.mDeltaVec);
        this.mDeltaVec.x = this.mAccele.x * deltaTime;
        this.mDeltaVec.y = this.mAccele.y * deltaTime;
        this.mDeltaVec.z = this.mAccele.z * deltaTime;
        this.mSpeed.add(this.mDeltaVec);
        this.mTime = curTime - this.mTimeBase;
    }

    public void updateSmallRainDropInfo(float curTime) {
        // float v2;
        float v5 = 0.01f;

        if (this.mPosition.y < -this.mVerticalBound / 2f) {
            this.clearAllParameters(curTime);
        }

        float deltaTime = curTime - this.mTimeBase - this.mTime;
        float v1 = 0.5f * (deltaTime * deltaTime + this.mTime * 2f * deltaTime);
        this.mDeltaVec.x = this.mSpeed0.x * deltaTime + this.mAccele.x * v1;
        this.mDeltaVec.y = this.mSpeed0.y * deltaTime + this.mAccele.y * v1;
        this.mDeltaVec.z = this.mSpeed0.z * deltaTime + this.mAccele.z * v1;
        this.mPosition.add(this.mDeltaVec);
        this.mDeltaVec.x = this.mAccele.x * deltaTime;

        if (Math.abs(this.mAccele.x) < v5 && Math.abs(this.mSpeed.x) > v5) {
            if (this.mSpeed.x >= v5) {
                this.mDeltaVec.x = -3f;
            } else {
                this.mDeltaVec.x = 3f;
            }
        }

        this.mDeltaVec.y = this.mAccele.y * deltaTime;
        this.mDeltaVec.z = this.mAccele.z * deltaTime;
        this.mSpeed.add(this.mDeltaVec);
        this.mTime = curTime - this.mTimeBase;
    }
}
