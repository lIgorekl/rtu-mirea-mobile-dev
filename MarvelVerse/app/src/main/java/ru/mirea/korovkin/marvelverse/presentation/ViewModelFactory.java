package ru.mirea.korovkin.marvelverse.presentation;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.mirea.korovkin.marvelverse.data.repository.AuthRepositoryImpl;
import ru.mirea.korovkin.marvelverse.data.repository.RepositoryFactory;
import ru.mirea.korovkin.marvelverse.domain.repository.ClientInfoRepository;
import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;

// У MainViewModel конструктор с параметрами, а стандартный ViewModelProvider умеет
// создавать только через пустой конструктор — поэтому нужна своя Factory.
// Практика 6: умеет делать две ViewModel — MainViewModel (список/детали) и ProfileViewModel.
// AuthRepositoryImpl создаю тут так же, как раньше в AuthActivity.
// Практика 7: MainViewModel тоже получает AuthRepository — избранное только для вошедших.
// Практика 8: + HeroMaskViewModel (распознавание лица для маски).
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    // applicationContext — чтобы фабрика (и репозитории) не держали фрагмент/Activity
    public ViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        ClientInfoRepository clientInfoRepository = RepositoryFactory.createClientInfoRepository(context);
        // смотрю, какую ViewModel просят
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            MovieRepository movieRepository = RepositoryFactory.createMovieRepository(context);
            return (T) new MainViewModel(movieRepository, clientInfoRepository, new AuthRepositoryImpl());
        }
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(new AuthRepositoryImpl(), clientInfoRepository);
        }
        // практика 8: вкладка "Маска" — модель распознавания лиц + проверка, вошёл ли юзер
        if (modelClass.isAssignableFrom(HeroMaskViewModel.class)) {
            return (T) new HeroMaskViewModel(
                    RepositoryFactory.createFaceRecognitionRepository(context), new AuthRepositoryImpl());
        }
        // забыл добавить ветку под новую ViewModel — упадёт сразу и понятно
        throw new IllegalArgumentException("Unknown ViewModel: " + modelClass.getName());
    }
}
