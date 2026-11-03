package com.mirea.korovkin.lecture1.domain.models;

// entity, просто данные, без логики
public class Movie {
    private int id;
    private String name;

    // конструктор
    public Movie(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}