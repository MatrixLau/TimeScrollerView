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

    private Path backgroundPath, trianglePath;
    private Paint indicatorPaint, trianglePaint;

    private int indicatorColor = Color.BLUE;
    private float indicatorStrokeWidth = getDp(10);
    private float widthPerMin = 0f;
    private int hour = 2;
    private int min = 19;
    private PointF startPoint = new PointF();
    private PointF endPoint = new PointF();
    private PointF newPoint = new PointF();

    private boolean isMoving = false;
    private boolean isInit = true;
    private boolean isRestoring = false;

    private float rollBackSpeed = 12f;
    private Handler handler = new Handler();
    private Runnable initTask = new Runnable() {
        @Override
        public void run() {
            if (!isMoving) {
                isRestoring = true;
                isInit = true;
                invalidate();
            }
        }
    };
    private long indicatorRestoreTime = 3000;
    private boolean isOnClock = false;
    private long runClockTime = 60;
    private Runnable onClockTask = new Runnable() {
        @Override
        public void run() {
//            Log.e(TAG, "run: isOnClock:"+isOnClock);
            if (++min >= 60) {
                min = 0;
                hour++;
            }
            hour = hour >= 24 ? 0 : hour;
            if (!isMoving && !isRestoring) {
                isInit = true;
                invalidate();

            }
            if (isOnClock) {
                handler.postDelayed(onClockTask, runClockTime);
            }
        }
    };

    private float canvasBorder = getDp(0);
    private int tmpHour = -1;
    private int tmpMin = -1;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        backgroundPath.reset();   //不reset会导致画布闪烁
        backgroundPath.addRoundRect(paddingLeft, paddingTop, getWidth() - paddingRight, getHeight() - paddingBottom, canvasBorder, canvasBorder, Path.Direction.CW);
        backgroundPath.close();
        canvas.clipPath(backgroundPath);

        widthPerMin = (float) width / 1440;

        float additionWidth = 0f;

        if (hour >= 0 && min >= 0) {
            additionWidth += ((hour * 60) + min) * widthPerMin;
        }

        if (isInit) {
            if (startPoint.equals(newPoint) && endPoint.equals(newPoint)) {
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
                    isRestoring = false;
                    handler.removeCallbacks(initTask);
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

        trianglePath.reset();
        if (isMoving) {
            trianglePath.moveTo(startPoint.x - indicatorStrokeWidth - 10f, (startPoint.y + endPoint.y) / 2f);
            trianglePath.lineTo(startPoint.x - indicatorStrokeWidth - 10f + 10f, (startPoint.y + endPoint.y) / 2f + 10f);
            trianglePath.lineTo(startPoint.x - indicatorStrokeWidth - 10f + 10f, (startPoint.y + endPoint.y) / 2f - 10f);
            trianglePath.close();

            trianglePath.moveTo(startPoint.x + indicatorStrokeWidth + 10f, (startPoint.y + endPoint.y) / 2f);
            trianglePath.lineTo(startPoint.x + indicatorStrokeWidth + 10f - 10f, (startPoint.y + endPoint.y) / 2f + 10f);
            trianglePath.lineTo(startPoint.x + indicatorStrokeWidth + 10f - 10f, (startPoint.y + endPoint.y) / 2f - 10f);
            trianglePath.close();
            canvas.drawPath(trianglePath, trianglePaint);
        }

        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, indicatorPaint);

        if (isInit) {
            //避免冲突
            if (isMoving) {
                isInit = false;
            } else {
                invalidate();
            }
        }
    }

    public void init() {

        backgroundPath = new Path();
        trianglePath = new Path();

        indicatorPaint = new Paint();
        indicatorPaint.setColor(indicatorColor);
        indicatorPaint.setStrokeWidth(indicatorStrokeWidth);
        indicatorPaint.setAntiAlias(true);

        trianglePaint = new Paint();
        trianglePaint.setColor(Color.RED);
        trianglePaint.setAntiAlias(true);
        trianglePaint.setStrokeWidth(indicatorStrokeWidth);
        trianglePaint.setStyle(Paint.Style.FILL);

        if (isOnClock) {
            handler.postDelayed(onClockTask, runClockTime);
        }
    }

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.w(TAG, "onTouchEvent: " + event.toString());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX(0) >= startPoint.x - 3 * indicatorStrokeWidth / 2f && event.getX(0) <= startPoint.x + 3 * indicatorStrokeWidth / 2f
                        && event.getY(0) >= startPoint.y && event.getY(0) <= endPoint.y) {
                    handler.removeCallbacks(initTask);
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
                isRestoring = true;
                handler.postDelayed(initTask, indicatorRestoreTime);
                break;

        }
        return true;
    }

    public void setTime(int hour, int min) {
        this.hour = hour;
        this.min = min;
        tmpHour = hour;
        tmpMin = min;
        invalidate();
    }

    public void setOnClock(boolean onClock) {
        isOnClock = onClock;
        handler.removeCallbacks(onClockTask);
        if (isOnClock) {
            tmpHour = hour;
            tmpMin = min;
            handler.postDelayed(onClockTask, runClockTime);
        } else {
            handler.removeCallbacks(onClockTask);
            hour = tmpHour;
            min = tmpMin;

            if (!isMoving) {
                isInit = true;
                invalidate();
            }
        }
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


}
