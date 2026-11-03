package ru.mirea.korovkin.marvelverse.domain.usecases;

import ru.mirea.korovkin.marvelverse.domain.models.Movie;
import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;

// Отдаёт один фильм по id (каталог приходит из сети через Retrofit).
public class GetMovieDetailsUseCase {
    private final MovieRepository movieRepository;

    public GetMovieDetailsUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie execute(int id) {
        return movieRepository.getMovieDetails(id);
    }
}
