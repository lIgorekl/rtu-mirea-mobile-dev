package com.mirea.korovkin.lecture1.domain.repository;

import com.mirea.korovkin.lecture1.domain.models.Movie;

// интерфейс, лежит в domain
// реализация - data/repository/MovieRepositoryImpl
public interface MovieRepository {
    boolean saveMovie(Movie movie); // true/false - получилось сохранить или нет
    Movie getMovie(); // достаёт сохранённый фильм
}