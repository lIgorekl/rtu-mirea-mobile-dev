package ru.mirea.korovkin.marvelverse.presentation;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.mirea.korovkin.marvelverse.data.repository.RepositoryFactory;
import ru.mirea.korovkin.marvelverse.domain.repository.ClientInfoRepository;
import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;

// У MainViewModel конструктор с параметрами, а стандартный ViewModelProvider умеет
// создавать только через пустой конструктор — поэтому нужна своя Factory.
// Она же единственное место в presentation, которое обращается к RepositoryFactory.
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public ViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        MovieRepository movieRepository = RepositoryFactory.createMovieRepository(context);
        ClientInfoRepository clientInfoRepository = RepositoryFactory.createClientInfoRepository(context);
        return (T) new MainViewModel(movieRepository, clientInfoRepository);
    }
}
