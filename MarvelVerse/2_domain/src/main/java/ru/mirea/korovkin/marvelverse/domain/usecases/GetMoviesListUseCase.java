package ru.mirea.korovkin.marvelverse.domain.usecases;

import java.util.List;

import ru.mirea.korovkin.marvelverse.domain.models.Movie;
import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;

// Отдаёт весь каталог фильмов (из сети через Retrofit, с практики 5).
public class GetMoviesListUseCase {
    private final MovieRepository movieRepository;

    public GetMoviesListUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> execute() {
        return movieRepository.getMovies();
    }
}
