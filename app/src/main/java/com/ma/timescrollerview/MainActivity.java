package com.ma.timescrollerview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    String TAG = getClass().getSimpleName();

    int timeSectionCount = 0;
    int timeSectionStartPositionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimeScrollerView timeScrollerView = findViewById(R.id.timeScroller);
//        TimeScrollerIndicatorView timeScrollerIndicatorView = findViewById(R.id.indicator);
//        timeScrollerIndicatorView.setCanvasBorder(timeScrollerView.getCornerRadius());
//        timeScrollerIndicatorView.setOnClock(true);
//
//        timeScrollerIndicatorView.setOnIndicatorDragListener(new TimeScrollerIndicatorView.onIndicatorDragListener() {
//            @Override
//            public void onDragStarted(float x, float y) {
//
//            }
//
//            @Override
//            public void onDragging(float x, float y) {
////                Log.e(TAG, "onDragging: x="+x+" y="+y);
//            }
//
//            @Override
//            public void onDragFinished(float x, float y) {
//                Log.e(TAG, "onDragFinished: x=" + x + " y=" + y);
//            }
//        });

//        Button timeSectionButton = findViewById(R.id.timeSectionButton);
//        timeSectionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (timeSectionCount == 24) timeSectionCount = 0;
//                else timeSectionCount++;
//                timeScrollerView.setTimeSectionCount(timeSectionCount);
//                Log.i(TAG, "onClick: TimeSectionCount=" + timeSectionCount + " TimeSectionStartPosition=" + timeSectionStartPositionCount);
//            }
//        });
//
//        Button timeSectionStartPositionButton = findViewById(R.id.timeSectionStartPositionButton);
//        timeSectionStartPositionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (timeSectionStartPositionCount == 24) timeSectionStartPositionCount = 1;
//                else timeSectionStartPositionCount++;
//                timeScrollerView.setTimeSectionStartPosition(timeSectionStartPositionCount);
//                Log.i(TAG, "onClick: TimeSectionCount=" + timeSectionCount + " TimeSectionStartPosition=" + timeSectionStartPositionCount);
//            }
//        });
    }
}