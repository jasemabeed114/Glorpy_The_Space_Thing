package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    private ArrayList<ScoreItem> scoreList;

    public ScoreAdapter(ArrayList<ScoreItem> scoreList) {
        this.scoreList = scoreList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View scoreView = inflater.inflate(R.layout.score_view, parent, false);

        return new ViewHolder(scoreView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView scoreTextView = holder.scoreTextView;
        TextView userTextView = holder.userTextView;
        TextView rankTextView = holder.rankTextView;
        int playerPosition = position + 1;
        ScoreItem scoreItem = scoreList.get(position);
        String scoreText = scoreItem.getScoreValue() + " Pts";
        String rankText = "#" + playerPosition;
        scoreTextView.setText(scoreText);
        userTextView.setText(scoreItem.getScoreOwner());
        rankTextView.setText(rankText);

    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userTextView;
        public TextView scoreTextView;
        public TextView rankTextView;

        public ViewHolder(View scoreView) {
            super(scoreView);
            userTextView = scoreView.findViewById(R.id.username_text_view);
            scoreTextView = scoreView.findViewById(R.id.score_text_view);
            rankTextView = scoreView.findViewById(R.id.rank);
        }
    }
}
