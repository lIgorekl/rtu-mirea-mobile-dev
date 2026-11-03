package com.mirea.korovkin.lecture1.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.mirea.korovkin.lecture1.domain.models.Movie;
import com.mirea.korovkin.lecture1.domain.repository.MovieRepository;

// реальная реализация MovieRepository, через SharedPreferences
public class MovieRepositoryImpl implements MovieRepository {

    // имя файла настроек и ключи, под которыми храню id/name
    private static final String PREFS_NAME = "movie_prefs";
    private static final String KEY_ID = "favorite_movie_id";
    private static final String KEY_NAME = "favorite_movie_name";

    private final SharedPreferences sharedPreferences;

    // context отдаётся снаружи (presentation), в domain его нет и не будет
    public MovieRepositoryImpl(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // сохраняю id и name отдельными ключами
    @Override
    public boolean saveMovie(Movie movie) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, movie.getId());
        editor.putString(KEY_NAME, movie.getName());
        return editor.commit(); // commit - синхронно, сразу знаю результат (apply - было бы асинхронно)
    }

    // если ничего не сохранено - id будет -1, name пустая строка (дефолты)
    @Override
    public Movie getMovie() {
        int id = sharedPreferences.getInt(KEY_ID, -1);
        String name = sharedPreferences.getString(KEY_NAME, "");
        return new Movie(id, name);
    }
}