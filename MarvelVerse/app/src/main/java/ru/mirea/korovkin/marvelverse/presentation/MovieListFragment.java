package ru.mirea.korovkin.marvelverse.presentation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.mirea.korovkin.marvelverse.R;
import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;
import ru.mirea.korovkin.marvelverse.domain.models.Movie;

// Практика 6: главный экран со списком фильмов — переехал из MainActivity во фрагмент.
// Практика 7: это стартовый пункт графа и первая вкладка. Переход на детали — через
// NavController и action из nav_graph, кнопку "Профиль" убрал (есть вкладка внизу).
// Логика та же (ViewModel + LiveData, TMDB, ошибки с "Повторить").
public class MovieListFragment extends Fragment {

    private static final String TAG = "MarvelVerseCheck";
    // id фильма "Железный человек" в TMDB — на нём проверяю детали
    private static final int IRON_MAN_ID = 1726;

    private MainViewModel vm;

    // ViewModel + загрузка. Тут важно: при переключении вкладок Navigation сохраняет
    // и пересоздаёт фрагмент, onCreate зовётся снова — поэтому гружу, только если
    // каталога в общей ViewModel ещё нет (иначе каждый заход на вкладку = запрос в TMDB)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requireActivity() — ViewModel общая на Activity, её же видят детали и избранное
        vm = new ViewModelProvider(requireActivity(), new ViewModelFactory(requireContext()))
                .get(MainViewModel.class);
        if (vm.getMovies().getValue() == null) {
            startLoading();
        } else {
            Log.d(TAG, "Movies already loaded, без нового запроса");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_list, container, false);
    }

    // лента, подписки, кнопка "Повторить"
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView statusText = view.findViewById(R.id.statusText);

        // клик по карточке: кладу фильм в общую ViewModel и иду по action'у в детали
        RecyclerView movieRecyclerView = view.findViewById(R.id.movieRecyclerView);
        movieRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        MovieAdapter movieAdapter = new MovieAdapter(movie -> {
            Log.d(TAG, "Open details: " + movie.getTitle());
            vm.selectMovie(movie);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_movieList_to_movieDetails);
        });
        movieRecyclerView.setAdapter(movieAdapter);

        // во фрагменте подписываюсь через getViewLifecycleOwner, а не this:
        // view умирает при уходе в бэк-стек, а сам фрагмент живёт дальше
        vm.getMovies().observe(getViewLifecycleOwner(), (List<Movie> movies) -> {
            Log.d(TAG, "Movies count: " + movies.size() + " → отдаю в RecyclerView");
            movieAdapter.setItems(movies);
        });

        // старые проверки из Logcat (практики 1-3) — оставил
        vm.getMovieDetails().observe(getViewLifecycleOwner(), (Movie details) ->
                Log.d(TAG, "Details for id=" + IRON_MAN_ID + ": " + (details != null ? details.getTitle() : "not found")));

        vm.getFavorites().observe(getViewLifecycleOwner(), (List<Movie> favorites) -> {
            Log.d(TAG, "Favorites count (Room): " + favorites.size());
            for (Movie fav : favorites) {
                Log.d(TAG, "Favorite: " + fav.getTitle());
            }
        });

        vm.getClientInfo().observe(getViewLifecycleOwner(), (ClientInfo clientInfo) ->
                Log.d(TAG, "Cached client info: " + (clientInfo != null ? clientInfo.getEmail() : "нет данных (ещё не входили)")));

        // MediatorLiveData из практики 3 — общий статус "сеть + БД"
        vm.getCombinedStatus().observe(getViewLifecycleOwner(), statusText::setText);

        // практика 5: ошибка сети → красный текст + "Повторить"; null → прячем
        TextView errorText = view.findViewById(R.id.errorText);
        Button retryButton = view.findViewById(R.id.retryButton);
        vm.getError().observe(getViewLifecycleOwner(), (String errorMessage) -> {
            if (errorMessage == null) {
                errorText.setVisibility(View.GONE);
                retryButton.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "Network error on screen: " + errorMessage);
                errorText.setText("Не удалось загрузить фильмы. " + errorMessage);
                errorText.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
            }
        });
        retryButton.setOnClickListener(v -> startLoading());
    }

    // вернулись на список (например, после входа) — перечитываю кэш SharedPreferences
    @Override
    public void onResume() {
        super.onResume();
        vm.loadClientInfo();
    }

    // загрузка: из onCreate и по кнопке "Повторить". Сеть ViewModel сама уводит в фон.
    // практика 7: раньше тут Iron Man каждый раз сам добавлялся в избранное (проверка Room
    // из практики 2) — теперь есть настоящая кнопка на деталях, поэтому просто читаю избранное
    private void startLoading() {
        vm.loadMovies();
        vm.loadMovieDetails(IRON_MAN_ID);
        vm.loadFavorites();
        vm.loadClientInfo();
    }
}
