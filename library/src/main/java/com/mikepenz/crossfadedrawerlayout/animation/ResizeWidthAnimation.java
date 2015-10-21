package com.mikepenz.crossfadedrawerlayout.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.mikepenz.crossfadedrawerlayout.ApplyTransformationListener;

/**
 * animate the resizing if TOUCH_UP
 */
public class ResizeWidthAnimation extends Animation {
    private int mWidth;
    private int mStartWidth;
    private View mView;

    private ApplyTransformationListener mApplyTransformationListener;

    public ResizeWidthAnimation(View view, int width, ApplyTransformationListener applyTransformationListener) {
        mView = view;
        mWidth = width;
        mApplyTransformationListener = applyTransformationListener;
        mStartWidth = view.getWidth();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);
        mView.getLayoutParams().width = newWidth;
        //change opacity
        mApplyTransformationListener.applyTransformation(newWidth);
        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}