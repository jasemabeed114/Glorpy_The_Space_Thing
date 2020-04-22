package com.bronzeswordstudios.glorpythespacething;

public class ScoreItem {
    private int scoreValue;
    private String scoreOwner;

    public ScoreItem(int scoreValue, String scoreOwner) {
        this.scoreValue = scoreValue;
        this.scoreOwner = scoreOwner;
    }

    public String getScoreOwner() {
        return scoreOwner;
    }

    public int getScoreValue() {
        return scoreValue;
    }
}
