package com.mirea.korovkin.lecture1.presentation;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mirea.korovkin.lecture1.domain.models.Movie;
import com.mirea.korovkin.lecture1.domain.repository.MovieRepository;
import com.mirea.korovkin.lecture1.domain.usecases.GetFavoriteFilmUseCase;
import com.mirea.korovkin.lecture1.domain.usecases.SaveMovieToFavoriteUseCase;

public class MainViewModel extends ViewModel {

    private final MovieRepository movieRepository;
    private final MutableLiveData<String> favoriteMovie = new MutableLiveData<>();

    public MainViewModel(MovieRepository movieRepository) {
        Log.d(MainViewModel.class.getSimpleName(), "MainViewModel created");
        this.movieRepository = movieRepository;
    }

    public LiveData<String> getFavoriteMovie() {
        return favoriteMovie;
    }

    public void setText(Movie movie) {
        boolean result = new SaveMovieToFavoriteUseCase(movieRepository).execute(movie);
        favoriteMovie.setValue(String.format("Save result %s", result));
    }

    public void getText() {
        Movie movie = new GetFavoriteFilmUseCase(movieRepository).execute();
        if (movie.getName() == null || movie.getName().isEmpty()) {
            favoriteMovie.setValue("Нет сохранённого фильма");
        } else {
            favoriteMovie.setValue(String.format("My favorite movie is %s", movie.getName()));
        }
    }

    @Override
    protected void onCleared() {
        Log.d(MainViewModel.class.getSimpleName(), "MainViewModel cleared");
        super.onCleared();
    }
}