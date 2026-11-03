package ru.mirea.korovkin.marvelverse.domain.repository;

import java.util.List;

import ru.mirea.korovkin.marvelverse.domain.models.Movie;

// Контракт для фильмов и избранного. Одна реализация (MovieRepositoryImpl) закрывает
// сразу два источника: каталог — из сети (Retrofit, практика 5), избранное — из Room.
// Сетевые методы блокирующие и при проблемах кидают RuntimeException — звать в фоне.
// Presentation видит только этот интерфейс (см. RepositoryFactory).
public interface MovieRepository {
    List<Movie> getMovies();
    Movie getMovieDetails(int id);

    void addToFavorites(int movieId);
    void removeFromFavorites(int movieId);
    boolean isFavorite(int movieId);
    List<Movie> getFavoriteMovies();
}
