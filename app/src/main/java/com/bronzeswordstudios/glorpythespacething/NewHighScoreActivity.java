package com.bronzeswordstudios.glorpythespacething;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewHighScoreActivity extends AppCompatActivity {
    Map<String, Object> scoreData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        final TextView scoreView = findViewById(R.id.score_number);
        final TextView rankView = findViewById(R.id.rank_number);
        final EditText userNameText = findViewById(R.id.username_edit_text);
        final ProgressBar scoreLoader = findViewById(R.id.score_loader);
        final Button submitButton = findViewById(R.id.submit_button);
        final LinearLayout rankLayout = findViewById(R.id.rank_layout);
        final LinearLayout nameLayout = findViewById(R.id.enter_name_layout);

        scoreView.setVisibility(View.VISIBLE);
        rankLayout.setVisibility(View.VISIBLE);
        nameLayout.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        scoreLoader.setVisibility(View.INVISIBLE);

        String scoreString = DataHolder.highScore + " Pts";
        String rankString = "#" + DataHolder.rank;
        scoreView.setText(scoreString);
        rankView.setText(rankString);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userNameString = userNameText.getText().toString().trim();
                if (userNameString.equals("")) {
                    Toast.makeText(NewHighScoreActivity.this, R.string.user_name_error, Toast.LENGTH_SHORT).show();
                } else {
                    scoreView.setVisibility(View.INVISIBLE);
                    rankLayout.setVisibility(View.INVISIBLE);
                    nameLayout.setVisibility(View.INVISIBLE);
                    submitButton.setVisibility(View.INVISIBLE);
                    scoreLoader.setVisibility(View.VISIBLE);

                    scoreData = new HashMap<>();
                    scoreData.put("score", DataHolder.highScore);
                    final FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = dataBase.collection(DataHolder.SCORE_KEY).document(userNameString);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().getData() == null) {
                                dataBase.collection(DataHolder.SCORE_KEY).document(userNameString).set(scoreData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful() && DataHolder.userToRemove == null) {
                                            Toast.makeText(NewHighScoreActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else if (task.isSuccessful()) {
                                            dataBase.collection(DataHolder.SCORE_KEY).document(DataHolder.userToRemove.getScoreOwner()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(NewHighScoreActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();
                                                        DataHolder.userToRemove = null;
                                                        finish();
                                                    } else {
                                                        Toast.makeText(NewHighScoreActivity.this, R.string.upload_error, Toast.LENGTH_SHORT).show();
                                                        scoreView.setVisibility(View.VISIBLE);
                                                        rankLayout.setVisibility(View.VISIBLE);
                                                        nameLayout.setVisibility(View.VISIBLE);
                                                        submitButton.setVisibility(View.VISIBLE);
                                                        scoreLoader.setVisibility(View.INVISIBLE);

                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(NewHighScoreActivity.this, R.string.upload_error, Toast.LENGTH_SHORT).show();
                                            scoreView.setVisibility(View.VISIBLE);
                                            rankLayout.setVisibility(View.VISIBLE);
                                            nameLayout.setVisibility(View.VISIBLE);
                                            submitButton.setVisibility(View.VISIBLE);
                                            scoreLoader.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(NewHighScoreActivity.this, R.string.name_exists, Toast.LENGTH_SHORT).show();
                                scoreView.setVisibility(View.VISIBLE);
                                rankLayout.setVisibility(View.VISIBLE);
                                nameLayout.setVisibility(View.VISIBLE);
                                submitButton.setVisibility(View.VISIBLE);
                                scoreLoader.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                }
            }
        });
    }
}
