package ru.mirea.korovkin.marvelverse.domain.usecases;

import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;

// Добавляет фильм в избранное (в итоге — запись в Room через MovieRepositoryImpl).
public class AddToFavoritesUseCase {
    private final MovieRepository movieRepository;

    public AddToFavoritesUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public void execute(int movieId) {
        movieRepository.addToFavorites(movieId);
    }
}
