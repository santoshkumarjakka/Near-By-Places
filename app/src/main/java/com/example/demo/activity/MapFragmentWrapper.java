package com.example.demo.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.demo.R;

public class MapFragmentWrapper extends RelativeLayout {

    public MapFragmentWrapper(Context context) {
        super(context);
        init(context);
    }

    public MapFragmentWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
                init(context);
    }

    public MapFragmentWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
                init(context);
    }

    interface OnDragListener {
        void onDragStart(ImageView mMarkImageView);

        void onDragEnd(ImageView mMarkImageView);
    }

    OnDragListener mOnDragListener;

    private ImageView mMarkImageView;
    private View mShadowView;
    RelativeLayout.LayoutParams params;

    public void init(Context context) {
        mMarkImageView = new ImageView(context);
        mMarkImageView.setImageResource(R.drawable.ic_marker_centered);

        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        mShadowView = new View(context);
        mShadowView.setBackgroundResource(R.drawable.map_pin_shadow);
        mShadowView.setVisibility(VISIBLE);

    }

    void animateUp() {
        if (mMarkImageView != null && mShadowView != null) {
            ObjectAnimator translateY = ObjectAnimator.ofFloat(mMarkImageView, "translationY", (float) mMarkImageView.getHeight() / 10);

            ObjectAnimator alphaShadow = ObjectAnimator.ofFloat(mShadowView,
                    "alpha",
                    1f,
                    0.6f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(translateY, alphaShadow);
            animatorSet.start();
        }
    }

    void animateDown() {
        if (mMarkImageView != null && mShadowView != null) {
            ObjectAnimator translateYInverse = ObjectAnimator.ofFloat(mMarkImageView,
                    "translationY",
                    mMarkImageView.getHeight() / 25);

            ObjectAnimator alphaShadowInverse = ObjectAnimator.ofFloat(mShadowView,
                    "alpha",
                    0.6f,
                    1f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(translateYInverse, alphaShadowInverse);
            animatorSet.start();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        removeView(mMarkImageView);
        removeView(mShadowView);
        addView(mMarkImageView, -1, params);
        addView(mShadowView, -1, params);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            animateUp();
            mOnDragListener.onDragStart(mMarkImageView);
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            animateDown();
            mOnDragListener.onDragEnd(mMarkImageView);
        }
        return super.dispatchTouchEvent(ev);

    }

    public void setOnDragListener(OnDragListener mOnDragListener) {
        this.mOnDragListener = mOnDragListener;
    }

}
