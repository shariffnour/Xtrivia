package com.nour.xtrivia;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.RealmResults;

public class ScoresRecyclerAdapter extends RecyclerView.Adapter<ScoresRecyclerAdapter.ViewHolder> {
    private Context context;
    private final LayoutInflater layoutInflater;
    private ArrayList<Scores> scores;

    public ScoresRecyclerAdapter(Context context, ArrayList<Scores> scores){
        this.context = context;
        this.scores = scores;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.score_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Scores score = scores.get(position);
        holder.category.setText(score.getCategory());
        holder.difficulty.setText(score.getDifficulty());
        holder.points.setText(Integer.toString(score.getPoints()));

    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView category, difficulty, points;
        ImageView catIcon, diffIcon, ptsIcon;
        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.categoryName);
            difficulty = itemView.findViewById(R.id.difficulty);
            points = itemView.findViewById(R.id.points);

            catIcon = itemView.findViewById(R.id.categoryIcon);
            diffIcon = itemView.findViewById(R.id.difficultyIcon);
            ptsIcon = itemView.findViewById(R.id.pointsIcon);
        }
    }
}
