package com.mirea.korovkin.lecture1.domain.repository;

import com.mirea.korovkin.lecture1.domain.models.Movie;

public interface MovieRepository {
    boolean saveMovie(Movie movie);
    Movie getMovie();
}