package com.mirea.korovkin.lecture1.domain.usecases;

import com.mirea.korovkin.lecture1.domain.models.Movie;
import com.mirea.korovkin.lecture1.domain.repository.MovieRepository;

// use-case на получение любимого фильма
public class GetFavoriteFilmUseCase {
    private MovieRepository movieRepository;

    // repository прилетает через конструктор (DI)
    public GetFavoriteFilmUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // просто прокидываю вызов в репозиторий, никакой своей логики тут нет
    public Movie execute() {
        return movieRepository.getMovie();
    }
}