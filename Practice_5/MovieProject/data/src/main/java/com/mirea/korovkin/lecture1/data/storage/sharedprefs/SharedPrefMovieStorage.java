package com.mirea.korovkin.lecture1.data.storage.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.mirea.korovkin.lecture1.data.storage.MovieStorage;
import com.mirea.korovkin.lecture1.data.storage.models.Movie;

import java.time.LocalDate;

public class SharedPrefMovieStorage implements MovieStorage {

    private static final String PREFS_NAME = "movie_prefs";
    private static final String KEY_ID = "favorite_movie_id";
    private static final String KEY_NAME = "favorite_movie_name";
    private static final String KEY_DATE = "favorite_movie_date";

    private final SharedPreferences sharedPreferences;

    public SharedPrefMovieStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public Movie get() {
        int id = sharedPreferences.getInt(KEY_ID, -1);
        String name = sharedPreferences.getString(KEY_NAME, "");
        String date = sharedPreferences.getString(KEY_DATE, LocalDate.now().toString());
        return new Movie(id, name, date);
    }

    @Override
    public boolean save(Movie movie) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, movie.getId());
        editor.putString(KEY_NAME, movie.getName());
        editor.putString(KEY_DATE, movie.getLocalDate());
        return editor.commit();
    }
}