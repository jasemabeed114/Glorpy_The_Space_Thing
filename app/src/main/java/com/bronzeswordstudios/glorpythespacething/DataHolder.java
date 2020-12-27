package com.bronzeswordstudios.glorpythespacething;

import android.media.MediaPlayer;
import android.provider.BaseColumns;

import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Arrays;

public class DataHolder {
    public static final String SCORE_KEY = "score";
    // holds static data for use across activities
    public static int score;
    public static int highScore;
    public static int highestScore;
    public static int rank;
    public static int powerMod;
    public static int lifeMod;
    public static int speedMod;
    public static int freePoints;
    public static long lastRewardTime;
    public static InterstitialAd interstitialAd;
    public static ScoreItem userToRemove;
    public static MediaPlayer backTrack;

    // insertion sort by score value in descending order
    public static ArrayList<ScoreItem> sortScoreItems(ArrayList<ScoreItem> scoreItems) {
        ArrayList<ScoreItem> sortedItems = new ArrayList<>();
        ScoreItem[] score_array = scoreItems.toArray(new ScoreItem[0]);

        for (int i = 0; i < score_array.length; i++) {

            int j = i;

            while (j > 0 && score_array[j - 1].getScoreValue() < score_array[j].getScoreValue()) {

                ScoreItem holder = score_array[j];
                score_array[j] = score_array[j - 1];
                score_array[j - 1] = holder;
                j = j - 1;

            }
            sortedItems = new ArrayList<>(Arrays.asList(score_array));
        }
        return sortedItems;
    }

    public static final class DataEntry implements BaseColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String HIGHEST_SCORE = "user_score";
        public static final String POWER_VALUE = "power_value";
        public static final String LIFE_VALUE = "life_value";
        public static final String SPEED_VALUE = "speed_value";
        public static final String POINTS_VALUE = "points_value";
        public static final String TIME_VALUE = "time_value";

        public static final int HIGHEST_SCORE_INDEX = 1;
        public static final int POWER_INDEX = 2;
        public static final int LIFE_INDEX = 3;
        public static final int SPEED_INDEX = 4;
        public static final int POINTS_INDEX = 5;
        public static final int REWARD_TIME_INDEX = 6;
        public static final String[] projection = {_ID, HIGHEST_SCORE, POWER_VALUE, LIFE_VALUE, SPEED_VALUE, POINTS_VALUE, TIME_VALUE};
        public static String TABLE_NAME = "glorpy_data";
    }

}

