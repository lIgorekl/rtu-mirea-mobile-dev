package ru.mirea.korovkin.marvelverse.presentation;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;
import ru.mirea.korovkin.marvelverse.domain.models.Movie;
import ru.mirea.korovkin.marvelverse.domain.repository.ClientInfoRepository;
import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;
import ru.mirea.korovkin.marvelverse.domain.usecases.AddToFavoritesUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.GetClientInfoUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.GetFavoriteMoviesUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.GetMovieDetailsUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.GetMoviesListUseCase;

// Мост между MainActivity и domain-слоем (практика 3): Activity сюда не лезет напрямую
// в репозитории/use-case'ы, а только вызывает методы ниже и слушает LiveData.
// Переживает поворот экрана (в отличие от Activity) — см. ViewModelFactory, как её создают.
// Практика 5: всё, что ходит в сеть, гоняю в фоновом потоке, результат — через postValue.
public class MainViewModel extends ViewModel {

    private static final String TAG = "MarvelVerseCheck";

    private final GetMoviesListUseCase getMoviesListUseCase;
    private final GetMovieDetailsUseCase getMovieDetailsUseCase;
    private final AddToFavoritesUseCase addToFavoritesUseCase;
    private final GetFavoriteMoviesUseCase getFavoriteMoviesUseCase;
    private final GetClientInfoUseCase getClientInfoUseCase;

    // один фоновый поток: задачи идут по очереди → сначала каталог, потом детали и избранное
    // (они уже берут фильмы из кэша репозитория, без лишних запросов)
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<Movie>> movies = new MutableLiveData<>();
    private final MutableLiveData<Movie> movieDetails = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> favorites = new MutableLiveData<>();
    private final MutableLiveData<ClientInfo> clientInfo = new MutableLiveData<>();
    // текст ошибки сети для экрана; null = ошибки нет
    private final MutableLiveData<String> error = new MutableLiveData<>();
    // Отдельная LiveData, которая сама следит за movies и favorites и пересчитывает
    // общий статус — про неё пункт 3 контрольного задания (MediatorLiveData).
    private final MediatorLiveData<String> combinedStatus = new MediatorLiveData<>();

    public MainViewModel(MovieRepository movieRepository, ClientInfoRepository clientInfoRepository) {
        Log.d(TAG, "MainViewModel created");
        this.getMoviesListUseCase = new GetMoviesListUseCase(movieRepository);
        this.getMovieDetailsUseCase = new GetMovieDetailsUseCase(movieRepository);
        this.addToFavoritesUseCase = new AddToFavoritesUseCase(movieRepository);
        this.getFavoriteMoviesUseCase = new GetFavoriteMoviesUseCase(movieRepository);
        this.getClientInfoUseCase = new GetClientInfoUseCase(clientInfoRepository);

        // MediatorLiveData слушает оба источника: сеть (movies) и БД (favorites).
        combinedStatus.addSource(movies, m -> updateCombinedStatus());
        combinedStatus.addSource(favorites, f -> updateCombinedStatus());
    }

    // Пересчитывает combinedStatus. Ждём, пока оба источника хоть раз получат значение
    // (иначе можно словить NPE на getValue()). Колбэки addSource идут в главном потоке → setValue ок.
    private void updateCombinedStatus() {
        List<Movie> m = movies.getValue();
        List<Movie> f = favorites.getValue();
        if (m != null && f != null) {
            combinedStatus.setValue(String.format("Movies (сеть): %d, Favorites (БД): %d", m.size(), f.size()));
        }
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<Movie> getMovieDetails() {
        return movieDetails;
    }

    public LiveData<List<Movie>> getFavorites() {
        return favorites;
    }

    public LiveData<ClientInfo> getClientInfo() {
        return clientInfo;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getCombinedStatus() {
        return combinedStatus;
    }

    // Ниже — методы, которые Activity дёргает по своей инициативе (клик, onCreate и т.д.).
    // Все они void: результат кладём в LiveData, а не возвращаем напрямую.
    // Из фонового потока можно только postValue (setValue там упадёт).

    // каталог из сети. Успех — список + стираю старую ошибку, провал — текст ошибки на экран
    public void loadMovies() {
        executor.execute(() -> {
            try {
                movies.postValue(getMoviesListUseCase.execute());
                error.postValue(null);
            } catch (RuntimeException e) {
                Log.e(TAG, "loadMovies failed", e);
                error.postValue(e.getMessage());
            }
        });
    }

    public void loadMovieDetails(int id) {
        executor.execute(() -> {
            try {
                movieDetails.postValue(getMovieDetailsUseCase.execute(id));
            } catch (RuntimeException e) {
                Log.e(TAG, "loadMovieDetails failed", e);
                error.postValue(e.getMessage());
            }
        });
    }

    // Room + детали фильмов из сети — тоже в фон (сеть в главном потоке Android запрещает)
    public void addToFavoriteAndReload(int movieId) {
        executor.execute(() -> {
            try {
                addToFavoritesUseCase.execute(movieId);
                favorites.postValue(getFavoriteMoviesUseCase.execute());
            } catch (RuntimeException e) {
                Log.e(TAG, "addToFavoriteAndReload failed", e);
                error.postValue(e.getMessage());
            }
        });
    }

    // SharedPreferences быстрые и без сети — можно прямо тут, через setValue
    public void loadClientInfo() {
        clientInfo.setValue(getClientInfoUseCase.execute());
    }

    // ViewModel умирает насовсем (Activity закрыли, не поворот) — гашу поток
    @Override
    protected void onCleared() {
        executor.shutdownNow();
        super.onCleared();
    }
}
