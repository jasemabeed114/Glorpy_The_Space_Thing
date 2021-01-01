package com.bronzeswordstudios.glorpythespacething;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Debug: ";
    RewardedAd rewardedAd;
    private MainBackgroundView mainBackgroundView;
    private long timeBetweenAdsMillis;
    private SQLiteDatabase localDb;

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
        MobileAds.initialize(this);

        // set up add confirmation
        final LinearLayout adPopUp = findViewById(R.id.ad_confirm);
        adPopUp.setVisibility(View.INVISIBLE);
        Button yes_button = findViewById(R.id.yes_button);
        Button no_button = findViewById(R.id.no_button);


        // half hour minimum between ads
        timeBetweenAdsMillis = 1800000;

        // handle music
        if (DataHolder.backTrack == null) {
            DataHolder.backTrack = MediaPlayer.create(this, R.raw.glorpy_title);
            DataHolder.backTrack.setVolume(0.5f, 0.5f);
        }
        DataHolder.backTrack.start();

        //setup ads
        // todo: set to my ad id on release
        DataHolder.interstitialAd = new InterstitialAd(MainActivity.this);
        DataHolder.interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        DataHolder.interstitialAd.loadAd(new AdRequest.Builder().build());
        loadRewardAd();

        //Load or setup local database
        DBHelper dbHelper = new DBHelper(this);
        localDb = dbHelper.getReadableDatabase();
        Cursor cursor = localDb.query(DataHolder.DataEntry.TABLE_NAME, DataHolder.DataEntry.projection,
                null, null, null, null, null);
        TextView highest_score_view = findViewById(R.id.highest_score_number);

        // if database exists get data
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String highestScore = String.valueOf(cursor.getInt(DataHolder.DataEntry.HIGHEST_SCORE_INDEX));
            highest_score_view.setText(highestScore);
            int powerMod = cursor.getInt(DataHolder.DataEntry.POWER_INDEX);
            int lifeMod = cursor.getInt(DataHolder.DataEntry.LIFE_INDEX);
            int speedMod = cursor.getInt(DataHolder.DataEntry.SPEED_INDEX);
            int freePoints = cursor.getInt(DataHolder.DataEntry.POINTS_INDEX);
            long lastRewardTime = cursor.getLong(DataHolder.DataEntry.REWARD_TIME_INDEX);
            DataHolder.highestScore = Integer.parseInt(highestScore);
            DataHolder.powerMod = powerMod;
            DataHolder.lifeMod = lifeMod;
            DataHolder.speedMod = speedMod;
            DataHolder.freePoints = freePoints;
            DataHolder.lastRewardTime = lastRewardTime;

            // else create database
        } else {
            highest_score_view.setText("0");
            DataHolder.highestScore = 0;
            DataHolder.powerMod = 0;
            DataHolder.lifeMod = 0;
            DataHolder.speedMod = 0;
            DataHolder.freePoints = 0;
            DataHolder.lastRewardTime = 0;
            ContentValues values = new ContentValues();
            values.put(DataHolder.DataEntry.HIGHEST_SCORE, 0);
            values.put(DataHolder.DataEntry.POWER_VALUE, 0);
            values.put(DataHolder.DataEntry.LIFE_VALUE, 0);
            values.put(DataHolder.DataEntry.SPEED_VALUE, 0);
            values.put(DataHolder.DataEntry.POINTS_VALUE, 0);
            values.put(DataHolder.DataEntry.TIME_VALUE, 0);
            long newRowID = localDb.insert(DataHolder.DataEntry.TABLE_NAME, null, values);
        }
        cursor.close();

        // handle buttons
        TextView playButton = findViewById(R.id.play_button);
        TextView scoreButton = findViewById(R.id.score_button);
        TextView evoButton = findViewById(R.id.evolution_button);
        TextView infoButton = findViewById(R.id.info_button);
        TextView dailyButton = findViewById(R.id.daily_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
                DataHolder.backTrack.seekTo(0);
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

        evoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EvolutionActivity.class);
                startActivity(intent);
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adPopUp.setVisibility(View.VISIBLE);
            }

        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adPopUp.setVisibility(View.INVISIBLE);
                if (rewardedAd.isLoaded()) {
                    if (System.currentTimeMillis() - DataHolder.lastRewardTime >= timeBetweenAdsMillis) {

                        final Activity activityContext = MainActivity.this;
                        final RewardedAdCallback adCallback = new RewardedAdCallback() {
                            @Override
                            public void onRewardedAdOpened() {
                                // Ad opened.

                            }

                            @Override
                            public void onRewardedAdClosed() {
                                // Ad closed.
                                loadRewardAd();
                            }

                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem reward) {
                                // User earned reward.
                                String rewardMessage = reward.getAmount() + " " + getString(R.string.reward_toast);
                                Toast.makeText(activityContext, rewardMessage, Toast.LENGTH_SHORT).show();
                                DataHolder.freePoints += reward.getAmount();
                                DataHolder.lastRewardTime = System.currentTimeMillis();
                                ContentValues values = new ContentValues();
                                values.put(DataHolder.DataEntry.POINTS_VALUE, DataHolder.freePoints);
                                values.put(DataHolder.DataEntry.TIME_VALUE, DataHolder.lastRewardTime);
                                updateDatabase(values);

                            }

                            @Override
                            public void onRewardedAdFailedToShow(AdError adError) {
                                // Ad failed to display.
                            }
                        };
                        rewardedAd.show(activityContext, adCallback);
                    } else {
                        long minutesRemaining = (timeBetweenAdsMillis - (System.currentTimeMillis() - DataHolder.lastRewardTime)) / (60000);
                        String timeRemainingMessage = getString(R.string.time_remaining) + " " + minutesRemaining;
                        Toast.makeText(MainActivity.this, timeRemainingMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.reward_not_ready, Toast.LENGTH_SHORT).show();
                }
            }
        });

        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adPopUp.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onPause() {
        mainBackgroundView.pause();
        DataHolder.backTrack.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mainBackgroundView.resume();
        DataHolder.backTrack.start();
        loadRewardAd();
        super.onResume();
    }

    @Override
    protected void onStop() {
        localDb.close();
        super.onStop();
    }

    private void loadRewardAd() {
        // todo: insert my add key on release
        rewardedAd = new RewardedAd(this, "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback rewardedAdLoadCallback = new RewardedAdLoadCallback();
        rewardedAd.loadAd(new AdRequest.Builder().build(), rewardedAdLoadCallback);
    }

    public void updateDatabase(ContentValues contentValues) {
        DBHelper dbHelper = new DBHelper(this);
        localDb = dbHelper.getWritableDatabase();
        if (contentValues.size() > 0) {

            long newRowID = localDb.update(DataHolder.DataEntry.TABLE_NAME, contentValues, null, null);
        }
    }

}