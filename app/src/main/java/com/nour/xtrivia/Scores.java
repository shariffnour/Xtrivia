package com.nour.xtrivia;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Scores extends RealmObject {
    @PrimaryKey
    private int id;

    private String category;
    private String difficulty;
    private int points;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
