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
import ru.mirea.korovkin.marvelverse.domain.models.Movie;

// Практика 7: вкладка "Избранное" (экран 4 из ТЗ). До этого Room-избранное было видно
// только в Logcat. Данные — из той же общей MainViewModel (favorites), список — тот же MovieAdapter.
// Гость видит заглушку с кнопкой входа, вошедший — свой список. Клик → детали через action.
public class FavoritesFragment extends Fragment {

    private static final String TAG = "MarvelVerseCheck";

    private MainViewModel vm;

    // views, которые прячу/показываю в render()
    private View guestBlock;
    private TextView emptyText;
    private RecyclerView favoritesRecyclerView;

    // общая ViewModel на Activity, как у списка и деталей
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity(), new ViewModelFactory(requireContext()))
                .get(MainViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    // лента + подписки на избранное и юзера
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        guestBlock = view.findViewById(R.id.guestBlock);
        emptyText = view.findViewById(R.id.emptyText);
        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView);

        // тот же адаптер, что на главном: клик → фильм в ViewModel → action в детали
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        MovieAdapter adapter = new MovieAdapter(movie -> {
            Log.d(TAG, "Open details from favorites: " + movie.getTitle());
            vm.selectMovie(movie);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_favorites_to_movieDetails);
        });
        favoritesRecyclerView.setAdapter(adapter);

        vm.getFavorites().observe(getViewLifecycleOwner(), (List<Movie> favorites) -> {
            Log.d(TAG, "Favorites tab: " + favorites.size());
            adapter.setItems(favorites);
            render();
        });
        vm.getCurrentUser().observe(getViewLifecycleOwner(), user -> render());

        // гость → сразу на экран входа (action_favorites_to_auth), после входа вернусь сюда
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_favorites_to_auth));
    }

    // при каждом показе вкладки: кто вошёл + свежее избранное из Room
    @Override
    public void onResume() {
        super.onResume();
        vm.loadCurrentUser();
        vm.loadFavorites();
    }

    // что показать: гость → заглушка, вошёл и пусто → "пока пусто", иначе список
    private void render() {
        if (guestBlock == null) return;
        boolean loggedIn = vm.getCurrentUser().getValue() != null;
        List<Movie> favorites = vm.getFavorites().getValue();
        boolean empty = favorites == null || favorites.isEmpty();

        guestBlock.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        emptyText.setVisibility(loggedIn && empty ? View.VISIBLE : View.GONE);
        favoritesRecyclerView.setVisibility(loggedIn && !empty ? View.VISIBLE : View.GONE);
    }

    // view умерла — ссылки отпускаю
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        guestBlock = null;
        emptyText = null;
        favoritesRecyclerView = null;
    }
}
