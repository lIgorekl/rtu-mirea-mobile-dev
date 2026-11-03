package com.mirea.korovkin.lecture1.data.storage.models;

public class Movie {
    private final int id;
    private final String name;
    private final String localDate;

    public Movie(int id, String name, String localDate) {
        this.id = id;
        this.name = name;
        this.localDate = localDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocalDate() {
        return localDate;
    }
}