package com.ma.timescrollerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class TimeScroller extends RelativeLayout {

    String TAG = getClass().getSimpleName();

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

        timeScrollerIndicatorView.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.e(TAG, "onDrag: " + event.toString());
                return false;
            }
        });
        addView(timeScrollerView);
        addView(timeScrollerIndicatorView);
    }

    public void init() {
        TimeScrollerView timeScrollerView = new TimeScrollerView(getContext(), null);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}
