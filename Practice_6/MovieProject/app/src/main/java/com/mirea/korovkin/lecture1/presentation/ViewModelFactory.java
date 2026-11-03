package com.mirea.korovkin.lecture1.presentation;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mirea.korovkin.lecture1.data.repository.MovieRepositoryImpl;
import com.mirea.korovkin.lecture1.data.storage.MovieStorage;
import com.mirea.korovkin.lecture1.data.storage.sharedprefs.SharedPrefMovieStorage;
import com.mirea.korovkin.lecture1.domain.repository.MovieRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public ViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        MovieStorage movieStorage = new SharedPrefMovieStorage(context);
        MovieRepository movieRepository = new MovieRepositoryImpl(movieStorage);
        return (T) new MainViewModel(movieRepository);
    }
}