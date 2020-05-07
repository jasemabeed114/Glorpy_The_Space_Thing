package com.bronzeswordstudios.glorpythespacething;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase localDb = dbHelper.getWritableDatabase();
        if (DataHolder.score > DataHolder.highestScore){
            ContentValues values = new ContentValues();
            values.put(DataHolder.DataEntry.HIGHEST_SCORE, DataHolder.score);
            String selection = DataHolder.DataEntry._ID + " = ?";
            String selectionArgs[] = {"1"};
            int count = localDb.update(DataHolder.DataEntry.TABLE_NAME, values, selection, selectionArgs);
        }
        final int score = DataHolder.score;
        final TextView scoreValueView = findViewById(R.id.score_value);
        scoreValueView.setText(String.valueOf(score));
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Button replayButton = findViewById(R.id.replay_button);
        Button viewScoresButton = findViewById(R.id.view_scores_button);
        Button exitButton = findViewById(R.id.exit_button);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, GameActivity.class);
                DataHolder.score = 0;
                startActivity(intent);
                finish();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                DataHolder.score = 0;
                startActivity(intent);
                finish();

            }
        });

        viewScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, ScoreActivity.class);
                DataHolder.score = 0;
                startActivity(intent);
                finish();
            }
        });

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
                        if (score >= scoreItems.get(i).getScoreValue()) {
                            DataHolder.rank = i + 1;
                            higherScore = true;
                            if (scoreItems.size() >= 10) {
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
                    Toast.makeText(GameOverActivity.this, "TEST: No data available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
