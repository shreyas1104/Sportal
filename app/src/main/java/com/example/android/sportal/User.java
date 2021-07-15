package com.example.android.sportal;

/**
 * Created by Zamaan on 25-03-2018.
 */

public class User {
    public String UID;
    public String name;
    public String dob;
    public String email;
    public String student_id;
    public String photo_url;
    public String degree;
    public String branch;
    public Boolean booked;
    public Boolean received;
    public String booking_id;
    public String booking_date;
    public String issue_date;

    public User() {
        //default constructor
    }

    public User(String UID, String name, String dob, String email, String student_id, String photo_url, String degree, String branch, Boolean booked, Boolean received, String booking_id, String booking_date, String issue_date) {
        this.UID = UID;
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.student_id = student_id;
        this.photo_url = photo_url;
        this.degree = degree;
        this.branch = branch;
        this.booked = booked;
        this.received = received;
        this.booking_id = booking_id;
        this.booking_date = booking_date;
        this.issue_date = issue_date;
    }
}
