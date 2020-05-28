package com.bronzeswordstudios.glorpythespacething;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MainBackgroundView mainBackgroundView;
    private MediaPlayer backTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Display display = getWindowManager().getDefaultDisplay();
        Point displayPoint = new Point();
        display.getSize(displayPoint);
        mainBackgroundView = new MainBackgroundView(this, displayPoint.x, displayPoint.y);
        FrameLayout backgroundView = findViewById(R.id.background_view);
        backgroundView.addView(mainBackgroundView);

        backTrack = MediaPlayer.create(this, R.raw.glorpy_main);
        //backTrack.setVolume(1.0f, 1.0f);
        backTrack.start();


        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase localDb = dbHelper.getReadableDatabase();
        Cursor cursor = localDb.query(DataHolder.DataEntry.TABLE_NAME, DataHolder.DataEntry.projection,
                null, null, null, null, null);
        TextView highest_score_view = findViewById(R.id.highest_score_number);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String highestScore = String.valueOf(cursor.getInt(1));
            highest_score_view.setText(highestScore);
            DataHolder.highestScore = Integer.parseInt(highestScore);
            cursor.close();
        } else {
            highest_score_view.setText("0");
            DataHolder.highestScore = 0;
            ContentValues values = new ContentValues();
            values.put(DataHolder.DataEntry.HIGHEST_SCORE, 0);
            long newRowID = localDb.insert(DataHolder.DataEntry.TABLE_NAME, null, values);
            cursor.close();
        }
        ImageButton playButton = findViewById(R.id.play_button);
        ImageButton scoreButton = findViewById(R.id.score_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mainBackgroundView.pause();
        backTrack.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mainBackgroundView.resume();
        backTrack.start();
    }
}
