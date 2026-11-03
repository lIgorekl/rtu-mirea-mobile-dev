package com.mirea.korovkin.lecture1.data.storage;

import com.mirea.korovkin.lecture1.data.storage.models.Movie;

public interface MovieStorage {
    Movie get();
    boolean save(Movie movie);
}