package com.bronzeswordstudios.glorpythespacething;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EvolutionActivity extends AppCompatActivity {
    private MainBackgroundView mainBackgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evolution);
        FrameLayout backgroundView = findViewById(R.id.background_view);
        Display display = getWindowManager().getDefaultDisplay();
        Point displayPoint = new Point();
        display.getSize(displayPoint);
        mainBackgroundView = new MainBackgroundView(this, displayPoint.x, displayPoint.y);
        backgroundView.addView(mainBackgroundView);

        Button submitButton = findViewById(R.id.submit_button);
        Button backButton = findViewById(R.id.back_button);
        TextView increasePowerView = findViewById(R.id.increase_power_button);
        TextView decreasePowerView = findViewById(R.id.decrease_power_button);
        TextView increaseLifeView = findViewById(R.id.increase_life_button);
        TextView decreaseLifeView = findViewById(R.id.decrease_life_button);
        TextView increaseSpeedView = findViewById(R.id.increase_speed_button);
        TextView decreaseSpeedView = findViewById(R.id.decrease_speed_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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