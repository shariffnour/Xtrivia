package com.nour.xtrivia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ScoresActivity extends AppCompatActivity {

    private RecyclerView scoresRecycler;
    private ScoresRecyclerAdapter scoresRecyclerAdapter;
    private LinearLayoutManager layoutManager;
    private RealmResults<Scores> realmResults;
    private ArrayList<Scores> scores;
    private Realm realmDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        realmDB = Realm.getDefaultInstance();

        scoresRecycler = findViewById(R.id.scoresRecycler);
        layoutManager = new LinearLayoutManager(this);
        scoresRecycler.setLayoutManager(layoutManager);
        scores = new ArrayList<>();
        fetchAllScores();

    }

    private void fetchAllScores() {
        realmResults = realmDB.where(Scores.class).findAllAsync();
        for(Scores s: realmResults){
            scores.add(s);
        }
        scoresRecyclerAdapter = new ScoresRecyclerAdapter(this, scores);
        scoresRecycler.setAdapter(scoresRecyclerAdapter);
    }
}
