package com.mirea.korovkin.lecture1.domain.usecases;

import com.mirea.korovkin.lecture1.domain.models.Movie;
import com.mirea.korovkin.lecture1.domain.repository.MovieRepository;

public class SaveMovieToFavoriteUseCase {
    private MovieRepository movieRepository;

    public SaveMovieToFavoriteUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public boolean execute(Movie movie) {
        return movieRepository.saveMovie(movie);
    }
}