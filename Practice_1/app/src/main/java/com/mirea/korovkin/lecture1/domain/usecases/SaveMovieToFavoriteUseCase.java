package com.mirea.korovkin.lecture1.domain.usecases;

import com.mirea.korovkin.lecture1.domain.models.Movie;
import com.mirea.korovkin.lecture1.domain.repository.MovieRepository;

// use-case на сохранение фильма
public class SaveMovieToFavoriteUseCase {
    private MovieRepository movieRepository;

    public SaveMovieToFavoriteUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // отдаёт movie в репозиторий и возвращает результат сохранения
    public boolean execute(Movie movie) {
        return movieRepository.saveMovie(movie);
    }
}