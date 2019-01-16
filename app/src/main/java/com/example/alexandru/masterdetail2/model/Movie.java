package com.example.alexandru.masterdetail2.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class Movie implements Serializable {

    @NonNull
    @PrimaryKey
    private String id;

    @ColumnInfo(name="title")
    private String title;

    @ColumnInfo(name="details")
    private String details;

    @ColumnInfo(name="score")
    private int score;

    @ColumnInfo(name="year")
    private int year;

    @ColumnInfo(name="image")
    private String image;

    public Movie(@NonNull String id, String title, String details, int score, int year, String image) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.score = score;
        this.year = year;
        this.image = image;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public int getScore() {
        return score;
    }

    public int getYear() {
        return year;
    }

    public String getImage() {
        return image;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getId() + ". " + this.getTitle() + " (" + this.getYear() + ")";
    }
}
