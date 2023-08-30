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
import android.util.Log;
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
    private int hour = 11;
    private int min = 28;
    private PointF startPoint = new PointF();
    private PointF endPoint = new PointF();
    private PointF newPoint = new PointF();

    private boolean isMoving = false;
    private boolean isInit = true;
    private boolean isRestoring = false;

    private float rollBackSpeed = 12f;
    private Handler handler = new Handler();
    private long runClockTime = 60;
    private long indicatorRestoreTime = 3000;
    private boolean isOnClock = false;
    private int restoreHour = 0;
    private onClockTickingListener onClockTickingListener = null;
    private int restoreMin = 0;
    private Runnable onClockTask = new Runnable() {
        @Override
        public void run() {
//            Log.e(TAG, "run: isOnClock:"+isOnClock+" isInit:"+isInit+" isMoving:"+isMoving+" isRestoring:"+isRestoring);
            if (++min >= 60) {
                min = 0;
                hour++;
            }
            hour = hour >= 24 ? 0 : hour;
            if (!isMoving && !isRestoring) {
                isInit = true;
                isFromClockTask = true;
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
    private Runnable initTask = new Runnable() {
        @Override
        public void run() {
            if (!isMoving) {
                isRestoring = true;
                isInit = true;
                restoreHour = hour;
                restoreMin = min;
                Log.e(TAG, "initTask: Restoring......");
                invalidate();
            }
        }
    };

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
        float additionWidthRestore = 0f;

        if (hour >= 0 && min >= 0) {
            additionWidth += ((hour * 60) + min) * widthPerMin;
            additionWidthRestore += ((restoreHour * 60) + restoreMin) * widthPerMin;
        }

        if (isInit) {
            if (startPoint.equals(newPoint) && endPoint.equals(newPoint)) {
                startPoint.set(paddingLeft + additionWidth, paddingTop);
                endPoint.set(paddingLeft + additionWidth, paddingTop + height);
                isInit = false;
            } else {
                float distance = 0f;
                float targetPosX = paddingLeft + additionWidth;
                float restorePosX = paddingLeft + additionWidthRestore;
                //防止23:59跳转到0:00 onClockTicking监听器显示返回路径
                if (startPoint.x > targetPosX && hour == 0 && min == 0) isRestoring = true;
                if (startPoint.x > targetPosX) {  //后退
                    distance = startPoint.x - targetPosX;
//                    Log.e(TAG, "onDraw: distance="+distance);
                    startPoint.x -= distance / rollBackSpeed;
                    endPoint.x -= distance / rollBackSpeed;
                    if (startPoint.x < targetPosX) {
                        startPoint.x = targetPosX;
                        endPoint.x = targetPosX;
                    } else if (startPoint.x > targetPosX && startPoint.x <= restorePosX) {
                        if (isRestoring) {
                            Log.e(TAG, "init: Restored");
                            handler.removeCallbacks(initTask);
                            isRestoring = false;
                        }
                    }
//                    Log.e(TAG, "onDraw: 1");
//                    Log.e(TAG, "onDraw: startPoint.x="+startPoint.x+" paddingLeft+additionWidth="+String.valueOf(paddingLeft + additionWidth));
                } else if (startPoint.x < targetPosX) {  //前移
                    distance = targetPosX - startPoint.x;
                    distance = Math.max(distance, widthPerMin);
//                    Log.e(TAG, "onDraw: distance="+distance);
                    startPoint.x += distance / rollBackSpeed;
                    endPoint.x += distance / rollBackSpeed;
                    if (startPoint.x > targetPosX) {
                        startPoint.x = targetPosX;
                        endPoint.x = targetPosX;
                    } else if (startPoint.x < targetPosX && startPoint.x >= restorePosX) {
                        if (isRestoring) {
                            Log.e(TAG, "init: Restored");
                            handler.removeCallbacks(initTask);
                            isRestoring = false;
                        }
                    }
//                    Log.e(TAG, "onDraw: startPoint.x="+startPoint.x+" paddingLeft+additionWidth="+String.valueOf(paddingLeft + additionWidth));
//                    Log.e(TAG, "onDraw: 2");
                } else {
                    if (isRestoring) {
                        Log.e(TAG, "initTask: Restored");
                        handler.removeCallbacks(initTask);
                        isRestoring = false;
                    }
                    isInit = false;
                }
            }

            if (!isRestoring && isOnClock && isFromClockTask) {
                if (onClockTickingListener != null)
                    onClockTickingListener.onClockTicking(startPoint.x);
                isFromClockTask = false;
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
                isRestoring = false;
            } else {
                invalidate();
            }
        }
    }

    private onIndicatorDragListener onIndicatorDragListener = null;
    private boolean isFromClockTask = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.w(TAG, "onTouchEvent: " + event.toString());

        //屏蔽父级触摸事件
        getParent().requestDisallowInterceptTouchEvent(true);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX(0) >= startPoint.x - 3 * indicatorStrokeWidth / 2f && event.getX(0) <= startPoint.x + 3 * indicatorStrokeWidth / 2f
                        && event.getY(0) >= startPoint.y && event.getY(0) <= endPoint.y) {
                    handler.removeCallbacks(initTask);
                    isMoving = true;
                    if (onIndicatorDragListener != null)
                        onIndicatorDragListener.onDragStarted(event.getX(), event.getY());
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isMoving) {
                    if (startPoint.x != event.getX(0)) {
                        startPoint.x = event.getX(0);
                        endPoint.x = event.getX(0);
                        //避免出界
                        if (startPoint.x < getPaddingLeft()) {
                            startPoint.x = getPaddingLeft();
                            endPoint.x = getPaddingLeft();
                        } else if (startPoint.x > getWidth() - getPaddingRight()) {
                            startPoint.x = getWidth() - getPaddingRight();
                            endPoint.x = getWidth() - getPaddingRight();
                        }
                        if (onIndicatorDragListener != null)
                            onIndicatorDragListener.onDragging(startPoint.x, event.getY());
                        invalidate();
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                isMoving = false;
                isRestoring = true;
                handler.postDelayed(initTask, indicatorRestoreTime);
                invalidate();
                float x = event.getX(0);
                //避免出界
                if (x < getPaddingLeft()) {
                    x = getPaddingLeft();
                } else if (x > getWidth() - getPaddingRight()) {
                    x = getWidth() - getPaddingRight();
                }
                if (onIndicatorDragListener != null)
                    onIndicatorDragListener.onDragFinished(x, event.getY());
                return true;
        }
        return false;
    }

    public void setOnClockTickingListener(TimeScrollerIndicatorView.onClockTickingListener onClockTickingListener) {
        this.onClockTickingListener = onClockTickingListener;
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

    /**
     * 设置view初始指标所处时间点
     *
     * @param hour 所处小时
     * @param min  所处分钟
     */
    public void setTime(int hour, int min) {
        this.hour = hour;
        this.min = min;
        tmpHour = hour;
        tmpMin = min;
        invalidate();
    }

    /**
     * 设置指标根据设定间隔走设定距离
     *
     * @param onClock 开关
     */
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

    public boolean isOnClock() {
        return isOnClock;
    }

    /**
     * 设置全局画布圆角大小
     *
     * @param canvasBorder 圆角大小
     */
    public void setCanvasBorder(float canvasBorder) {
        this.canvasBorder = canvasBorder;
    }

    /**
     * 设置指标拖动监听器
     *
     * @param onIndicatorDragListener 指标拖动监听器
     */
    public void setOnIndicatorDragListener(TimeScrollerIndicatorView.onIndicatorDragListener onIndicatorDragListener) {
        this.onIndicatorDragListener = onIndicatorDragListener;
    }

    /**
     * 指标拖动监听器
     */
    public interface onIndicatorDragListener {
        /**
         * 拖拽开始时
         *
         * @param x x轴坐标
         * @param y y轴坐标
         */
        void onDragStarted(float x, float y);

        /**
         * 拖拽中
         *
         * @param x x轴坐标
         * @param y y轴坐标
         */
        void onDragging(float x, float y);

        /**
         * 拖拽结束时
         *
         * @param x x轴坐标
         * @param y y轴坐标
         */
        void onDragFinished(float x, float y);
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

    /**
     * 指标根据设定间隔移动设定距离监听器
     */
    public interface onClockTickingListener {
        /**
         * 开启后 自动移动时触发
         *
         * @param x x轴坐标
         */
        void onClockTicking(float x);
    }


}
