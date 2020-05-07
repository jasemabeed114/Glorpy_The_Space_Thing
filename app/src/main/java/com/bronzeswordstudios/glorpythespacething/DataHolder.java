package com.bronzeswordstudios.glorpythespacething;

import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;

public class DataHolder {
    public static final String SCORE_KEY = "score";
    // holds static data for use across activities
    public static int score;
    public static int highestScore;
    public static int rank;
    public static ScoreItem userToRemove;

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
        public static String HIGHEST_SCORE = "user_score";
        public static final String[] projection = {_ID, HIGHEST_SCORE};
        public static String TABLE_NAME = "glorpy_data";
    }
}

