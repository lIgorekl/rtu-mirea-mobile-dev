package ru.mirea.korovkin.marvelverse.domain.usecases;

import java.util.List;

import ru.mirea.korovkin.marvelverse.domain.models.Movie;
import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;

// Отдаёт список избранных фильмов (читает Room, потом докручивает детали через MovieRepository).
public class GetFavoriteMoviesUseCase {
    private final MovieRepository movieRepository;

    public GetFavoriteMoviesUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> execute() {
        return movieRepository.getFavoriteMovies();
    }
}
