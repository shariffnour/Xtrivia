package com.nour.xtrivia;

import java.util.HashMap;
import java.util.Map;

public class Categories {
    private static final Map<String, Integer> categories;

    public Categories() {
    }

    static {
        categories = new HashMap<>();
        categories.put("General Knowledge", 9);
        categories.put("Books", 10);
        categories.put("Movies", 11);
        categories.put("Music", 12);
        categories.put("Musicals & Theatres", 13);
        categories.put("Television", 14);
        categories.put("Video Games", 15);
        categories.put("Board Games", 16);
        categories.put("Nature", 17);
        categories.put("Computers", 18);
        categories.put("Mathematics", 19);
        categories.put("Mythology", 20);
        categories.put("Sports", 21);
        categories.put("Geography", 22);
        categories.put("History", 23);
        categories.put("Politics", 24);
        categories.put("Art", 25);
        categories.put("Celebrities", 26);
        categories.put("Animals", 27);
        categories.put("Vehicles", 28);
        categories.put("Comics", 29);
        categories.put("Gadgets", 30);
        categories.put("Anime & Manga", 31);
        categories.put("Cartoons & Animations", 32);
    }

    public static Map<String, Integer> getCategories(){ return categories;}

}
