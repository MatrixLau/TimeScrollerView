package com.ma.timescrollerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TimeScroller extends RelativeLayout {

    private String TAG = getClass().getSimpleName();

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
        TimeScrollerView timeScrollerView = new TimeScrollerView(getContext(), attrs);
        TimeScrollerIndicatorView timeScrollerIndicatorView = new TimeScrollerIndicatorView(getContext(), attrs);

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
                Log.e(TAG, "onDragFinished: index:" + timeScrollerView.getStartTimeSectionFromCoordinate(x));
            }
        });
//        timeScrollerIndicatorView.setOnClock(true);
        addView(timeScrollerIndicatorView);
    }

    public void init() {
        TimeScrollerView timeScrollerView = new TimeScrollerView(getContext());
        TimeScrollerIndicatorView timeScrollerIndicatorView = new TimeScrollerIndicatorView(getContext());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}
