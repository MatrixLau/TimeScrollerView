package com.ma.timescrollerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class TimeScrollerIndicatorView extends View {

    private String TAG = getClass().getSimpleName();

    private Path backgroundPath = new Path();
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

    private float rollBackSpeed = 12f;
    private Handler initHandler = new Handler();
    private Runnable initTask = new Runnable() {
        @Override
        public void run() {
            if (!isMoving) {
                isInit = true;
                invalidate();
            }
        }
    };

    private float canvasBorder = getDp(0);

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

        backgroundPath.addRoundRect(paddingLeft, paddingTop, getWidth() - paddingRight, getHeight() - paddingBottom, canvasBorder, canvasBorder, Path.Direction.CW);
        backgroundPath.close();
        canvas.clipPath(backgroundPath);

        widthPerMin = (float) width / 1440;

        float additionWidth = 0f;

        if (hour >= 0 && min >= 0) {
            additionWidth += ((hour * 60) + min) * widthPerMin;
        }

        if (isInit) {
            if (startPoint.equals(new PointF()) && endPoint.equals(new PointF())) {
                startPoint.set(paddingLeft + additionWidth, paddingTop);
                endPoint.set(paddingLeft + additionWidth, paddingTop + height);
                isInit = false;
            } else {
                float distance = 0f;
                if (startPoint.x > paddingLeft + additionWidth) {
                    distance = startPoint.x - paddingLeft + additionWidth;
                    startPoint.x -= distance / rollBackSpeed;
                    endPoint.x -= distance / rollBackSpeed;
                    if (startPoint.x < paddingLeft + additionWidth) {
                        startPoint.x = paddingLeft + additionWidth;
                        endPoint.x = paddingLeft + additionWidth;
                    }
                } else if (startPoint.x < paddingLeft + additionWidth) {
                    distance = paddingLeft + additionWidth - startPoint.x;
                    startPoint.x += distance / rollBackSpeed;
                    endPoint.x += distance / rollBackSpeed;
                    if (startPoint.x > paddingLeft + additionWidth) {
                        startPoint.x = paddingLeft + additionWidth;
                        endPoint.x = paddingLeft + additionWidth;
                    }
                } else {
                    isInit = false;
                }
            }
        }

        //避免出界
        if (startPoint.x < paddingLeft) {
            startPoint.x = paddingLeft;
            endPoint.x = paddingLeft;
        } else if (startPoint.x > getWidth() - paddingRight) {
            startPoint.x = getWidth() - paddingRight;
            endPoint.x = getWidth() - paddingRight;
        }

        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, indicatorPaint);

        if (isInit) {
            if (isMoving) {
                isInit = false;
            } else {
                invalidate();
            }
        }
    }

    public void setTime(int hour, int min) {
        this.hour = hour;
        this.min = min;
        invalidate();
    }

    public void setCanvasBorder(float canvasBorder) {
        this.canvasBorder = canvasBorder;
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
//        Log.w(TAG, "onTouchEvent: " + event.toString());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX(0) >= startPoint.x - indicatorStrokeWidth && event.getX(0) <= startPoint.x + indicatorStrokeWidth
                        && event.getY(0) >= startPoint.y && event.getY(0) <= endPoint.y) {
                    initHandler.removeCallbacks(initTask);
                    isMoving = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isMoving) {
                    if (startPoint.x != event.getX(0)) {
                        startPoint.x = event.getX(0);
                        endPoint.x = event.getX(0);
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                isMoving = false;
                initHandler.postDelayed(initTask, 3000);
                break;

        }
        return true;
    }
}
