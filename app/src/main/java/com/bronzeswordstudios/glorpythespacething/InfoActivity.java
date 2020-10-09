package com.bronzeswordstudios.glorpythespacething;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    MainBackgroundView mainBackgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        FrameLayout frameLayout = findViewById(R.id.background_view);
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        mainBackgroundView = new MainBackgroundView(this, point.x, point.y);
        frameLayout.addView(mainBackgroundView);
    }

    @Override
    protected void onPause() {
        mainBackgroundView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mainBackgroundView.resume();
        super.onResume();
    }
}