package ru.mirea.korovkin.marvelverse.domain.usecases;

import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;

// Убирает фильм из избранного (удаление записи в Room).
public class RemoveFromFavoritesUseCase {
    private final MovieRepository movieRepository;

    public RemoveFromFavoritesUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public void execute(int movieId) {
        movieRepository.removeFromFavorites(movieId);
    }
}
