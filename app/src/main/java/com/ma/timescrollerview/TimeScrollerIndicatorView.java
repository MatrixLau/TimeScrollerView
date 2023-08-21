package com.ma.timescrollerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class TimeScrollerIndicatorView extends View {

    private String TAG = getClass().getSimpleName();

    private Paint indicatorPaint;

    private int indicatorColor = Color.BLUE;
    private float indicatorStrokeWidth = getDp(10);
    private float widthPerMin = 0f;
    private int hour = 2;
    private int min = 19;
    private PointF startPoint = new PointF();
    private PointF endPoint = new PointF();

    private boolean isMoving = false;
    private boolean isInit = true;

    private Handler initHandler = new Handler();
    private Runnable initTask = new Runnable() {
        @Override
        public void run() {
            isInit = true;
            invalidate();
        }
    };

    public TimeScrollerIndicatorView(Context context) {
        super(context);
        init();
    }

    public TimeScrollerIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeScrollerIndicatorView);

        indicatorStrokeWidth = typedArray.getDimension(R.styleable.TimeScrollerIndicatorView_TimeScrollerIndicatorView_stroke_width, indicatorStrokeWidth);
        indicatorColor = typedArray.getColor(R.styleable.TimeScrollerIndicatorView_TimeScrollerIndicatorView_indicator_color, indicatorColor);

        typedArray.recycle();
        init();
    }

    public TimeScrollerIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TimeScrollerIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        indicatorPaint = new Paint();
        indicatorPaint.setColor(indicatorColor);
        indicatorPaint.setStrokeWidth(indicatorStrokeWidth);
        indicatorPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        widthPerMin = (float) width / 1440;

        float additionWidth = 0f;

        if (hour >= 0 && min >= 0) {
            additionWidth += ((hour * 60) + min) * widthPerMin;
        }

        if (isInit) {
            startPoint.set(paddingLeft + additionWidth, paddingTop);
            endPoint.set(paddingLeft + additionWidth, paddingTop + height);
            isInit = false;
        }

        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, indicatorPaint);

    }

    public void setTime(int hour, int min) {
        this.hour = hour;
        this.min = min;
        invalidate();
    }

    /**
     * 获取统一化像素大小
     *
     * @param dp 传入的值
     * @return 转化成统一标准的值
     */
    public float getDp(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isMoving) {
            startPoint.x = event.getX(0);
            endPoint.x = event.getX(0);
            invalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            isMoving = false;
            initHandler.removeCallbacks(initTask);
            initHandler.postDelayed(initTask, 5000);
            return false;
        }
        //点击到标记线
        if (event.getX(0) >= startPoint.x - indicatorStrokeWidth / 2f && event.getX(0) <= startPoint.x + indicatorStrokeWidth / 2f
                && event.getY(0) >= startPoint.y && event.getY(0) <= endPoint.y) {
            Log.w(TAG, "onTouchEvent: " + event.toString());
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                isMoving = true;
            }
        }
        return true;
    }
}
