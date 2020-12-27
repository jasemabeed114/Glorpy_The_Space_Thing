package com.bronzeswordstudios.glorpythespacething;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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
    private ContentValues contentValues;


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
        contentValues = new ContentValues();
        DataHolder.backTrack.start();

        // set up display
        Button submitButton = findViewById(R.id.submit_button);
        final TextView freePointsView = findViewById(R.id.free_points);
        TextView increasePowerView = findViewById(R.id.increase_power_button);
        TextView decreasePowerView = findViewById(R.id.decrease_power_button);
        TextView increaseLifeView = findViewById(R.id.increase_life_button);
        TextView decreaseLifeView = findViewById(R.id.decrease_life_button);
        TextView increaseSpeedView = findViewById(R.id.increase_speed_button);
        TextView decreaseSpeedView = findViewById(R.id.decrease_speed_button);
        final TextView powerValueView = findViewById(R.id.power_mod_value);
        final TextView lifeValueView = findViewById(R.id.life_mod_value);
        final TextView speedValueView = findViewById(R.id.speed_mod_value);

        powerValueView.setText(String.valueOf(DataHolder.powerMod));
        lifeValueView.setText(String.valueOf(DataHolder.lifeMod));
        speedValueView.setText(String.valueOf(DataHolder.speedMod));
        freePointsView.setText(String.valueOf(DataHolder.freePoints));

        // handle clicks
        increasePowerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataHolder.freePoints > 0) {
                    DataHolder.freePoints--;
                    freePointsView.setText(String.valueOf(DataHolder.freePoints));
                    DataHolder.powerMod++;
                    powerValueView.setText(String.valueOf(DataHolder.powerMod));
                    contentValues.put(DataHolder.DataEntry.POWER_VALUE, DataHolder.powerMod);
                    contentValues.put(DataHolder.DataEntry.POINTS_VALUE, DataHolder.freePoints);
                }
            }
        });

        decreasePowerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataHolder.powerMod > 0) {
                    DataHolder.freePoints++;
                    freePointsView.setText(String.valueOf(DataHolder.freePoints));
                    DataHolder.powerMod--;
                    powerValueView.setText(String.valueOf(DataHolder.powerMod));
                    contentValues.put(DataHolder.DataEntry.POWER_VALUE, DataHolder.powerMod);
                    contentValues.put(DataHolder.DataEntry.POINTS_VALUE, DataHolder.freePoints);
                }
            }
        });

        increaseLifeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataHolder.freePoints > 0) {
                    DataHolder.freePoints--;
                    freePointsView.setText(String.valueOf(DataHolder.freePoints));
                    DataHolder.lifeMod++;
                    lifeValueView.setText(String.valueOf(DataHolder.lifeMod));
                    contentValues.put(DataHolder.DataEntry.LIFE_VALUE, DataHolder.lifeMod);
                    contentValues.put(DataHolder.DataEntry.POINTS_VALUE, DataHolder.freePoints);
                }
            }
        });

        decreaseLifeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataHolder.lifeMod > 0) {
                    DataHolder.freePoints++;
                    freePointsView.setText(String.valueOf(DataHolder.freePoints));
                    DataHolder.lifeMod--;
                    lifeValueView.setText(String.valueOf(DataHolder.lifeMod));
                    contentValues.put(DataHolder.DataEntry.LIFE_VALUE, DataHolder.lifeMod);
                    contentValues.put(DataHolder.DataEntry.POINTS_VALUE, DataHolder.freePoints);
                }
            }
        });

        increaseSpeedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataHolder.freePoints > 0) {
                    DataHolder.freePoints--;
                    freePointsView.setText(String.valueOf(DataHolder.freePoints));
                    DataHolder.speedMod++;
                    speedValueView.setText(String.valueOf(DataHolder.speedMod));
                    contentValues.put(DataHolder.DataEntry.SPEED_VALUE, DataHolder.speedMod);
                    contentValues.put(DataHolder.DataEntry.POINTS_VALUE, DataHolder.freePoints);
                }
            }
        });

        decreaseSpeedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataHolder.speedMod > 0) {
                    DataHolder.freePoints++;
                    freePointsView.setText(String.valueOf(DataHolder.freePoints));
                    DataHolder.speedMod--;
                    speedValueView.setText(String.valueOf(DataHolder.speedMod));
                    contentValues.put(DataHolder.DataEntry.SPEED_VALUE, DataHolder.speedMod);
                    contentValues.put(DataHolder.DataEntry.POINTS_VALUE, DataHolder.freePoints);
                }
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase(contentValues);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        mainBackgroundView.pause();
        if (contentValues.size() > 0) {
            updateDatabase(contentValues);
        }
        DataHolder.backTrack.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mainBackgroundView.resume();
        DataHolder.backTrack.start();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (contentValues.size() > 0) {
            updateDatabase(contentValues);
        }
        super.onBackPressed();
    }

    public void updateDatabase(ContentValues contentValues) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase localDb = dbHelper.getWritableDatabase();
        if (contentValues.size() > 0) {

            long newRowID = localDb.update(DataHolder.DataEntry.TABLE_NAME, contentValues, null, null);
        }
    }

}