package com.fram.codingassignment.mvp.portfoliochart.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thaile on 6/4/17.
 */

@IgnoreExtraProperties
public class Nav {

    @SerializedName("date")
    private String date;

    @SerializedName("amount")
    private float amount;

    private int month;

    private int day;

    private String hash;

    private long timestamp;

    public Nav() {
    }

    public Nav(String date, float amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
