package com.mikepenz.crossfadedrawerlayout.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mikepenz.crossfadedrawerlayout.ApplyTransformationListener;
import com.mikepenz.crossfadedrawerlayout.animation.ResizeWidthAnimation;
import com.mikepenz.materialize.util.UIUtils;
import com.mikepenz.materialize.view.ScrimInsetsRelativeLayout;

/**
 * Created by mikepenz on 20.10.15.
 */
public class CrossfadeDrawerLayout extends DrawerLayout {
    private static final int DEFAULT_ANIMATION = 200;

    private boolean mDrawerOpened = false;

    private float mTouchDown = -1;
    private float mPrevTouch = -1;

    private CrossfadeListener mCrossfadeListener;

    private int mMinWidth = 0;
    private int mMaxWidth = 0;

    private ScrimInsetsRelativeLayout mContainer;
    private ViewGroup mSmallView;
    private ViewGroup mLargeView;

    private boolean mIsCrossfaded = false;

    private boolean mSliderOnRight = false;

    public CrossfadeDrawerLayout(Context context) {
        super(context);
        init(context);
    }

    public CrossfadeDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CrossfadeDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context ctx) {
        super.addDrawerListener(drawerListener);
        //define default valuse for min and max
        mMinWidth = (int) UIUtils.convertDpToPixel(72, ctx);
        mMaxWidth = (int) UIUtils.convertDpToPixel(200, ctx);
    }

    public void setMinWidthPx(int minWidth) {
        this.mMinWidth = minWidth;
    }

    public void setMaxWidthPx(int maxWidth) {
        this.mMaxWidth = maxWidth;
    }

    public ViewGroup getSmallView() {
        return mSmallView;
    }

    public ViewGroup getLargeView() {
        return mLargeView;
    }

    public ScrimInsetsRelativeLayout getContainer() {
        return mContainer;
    }

    public boolean isCrossfaded() {
        return mIsCrossfaded;
    }

    public CrossfadeDrawerLayout withSliderOnRight(boolean mSliderOnRight) {
        this.mSliderOnRight = mSliderOnRight;
        return this;
    }

    /**
     * defines a CrossfadeListener which is called when you slide the crossfader
     *
     * @param crossfadeListener
     * @return
     */
    public CrossfadeDrawerLayout withCrossfadeListener(CrossfadeListener crossfadeListener) {
        this.mCrossfadeListener = crossfadeListener;
        return this;
    }

    @Override
    public void addView(View child, int index) {
        child = wrapSliderContent(child, index);
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        child = wrapSliderContent(child, index);
        super.addView(child, index, params);
    }

    /**
     * this will wrap the view which is added to the slider into another layout so we can then overlap the small and large view
     *
     * @param child
     * @param index
     * @return
     */
    private View wrapSliderContent(View child, int index) {
        //TODO !!
        if (index == 1 && child.getId() != -1) {
            mLargeView = (ViewGroup) child;
            mContainer = new ScrimInsetsRelativeLayout(getContext());
            mContainer.setGravity(Gravity.START);
            mContainer.setLayoutParams(child.getLayoutParams());

            mContainer.addView(mLargeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            mSmallView = new LinearLayout(getContext());
            mContainer.addView(mSmallView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            UIUtils.setAlpha(mLargeView, 0);
            mLargeView.setVisibility(View.GONE);

            //correct fitsSystemWindows handling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mContainer.setFitsSystemWindows(true);
                mSmallView.setFitsSystemWindows(true);
            }

            return mContainer;
        }
        return child;
    }

    public DrawerListener drawerListener = new DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mDrawerOpened = slideOffset == 1;
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            mDrawerOpened = true;
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            MarginLayoutParams lp = (MarginLayoutParams) drawerView.getLayoutParams();
            lp.width = mMinWidth;
            drawerView.setLayoutParams(lp);

            //revert alpha :D
            UIUtils.setAlpha(mSmallView, 1);
            mSmallView.bringToFront();
            UIUtils.setAlpha(mLargeView, 0);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (mDrawerOpened) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mTouchDown = motionEvent.getX();
                mPrevTouch = motionEvent.getX();
                return super.dispatchTouchEvent(motionEvent);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                boolean click = mTouchDown == mPrevTouch;
                mTouchDown = -1;
                mPrevTouch = -1;

                MarginLayoutParams lp = (MarginLayoutParams) mContainer.getLayoutParams();
                float percentage = calculatePercentage(lp.width);
                if (percentage > 50) {
                    fadeUp(DEFAULT_ANIMATION);
                } else {
                    fadeDown(DEFAULT_ANIMATION);
                }

                if (click) {
                    return super.dispatchTouchEvent(motionEvent);
                } else {
                    return true;
                }
            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && mTouchDown != -1) {
                MarginLayoutParams lp = (MarginLayoutParams) mContainer.getLayoutParams();
                //the current drawer width
                float diff = motionEvent.getX() - mTouchDown;

                if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL || mSliderOnRight) {
                    diff = diff * (-1);
                }

                if (diff == 0) {
                    //no difference nothing to do
                    //return super.dispatchTouchEvent(motionEvent);
                } else if (diff > 0 && lp.width <= mMaxWidth && (lp.width + diff) < mMaxWidth && lp.width >= mMinWidth) {
                    lp.width = (int) (lp.width + diff);
                    mContainer.setLayoutParams(lp);
                    mTouchDown = motionEvent.getX();
                    overlapViews(lp.width);
                } else if (diff < 0 && lp.width >= mMinWidth && (lp.width + diff) > mMinWidth) {
                    lp.width = (int) (lp.width + diff);
                    mContainer.setLayoutParams(lp);
                    mTouchDown = motionEvent.getX();
                    overlapViews(lp.width);
                } else if (lp.width < mMinWidth) {
                    lp.width = mMinWidth;
                    mContainer.setLayoutParams(lp);
                    mDrawerOpened = false;
                    mTouchDown = -1;
                    overlapViews(mMinWidth);
                } else if ((lp.width + diff) < mMinWidth) {
                    //return super.dispatchTouchEvent(motionEvent);
                }
                //return true;
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            return super.onTouchEvent(motionEvent);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    @Override
    public void openDrawer(int gravity) {
        mDrawerOpened = true;
        super.openDrawer(gravity);
    }

    @Override
    public void openDrawer(View drawerView) {
        mDrawerOpened = true;
        super.openDrawer(drawerView);
    }

    /**
     * crossfade the small to the large view (with default animation time)
     */
    public void crossfade() {
        crossfade(DEFAULT_ANIMATION);
    }

    /**
     * crossfade the small to the large view
     *
     * @param duration
     */
    public void crossfade(int duration) {
        if (isCrossfaded()) {
            fadeDown(duration);
        } else {
            fadeUp(duration);
        }
    }

    /**
     * animate to the large view
     *
     * @param duration
     */
    public void fadeUp(int duration) {
        //animate up
        mContainer.clearAnimation();
        ResizeWidthAnimation anim = new ResizeWidthAnimation(mContainer, mMaxWidth, new ApplyTransformationListener() {
            @Override
            public void applyTransformation(int width) {
                overlapViews(width);
            }
        });
        anim.setDuration(duration);
        mContainer.startAnimation(anim);
    }

    /**
     * animate to the small view
     *
     * @param duration
     */
    public void fadeDown(int duration) {
        //fade down
        mContainer.clearAnimation();
        ResizeWidthAnimation anim = new ResizeWidthAnimation(mContainer, mMinWidth, new ApplyTransformationListener() {
            @Override
            public void applyTransformation(int width) {
                overlapViews(width);
            }
        });
        anim.setDuration(duration);
        mContainer.startAnimation(anim);
    }

    /**
     * calculate the percentage to how many percent the slide is already visible
     *
     * @param width
     * @return
     */
    private float calculatePercentage(int width) {
        int absolute = mMaxWidth - mMinWidth;
        int current = width - mMinWidth;
        float percentage = 100.0f * current / absolute;
        //we can assume that we are crossfaded if the percentage is > 90
        mIsCrossfaded = percentage > 90;
        return percentage;
    }

    //remember the previous width to optimize performance
    private int mWidth = -1;

    /**
     * overlap the views and provide the crossfade effect
     *
     * @param width
     */
    private void overlapViews(int width) {
        if (width == mWidth) {
            return;
        }
        //remember this width so it is't processed twice
        mWidth = width;


        float percentage = calculatePercentage(width);
        float alpha = percentage / 100;

        UIUtils.setAlpha(mSmallView, 1);
        mSmallView.setClickable(false);
        mLargeView.bringToFront();
        UIUtils.setAlpha(mLargeView, alpha);
        mLargeView.setClickable(true);
        mLargeView.setVisibility(alpha > 0.01f ? View.VISIBLE : View.GONE);

        //notify the crossfadeListener
        if (mCrossfadeListener != null) {
            mCrossfadeListener.onCrossfade(mContainer, calculatePercentage(width), width);
        }
    }

    public interface CrossfadeListener {
        void onCrossfade(View containerView, float currentSlidePercentage, int slideOffset);
    }
}
