package com.ma.timescrollerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TimeScroller extends RelativeLayout {

    private String TAG = getClass().getSimpleName();

    public TimeScrollerView timeScrollerView;
    public TimeScrollerIndicatorView timeScrollerIndicatorView;

    public TimeScroller(Context context) {
        super(context);
        init();
    }

    public TimeScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TimeScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public TimeScroller(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        timeScrollerView = new TimeScrollerView(getContext(), attrs);
        timeScrollerIndicatorView = new TimeScrollerIndicatorView(getContext(), attrs);

        addView(timeScrollerView);
        timeScrollerIndicatorView.setCanvasBorder(timeScrollerView.getCornerRadius());
        timeScrollerIndicatorView.setOnIndicatorDragListener(new TimeScrollerIndicatorView.onIndicatorDragListener() {
            @Override
            public void onDragStarted(float x, float y) {

            }

            @Override
            public void onDragging(float x, float y) {
//                Log.e(TAG, "onDragging: x="+x+" y="+y);
            }

            @Override
            public void onDragFinished(float x, float y) {
                Log.e(TAG, "onDragFinished: x=" + x + " y=" + y);
                int index = timeScrollerView.getStartTimeSectionFromCoordinate(x);
                Log.e(TAG, "onDragFinished: index:" + index);
                if (index != -1) {
                    Log.e(TAG, "onDragFinished: time:" + timeScrollerView.getTimeScrollerData().getData().get(index - 1) + " - " + timeScrollerView.getTimeScrollerData().getData().get(index));

                }
            }
        });
//        timeScrollerIndicatorView.setOnClockTickingListener(new TimeScrollerIndicatorView.onClockTickingListener() {
//            @Override
//            public void onClockTicking(float x) {
//                Log.w(TAG, "onClockTicking: x=" + x);
//                Log.w(TAG, "onClockTicking: currentSectionIndex=" + timeScrollerView.getStartTimeSectionFromCoordinate(x));
//            }
//        });
        timeScrollerIndicatorView.setOnClock(true);
        addView(timeScrollerIndicatorView);
    }

    public void init() {
        timeScrollerView = new TimeScrollerView(getContext());
        timeScrollerIndicatorView = new TimeScrollerIndicatorView(getContext());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }


}
