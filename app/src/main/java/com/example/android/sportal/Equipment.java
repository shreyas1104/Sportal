package com.example.android.sportal;

import java.util.ArrayList;

/**
 * Created by Zamaan on 07-04-2018.
 */

public class Equipment {
    public String id;
    public String name;
    public String sport;
    public String photo_url;
    public ArrayList<String> contents;
    public int total_count;
    public int booked_count;

    public Equipment() {
        //default constructor
    }

    public Equipment(String id, String name, String sport, String photo_url, ArrayList<String> contents, int total_count, int booked_count) {
        this.id = id;
        this.name = name;
        this.sport = sport;
        this.photo_url = photo_url;
        this.contents = contents;
        this.total_count = total_count;
        this.booked_count = booked_count;


    }
}
