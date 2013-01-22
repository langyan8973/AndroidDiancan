package com.custom.animation;

import android.R.bool;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class HistoryRotateAnim extends Animation {
	private float mFromDegree;   
    private float mToDegree;   
    private float mCenterX;   
    private float mCenterY;   
    private float mLeft;   
    private float mTop;   
    private Camera mCamera;   
    private static final String TAG = "HistoryRotateAnim";  
    private boolean b;
  
    public HistoryRotateAnim(float fromDegree, float toDegree, float left, float top,   
            float centerX, float centerY,boolean b1) {   
        this.mFromDegree = fromDegree;   
        this.mToDegree = toDegree;   
        this.mLeft = left;   
        this.mTop = top;   
        this.mCenterX = centerX;   
        this.mCenterY = centerY;   
        b=b1;
    }   
  
    @Override   
    public void initialize(int width, int height, int parentWidth,   
            int parentHeight) {   
        super.initialize(width, height, parentWidth, parentHeight);   
        mCamera = new Camera();   
        this.setDuration(600);
    }   
  
    @Override   
    protected void applyTransformation(float interpolatedTime, Transformation t) {   
        final float FromDegree = mFromDegree;   
        float degrees = FromDegree + (mToDegree - mFromDegree)   
                * interpolatedTime;   
        final float centerX = mCenterX;   
        final float centerY = mCenterY;   
        final Matrix matrix = t.getMatrix();  
  
        if (degrees <= -150.0f) {   
             degrees = -180.0f;   
             mCamera.save();   
             mCamera.rotateY(degrees);   
             mCamera.getMatrix(matrix);   
             mCamera.restore();   
        } else if(degrees >=150.0f){   
            degrees = 180.0f;   
            mCamera.save();   
            mCamera.rotateY(degrees);   
            mCamera.getMatrix(matrix);   
            mCamera.restore();   
        }else{   
            mCamera.save();   
                       //这里很重要哦。  
            if(b)
            {
            	mCamera.translate(0, 0, centerX*interpolatedTime);
            }
            else {
            	mCamera.translate(0, 0, centerX*(1-interpolatedTime));
			}  
            mCamera.rotateY(degrees);   
//            mCamera.translate(0, 0, 0);   
            mCamera.getMatrix(matrix);   
            mCamera.restore();   
        }   
  
        matrix.preTranslate(-centerX, -centerY);   
        matrix.postTranslate(centerX, centerY);   
    }
}
