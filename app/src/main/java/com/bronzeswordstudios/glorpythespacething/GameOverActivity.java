package com.bronzeswordstudios.glorpythespacething;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GameOverActivity extends AppCompatActivity {

    GameOverBackground gameOverBackground;
    SQLiteDatabase localDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //set super cool background
        Display display = getWindowManager().getDefaultDisplay();
        Point displayPoint = new Point();
        display.getSize(displayPoint);
        gameOverBackground = new GameOverBackground(this, displayPoint.x, displayPoint.y);
        FrameLayout backgroundLayout = findViewById(R.id.background_view);
        backgroundLayout.addView(gameOverBackground);

        //get database info!
        DBHelper dbHelper = new DBHelper(this);
        localDb = dbHelper.getWritableDatabase();
        if (DataHolder.score > DataHolder.highestScore) {
            DataHolder.highestScore = DataHolder.score;
            ContentValues values = new ContentValues();
            values.put(DataHolder.DataEntry.HIGHEST_SCORE, DataHolder.score);
            String selection = DataHolder.DataEntry._ID + " = ?";
            String[] selectionArgs = {String.valueOf(DataHolder.DataEntry.HIGHEST_SCORE_INDEX)};
            int count = localDb.update(DataHolder.DataEntry.TABLE_NAME, values, selection, selectionArgs);
        }

        final TextView scoreValueView = findViewById(R.id.score_value);
        scoreValueView.setText(String.valueOf(DataHolder.score));
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Button replayButton = findViewById(R.id.replay_button);
        Button viewScoresButton = findViewById(R.id.view_scores_button);
        Button exitButton = findViewById(R.id.exit_button);


        //spawn ad
        if (DataHolder.interstitialAd != null) {
            DataHolder.interstitialAd.show(GameOverActivity.this);
        }

        //Buttons
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(GameOverActivity.this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        DataHolder.interstitialAd = interstitialAd;
                    }
                });
                Intent intent = new Intent(GameOverActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        viewScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, ScoreActivity.class);
                startActivity(intent);
            }
        });

        // assess scores from FireBase, update if new high scores
        firestore.collection(DataHolder.SCORE_KEY).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot rawData = task.getResult();
                if (rawData != null) {
                    ArrayList<DocumentSnapshot> scoreData = new ArrayList<>(rawData.getDocuments());
                    // check to see if there is a new high score, or a tie for a high score
                    ArrayList<ScoreItem> scoreItems = new ArrayList<>();
                    for (int i = 0; i < scoreData.size(); i++) {
                        ScoreItem userScore = new ScoreItem(Integer.parseInt(scoreData.get(i).get(DataHolder.SCORE_KEY).toString()), scoreData.get(i).getId());
                        scoreItems.add(userScore);
                    }

                    scoreItems = DataHolder.sortScoreItems(scoreItems);
                    boolean higherScore = false;
                    for (int i = 0; i < scoreItems.size(); i++) {
                        if (DataHolder.score >= scoreItems.get(i).getScoreValue()) {
                            DataHolder.rank = i + 1;
                            higherScore = true;
                            if (scoreItems.size() == 10) {
                                DataHolder.userToRemove = scoreItems.get(scoreItems.size() - 1);
                            }
                            Intent intent = new Intent(GameOverActivity.this, NewHighScoreActivity.class);
                            startActivity(intent);
                            break;
                        }
                    }
                    if (scoreItems.size() < 10 && !higherScore) {
                        DataHolder.rank = scoreItems.size() + 1;
                        Intent intent = new Intent(GameOverActivity.this, NewHighScoreActivity.class);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(GameOverActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameOverBackground.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameOverBackground.resume();
    }

    @Override
    protected void onStop() {
        if (localDb != null) {
            localDb.close();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        DataHolder.score = 0;
        super.onDestroy();
    }
}
