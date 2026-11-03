package ru.mirea.korovkin.marvelverse.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.mirea.korovkin.marvelverse.R;
import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;
import ru.mirea.korovkin.marvelverse.domain.models.Movie;

// Главный экран. Activity не создаёт репозитории/use-case'ы сама — только просит их у MainViewModel
// и подписывается на LiveData (практика 3). С практики 4 список фильмов показывается в RecyclerView,
// с практики 5 фильмы идут из TMDB (Retrofit), а ошибки сети видны на экране.
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MarvelVerseCheck";
    // id фильма "Железный человек" в TMDB — на нём проверяю детали и избранное
    private static final int IRON_MAN_ID = 1726;

    private MainViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ViewModelFactory нужна, потому что у MainViewModel не пустой конструктор.
        vm = new ViewModelProvider(this, new ViewModelFactory(this)).get(MainViewModel.class);

        TextView statusText = findViewById(R.id.statusText);

        // Лента фильмов: LayoutManager = как раскладывать (вертикальным списком),
        // адаптер = кто заполняет карточки. Данных пока нет — придут через LiveData ниже.
        RecyclerView movieRecyclerView = findViewById(R.id.movieRecyclerView);
        movieRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MovieAdapter movieAdapter = new MovieAdapter();
        movieRecyclerView.setAdapter(movieAdapter);

        // Пришёл список из ViewModel → отдаём его адаптеру, лента перерисуется сама.
        vm.getMovies().observe(this, (List<Movie> movies) -> {
            Log.d(TAG, "Movies count: " + movies.size() + " → отдаю в RecyclerView");
            movieAdapter.setItems(movies);
        });

        vm.getMovieDetails().observe(this, (Movie details) ->
                Log.d(TAG, "Details for id=" + IRON_MAN_ID + ": " + (details != null ? details.getTitle() : "not found")));

        vm.getFavorites().observe(this, (List<Movie> favorites) -> {
            Log.d(TAG, "Favorites count (Room): " + favorites.size());
            for (Movie fav : favorites) {
                Log.d(TAG, "Favorite: " + fav.getTitle());
            }
        });

        vm.getClientInfo().observe(this, (ClientInfo clientInfo) ->
                Log.d(TAG, "Cached client info: " + (clientInfo != null ? clientInfo.getEmail() : "нет данных (ещё не входили)")));

        // MediatorLiveData из практики 3 — общий статус "сеть + БД".
        vm.getCombinedStatus().observe(this, (String status) -> statusText.setText(status));

        // Практика 5: ошибка сети → красный текст + кнопка "Повторить"; null → всё прячем.
        TextView errorText = findViewById(R.id.errorText);
        Button retryButton = findViewById(R.id.retryButton);
        vm.getError().observe(this, (String errorMessage) -> {
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

        startLoading();

        Button authButton = findViewById(R.id.authButton);
        authButton.setOnClickListener(v -> startActivity(new Intent(this, AuthActivity.class)));
    }

    // Запускаем загрузку (из onCreate и по кнопке "Повторить").
    // Сеть ViewModel сама уводит в фон, данные придут в наблюдатели выше.
    private void startLoading() {
        vm.loadMovies();
        vm.loadMovieDetails(IRON_MAN_ID);
        vm.addToFavoriteAndReload(IRON_MAN_ID);
        vm.loadClientInfo();
    }
}