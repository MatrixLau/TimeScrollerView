package com.ma.timescrollerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TimeScrollerView extends View {

    // 设置画笔变量
    Paint circlePaint, cornerRadiusPaint, rectPaint, scrollerPaint, scrollerRailPaint, scaleTextPaint, linePaint;
    Path backgroundPath;
    private String TAG = getClass().getSimpleName();
    private int circleColor = Color.RED;
    private int rectColor = Color.GRAY;
    private float cornerRadius = 0f;
    private float scrollerRailWidth = -1f;
    private int scrollerColor = Color.WHITE;
    private int scrollerRailColor = Color.BLACK;
    private int lineColor = 0xff424A7A;
    private float lineStroke = 4f;
    private float lineGap;

    private int timeSectionCount = 0;
    private int timeSectionStartPosition = 0;
    private float scrollerCornerRadius = 0f;

    // 自定义View有四个构造函数
    // 如果View是在Java代码里面new的，则调用第一个构造函数
    public TimeScrollerView(Context context) {
        super(context);

        // 在构造函数里初始化画笔的操作
        init();
    }


    // 如果View是在.xml里声明的，则调用第二个构造函数
    // 自定义属性是从AttributeSet参数传进来的
    public TimeScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 加载自定义属性集合CircleView
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeScrollerView);

        // 解析集合中的属性circle_color属性
        // 该属性的id为:R.styleable.CircleView_circle_color
        // 将解析的属性传入到画圆的画笔颜色变量当中（本质上是自定义画圆画笔的颜色）
        // 第二个参数是默认设置颜色（即无指定circle_color情况下使用）
        circleColor = a.getColor(R.styleable.TimeScrollerView_TimeScrollerView_circle_color, circleColor);
        cornerRadius = a.getFloat(R.styleable.TimeScrollerView_TimeScrollerView_corner_radius, cornerRadius);
        rectColor = a.getColor(R.styleable.TimeScrollerView_TimeScrollerView_rect_color, rectColor);
        scrollerRailWidth = a.getFloat(R.styleable.TimeScrollerView_TimeScrollerView_scroller_rail_width, scrollerRailWidth);
        scrollerColor = a.getColor(R.styleable.TimeScrollerView_TimeScrollerView_scroller_color, scrollerColor);
        scrollerRailColor = a.getColor(R.styleable.TimeScrollerView_TimeScrollerView_scroller_rail_color, scrollerRailColor);
        lineColor = a.getColor(R.styleable.TimeScrollerView_TimeScrollerView_line_color, lineColor);
        lineStroke = a.getFloat(R.styleable.TimeScrollerView_TimeScrollerView_line_stroke, lineStroke);
        scrollerCornerRadius = a.getFloat(R.styleable.TimeScrollerView_TimeScrollerView_scroller_corner_radius, scrollerCornerRadius);

        // 解析后释放资源
        a.recycle();

        init();
    }

    // 不会自动调用
    // 一般是在第二个构造函数里主动调用
    // 如View有style属性时
    public TimeScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    //API21之后才使用
    // 不会自动调用
    // 一般是在第二个构造函数里主动调用
    // 如View有style属性时
    public TimeScrollerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    // 画笔初始化
    private void init() {

        //画布限制Path
        backgroundPath = new Path();

        // 创建圆形画笔
        circlePaint = new Paint();
        // 设置画笔颜色
        circlePaint.setColor(circleColor);
        // 设置画笔宽度为10px
        circlePaint.setStrokeWidth(5f);
        //设置画笔模式为填充
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        //圆角画笔
        cornerRadiusPaint = new Paint();
        cornerRadiusPaint.setAntiAlias(true);
        cornerRadiusPaint.setColor(Color.WHITE);

        //长方形画笔
        rectPaint = new Paint();
        rectPaint.setColor(rectColor);
        rectPaint.setStrokeWidth(5f);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setAntiAlias(true);

        //轨道画笔
        scrollerRailPaint = new Paint();
        scrollerRailPaint.setColor(scrollerRailColor);
        scrollerRailPaint.setAntiAlias(true);

        //轨道滑轮画笔
        scrollerPaint = new Paint();
        scrollerPaint.setColor(scrollerColor);
        scrollerPaint.setAntiAlias(true);

        //刻度文字画笔
        scaleTextPaint = new Paint();
        scaleTextPaint.setColor(Color.WHITE);
        scaleTextPaint.setAntiAlias(true);
        scaleTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        scaleTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        scaleTextPaint.setTextSize(20);

        //刻度线画笔
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineStroke);

    }


    // 复写onDraw()进行绘制
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        // 获取传入的padding值
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        // 获取绘制内容的高度和宽度（考虑了四个方向的padding值）
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        //图像设置圆角 锯齿严重
//        setOutlineProvider(new ViewOutlineProvider() {
//            @Override
//            public void getOutline(View view, Outline outline) {
//                // 创建一个圆角矩形的轮廓
//                outline.setRoundRect(paddingLeft, paddingTop, getWidth() - paddingLeft, getHeight() - paddingTop, cornerRadius);
//            }
//        });
//        // 开启视图剪切到outline
//        setClipToOutline(true);


