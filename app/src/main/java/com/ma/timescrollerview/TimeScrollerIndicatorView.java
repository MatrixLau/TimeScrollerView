package com.ma.timescrollerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class TimeScrollerIndicatorView extends View {

    String TAG = getClass().getSimpleName();

    Paint indicatorPaint;

    int indicatorColor = Color.BLUE;
    float indicatorStrokeWidth = getDp(10);
    float widthPerMin = 0f;
    int hour = 2;
    int min = 19;

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

        canvas.drawLine(paddingLeft + additionWidth, paddingTop, paddingLeft + additionWidth, paddingTop + height, indicatorPaint);

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
}
