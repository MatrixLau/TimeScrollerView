package com.ma.timescrollerview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    String TAG = getClass().getSimpleName();

    int timeSectionCount = 0;
    int timeSectionStartPositionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimeScrollerView timeScrollerView = findViewById(R.id.timeScroller);
        Button timeSectionButton = findViewById(R.id.timeSectionButton);
        timeSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeSectionCount == 24) timeSectionCount = 0;
                else timeSectionCount++;
                timeScrollerView.setTimeSectionCount(timeSectionCount);
                Log.i(TAG, "onClick: TimeSectionCount=" + timeSectionCount + " TimeSectionStartPosition=" + timeSectionStartPositionCount);
            }
        });

        Button timeSectionStartPositionButton = findViewById(R.id.timeSectionStartPositionButton);
        timeSectionStartPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeSectionStartPositionCount == 24) timeSectionStartPositionCount = 1;
                else timeSectionStartPositionCount++;
                timeScrollerView.setTimeSectionStartPosition(timeSectionStartPositionCount);
                Log.i(TAG, "onClick: TimeSectionCount=" + timeSectionCount + " TimeSectionStartPosition=" + timeSectionStartPositionCount);
            }
        });
    }
}