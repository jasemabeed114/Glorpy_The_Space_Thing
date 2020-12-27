package com.bronzeswordstudios.glorpythespacething;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // handle views
        final ProgressBar scoreLoader = findViewById(R.id.score_loader);
        final RecyclerView scoreListView = findViewById(R.id.score_recycle_view);
        final TextView emptyViewText = findViewById(R.id.no_score_text);
        scoreLoader.setVisibility(View.VISIBLE);
        scoreListView.setVisibility(View.INVISIBLE);
        emptyViewText.setVisibility(View.INVISIBLE);

        // start music
        DataHolder.backTrack.start();

        // handle database
        CollectionReference scoreData = firestore.collection(DataHolder.SCORE_KEY);
        scoreData.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<ScoreItem> scoreItems = new ArrayList<>();
                QuerySnapshot rawData = task.getResult();
                if (rawData != null && !rawData.isEmpty()) {
                    ArrayList<DocumentSnapshot> scoreList = (ArrayList<DocumentSnapshot>) rawData.getDocuments();
                    for (int i = 0; i < scoreList.size(); i++) {
                        scoreItems.add(new ScoreItem(Integer.parseInt(scoreList.get(i).get(DataHolder.SCORE_KEY).toString()), scoreList.get(i).getId()));
                    }
                    scoreItems = DataHolder.sortScoreItems(scoreItems);
                    scoreListView.setLayoutManager(new LinearLayoutManager(ScoreActivity.this));
                    ScoreAdapter scoreAdapter = new ScoreAdapter(scoreItems);
                    scoreListView.setAdapter(scoreAdapter);
                    scoreLoader.setVisibility(View.INVISIBLE);
                    scoreListView.setVisibility(View.VISIBLE);
                } else {
                    scoreLoader.setVisibility(View.INVISIBLE);
                    emptyViewText.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        DataHolder.backTrack.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataHolder.backTrack.start();
    }
}


