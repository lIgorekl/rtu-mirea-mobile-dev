package com.mirea.korovkin.lecture1.data.repository;

import com.mirea.korovkin.lecture1.data.storage.MovieStorage;
import com.mirea.korovkin.lecture1.domain.models.Movie;
import com.mirea.korovkin.lecture1.domain.repository.MovieRepository;

import java.time.LocalDate;

public class MovieRepositoryImpl implements MovieRepository {

    private final MovieStorage movieStorage;

    public MovieRepositoryImpl(MovieStorage movieStorage) {
        this.movieStorage = movieStorage;
    }

    @Override
    public boolean saveMovie(Movie movie) {
        return movieStorage.save(mapToStorage(movie));
    }

    @Override
    public Movie getMovie() {
        return mapToDomain(movieStorage.get());
    }

    // domain → storage: при сохранении добавляем текущую дату
    private com.mirea.korovkin.lecture1.data.storage.models.Movie mapToStorage(Movie movie) {
        return new com.mirea.korovkin.lecture1.data.storage.models.Movie(
                movie.getId(), movie.getName(), LocalDate.now().toString());
    }

    // storage → domain: дату отбрасываем, domain-у она не нужна
    private Movie mapToDomain(com.mirea.korovkin.lecture1.data.storage.models.Movie movie) {
        return new Movie(movie.getId(), movie.getName());
    }
}