//        // 设置圆的半径 = 宽,高最小值的2分之1
//        int r = Math.min(width, height) / 2;
//
//        // 画出圆(蓝色)
//        // 圆心 = 控件的中央,半径 = 宽,高最小值的2分之1
//        canvas.drawCircle(paddingLeft + width / 2, paddingTop + height / 2, r, circlePaint);


        float rollerRail = scrollerRailWidth == -1 ? height / 5f : scrollerRailWidth;

        //画背景限制画布
        backgroundPath.addRoundRect(paddingLeft, paddingTop, getWidth() - paddingRight, getHeight() - paddingBottom, cornerRadius, cornerRadius, Path.Direction.CW);
        backgroundPath.close();
        canvas.clipPath(backgroundPath);

        //画出背景长方形
        canvas.drawRoundRect(paddingLeft, paddingTop, getWidth() - paddingRight, getHeight() - paddingBottom, cornerRadius, cornerRadius, rectPaint);

        float linePadding = width / 20f;
        lineGap = width / 24f;
        int lineCount = 0;

        //画刻度线
        for (int i = 1; i < 24; i++) {

            canvas.drawLine(paddingLeft + i * lineGap, paddingTop + linePadding / 2, paddingRight + i * lineGap, height + paddingTop - linePadding / 2, linePaint);

            lineCount++;

            //画刻度数字
            if (lineCount % 3 == 0 && lineCount != 24) {
                float textWidth = scaleTextPaint.measureText(String.valueOf(lineCount)) * 0.5f;
                float lineLength = (height - lineGap) / 10f;
                canvas.drawText(String.valueOf(lineCount), paddingLeft + (i - 1) * lineGap + lineGap / 2 - textWidth, height + paddingTop - linePadding / 2 - lineLength, scaleTextPaint);
            }

        }

        //画滑道
        canvas.drawRect(paddingLeft, getHeight() / 2f - rollerRail / 2, getWidth() - paddingRight, getHeight() / 2f + rollerRail / 2, scrollerRailPaint);

        //计算开始位置
//        int timeSectionStartPosition = 24;
        float timeSectionStartWidth = 0f;
        timeSectionStartWidth = timeSectionStartPosition * lineGap;

        //计算滑轮长度
//        int timeSectionCount = 6;
        float timeSectionWidth = 0f;

        if (timeSectionCount == 0) {
            timeSectionWidth = 0f;
        } else if (timeSectionCount == 24) {
            timeSectionWidth = width;
        } else if (timeSectionStartPosition + timeSectionCount <= 24) {
            timeSectionWidth = timeSectionCount * lineGap;
        } else if (timeSectionStartPosition + timeSectionCount > 24) {
            int todaySection = 24 - timeSectionStartPosition;
            float todaySectionWidth = todaySection * lineGap;
            int sectionsLeft = timeSectionStartPosition + timeSectionCount - 24;
            float sectionsLeftWidth = sectionsLeft * lineGap;
            timeSectionWidth = todaySectionWidth + sectionsLeftWidth;
        }

        //画滑轮
        if (timeSectionStartPosition + timeSectionCount <= 24) {
            Log.i(TAG, "onDraw: timeSectionStartWidth=" + timeSectionStartWidth + " timeSectionWidth=" + timeSectionWidth);
            //当天
            canvas.drawRoundRect(paddingLeft + timeSectionStartWidth, getHeight() / 2f - rollerRail / 2, paddingLeft + timeSectionStartWidth + timeSectionWidth, getHeight() / 2f + rollerRail / 2, scrollerCornerRadius, scrollerCornerRadius, scrollerPaint);
        } else {
            //跨天
            float diff = timeSectionWidth - (width - timeSectionStartWidth);
            if (timeSectionStartPosition != 24) {
                //先画当天
                canvas.drawRoundRect(paddingLeft + timeSectionStartWidth, getHeight() / 2f - rollerRail / 2, paddingLeft + width, getHeight() / 2f + rollerRail / 2, scrollerCornerRadius, scrollerCornerRadius, scrollerPaint);
            }
            //在画隔天
            canvas.drawRoundRect(paddingLeft, getHeight() / 2f - rollerRail / 2, paddingLeft + diff, getHeight() / 2f + rollerRail / 2, scrollerCornerRadius, scrollerCornerRadius, scrollerPaint);
        }


//
//        // 获取控件的高度和宽度
//        int width = getWidth();
//        int height = getHeight();
//
//        // 设置圆的半径 = 宽,高最小值的2分之1
//        int r = Math.min(width, height)/2;
//
//        // 画出圆（蓝色）
//        // 圆心 = 控件的中央,半径 = 宽,高最小值的2分之1
//        canvas.drawCircle(width/2,height/2,r,mPaint1);

//        canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()),cornerRadius,cornerRadius, cornerRadiusPaint);

    }

    public int getTimeSectionCount() {
        return timeSectionCount;
    }

    public void setTimeSectionCount(int timeSectionCount) {
        this.timeSectionCount = timeSectionCount;
        invalidate();
    }

    public int getTimeSectionStartPosition() {
        return timeSectionStartPosition;
    }

    public void setTimeSectionStartPosition(int timeSectionStartPosition) {
        this.timeSectionStartPosition = timeSectionStartPosition;
        invalidate();
    }
}
