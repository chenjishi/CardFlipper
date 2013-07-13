package com.chenjishi.CardFlipper;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA.
 * User: chenjishi
 * Date: 13-7-12
 * Time: 下午3:54
 * To change this template use File | Settings | File Templates.
 */
public abstract class CardsActivity extends Activity implements View.OnTouchListener, GestureDetector.OnGestureListener {
    protected static final int MIN_SWIPE_DISTANCE = 60;
    protected static final int INIT_CARD_COUNT = 4;

    private GestureDetector mGestureDetector;
    protected FrameLayout mContainer;
    protected LayoutInflater mInflater;

    private FrameLayout.LayoutParams mBottomLayer;
    private FrameLayout.LayoutParams mMiddleLayer;
    private FrameLayout.LayoutParams mTopLayer;

    private TranslateAnimation mOneStepUpAnimation;
    private TranslateAnimation mOneStepDownAnimation;

    protected int mCurrentCardId;
    protected int mDisplayWidth;
    protected int mCardsCount;
    private boolean isAnimationFinished = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_layout);

        mGestureDetector = new GestureDetector(this, this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDisplayWidth = metrics.widthPixels;

        mContainer = (FrameLayout) findViewById(R.id.cards_container);
        mContainer.setOnTouchListener(this);

        mBottomLayer = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBottomLayer.gravity = Gravity.TOP;
        mBottomLayer.setMargins(getValue(40), getValue(50), getValue(20), getValue(20));

        mMiddleLayer = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mMiddleLayer.gravity = Gravity.TOP;
        mMiddleLayer.setMargins(getValue(30), getValue(40), getValue(30), getValue(30));

        mTopLayer = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mTopLayer.gravity = Gravity.TOP;
        mTopLayer.setMargins(getValue(20), getValue(30), getValue(40), getValue(40));

        mOneStepUpAnimation = new TranslateAnimation(8, 0, 8, 0);
        mOneStepUpAnimation.setFillAfter(true);
        mOneStepUpAnimation.setInterpolator(new AccelerateInterpolator());
        mOneStepUpAnimation.setDuration(400);
        mOneStepUpAnimation.reset();

        mOneStepDownAnimation = new TranslateAnimation(-8, 0, -8, 0);
        mOneStepDownAnimation.setFillAfter(true);
        mOneStepDownAnimation.setInterpolator(new DecelerateInterpolator());
        mOneStepDownAnimation.setDuration(400);
        mOneStepDownAnimation.reset();

        mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        loadData();
    }

    protected void setupView() {
        for (int i = mCurrentCardId - 3; i <= mCurrentCardId; i++) {
            FrameLayout.LayoutParams layoutParams;

            if (i == mCurrentCardId - 3 || i == mCurrentCardId - 2) {
                layoutParams = mBottomLayer;
            } else if (i == mCurrentCardId - 1) {
                layoutParams = mMiddleLayer;
            } else {
                layoutParams = mTopLayer;
            }

            createCards(i, layoutParams);
        }
    }

    protected abstract void createCards(int index, FrameLayout.LayoutParams layoutParams);

    protected abstract void loadData();

    protected abstract FrameLayout getCardView();

    protected abstract void handleFlipBack();

    protected abstract void setFrontView(View v, int index);

    private final class DisplayBackView implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mContainer.post(new Runnable() {
                @Override
                public void run() {
                    handleFlipBack();
                }
            });
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    private int getValue(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics());
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        int count = mContainer.getChildCount();
        View view = mContainer.getChildAt(count - 1);

        FrameLayout currentCard = (FrameLayout) view.findViewById(R.id.view_card_container);

        FrameLayout frontView = (FrameLayout) view.findViewById(R.id.view_card_front);
        FrameLayout backView = (FrameLayout) view.findViewById(R.id.view_card_back);

        FlipAnimation animation = new FlipAnimation(frontView, backView, frontView.getWidth() / 2, backView.getHeight() / 2);
        if (frontView.getVisibility() == View.GONE) {
            animation.reverse();
        }

        animation.setAnimationListener(new DisplayBackView());

        currentCard.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
        currentCard.startAnimation(animation);

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        if (motionEvent.getX() - motionEvent2.getX() > MIN_SWIPE_DISTANCE) {
            if (!isAnimationFinished || mCurrentCardId == 0) return false;

            final int count = mContainer.getChildCount();
            final View recycleView = mContainer.getChildAt(count - 1);

            Animation hideAnimation = new TranslateAnimation(0, -mDisplayWidth, 0, -10);
            hideAnimation.setDuration(250);
            hideAnimation.setFillAfter(true);
            hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimationFinished = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mContainer.removeViewAt(count - 1);
                    mContainer.addView(getCardView(), 0, mBottomLayer);
                    mContainer.requestLayout();
                    isAnimationFinished = true;
                    mCurrentCardId--;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            recycleView.clearAnimation();
            recycleView.startAnimation(hideAnimation);

            mContainer.getChildAt(count - 2).clearAnimation();
            mContainer.getChildAt(count - 2).setLayoutParams(mTopLayer);
            mContainer.getChildAt(count - 2).startAnimation(mOneStepUpAnimation);

            if (mCurrentCardId > 1) {
                mContainer.getChildAt(count - 3).clearAnimation();
                mContainer.getChildAt(count - 3).setLayoutParams(mMiddleLayer);
                mContainer.getChildAt(count - 3).startAnimation(mOneStepUpAnimation);
            }
        }

        if (motionEvent2.getX() - motionEvent.getX() > MIN_SWIPE_DISTANCE) {
            if (!isAnimationFinished || mCurrentCardId == mCardsCount - 1) return false;

            Animation showAnimation = new TranslateAnimation(-mDisplayWidth, 0, -10, 0);
            showAnimation.setDuration(250);
            showAnimation.setFillAfter(true);
            showAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimationFinished = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCurrentCardId++;
                    isAnimationFinished = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            FrameLayout recycleView = (FrameLayout) mContainer.getChildAt(0);

            setFrontView(recycleView, mCurrentCardId + 1);

            recycleView.clearAnimation();
            recycleView.setLayoutParams(mTopLayer);
            recycleView.startAnimation(showAnimation);
            recycleView.bringToFront();

            mContainer.getChildAt(2).clearAnimation();
            mContainer.getChildAt(2).setLayoutParams(mMiddleLayer);
            mContainer.getChildAt(2).startAnimation(mOneStepDownAnimation);

            if (mCurrentCardId > 0) {
                mContainer.getChildAt(1).clearAnimation();
                mContainer.getChildAt(1).setLayoutParams(mBottomLayer);
                mContainer.getChildAt(1).startAnimation(mOneStepDownAnimation);
            }
        }

        return true;
    }
}
