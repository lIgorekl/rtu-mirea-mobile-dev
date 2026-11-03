package ru.mirea.korovkin.marvelverse.presentation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import ru.mirea.korovkin.marvelverse.R;
import ru.mirea.korovkin.marvelverse.domain.models.Movie;

// Практика 6: детали фильма. Фильм не передаю через Bundle — список кладёт его
// в общую MainViewModel (scope Activity), а я отсюда просто подписываюсь.
// Практика 7: сюда ведут два action'а (из списка и из избранного). Назад — через NavController.
// Плюс кнопка "В избранное / Убрать из избранного", видна только вошедшему.
public class MovieDetailsFragment extends Fragment {

    private static final String TAG = "MarvelVerseCheck";

    private MainViewModel vm;
    // текущий фильм на экране и кнопка избранного — нужны в updateFavoriteButton()
    private Movie shownMovie;
    private Button favoriteButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_details, container, false);
    }

    // подписка на выбранный фильм, избранное и юзера + кнопки
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // тот же экземпляр, что у списка, потому что scope — requireActivity()
        vm = new ViewModelProvider(requireActivity(), new ViewModelFactory(requireContext()))
                .get(MainViewModel.class);

        ImageView poster = view.findViewById(R.id.detailsPoster);
        TextView title = view.findViewById(R.id.detailsTitle);
        TextView release = view.findViewById(R.id.detailsRelease);
        TextView overview = view.findViewById(R.id.detailsOverview);
        favoriteButton = view.findViewById(R.id.favoriteButton);

        vm.getSelectedMovie().observe(getViewLifecycleOwner(), movie -> {
            if (movie == null) return;
            Log.d(TAG, "Details fragment shows: " + movie.getTitle());
            shownMovie = movie;
            title.setText(movie.getTitle());
            release.setText("Дата выхода: " + movie.getReleaseDate());
            // у некоторых фильмов TMDB описания на русском нет — пишу заглушку
            String text = movie.getOverview();
            overview.setText(text == null || text.isEmpty() ? "Описание пока не добавлено." : text);
            // Glide как в списке, только картинка больше
            Glide.with(this)
                    .load(movie.getPosterUrl())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_error)
                    .centerCrop()
                    .into(poster);
            updateFavoriteButton();
        });

        // избранное поменялось (добавил/убрал) или юзер вошёл/вышел — перерисовать кнопку
        vm.getFavorites().observe(getViewLifecycleOwner(), favorites -> updateFavoriteButton());
        vm.getCurrentUser().observe(getViewLifecycleOwner(), user -> updateFavoriteButton());

        // клик: есть в избранном — убираю, нет — добавляю. Room в фоне, список придёт через LiveData
        favoriteButton.setOnClickListener(v -> {
            if (shownMovie == null) return;
            if (vm.isInFavorites(shownMovie.getId())) {
                Log.d(TAG, "Remove from favorites: " + shownMovie.getTitle());
                vm.removeFromFavoriteAndReload(shownMovie.getId());
            } else {
                Log.d(TAG, "Add to favorites: " + shownMovie.getTitle());
                vm.addToFavoriteAndReload(shownMovie.getId());
            }
        });

        // своя кнопка "назад" — popBackStack, как в методичке:
        // если снимать нечего (false) — закрываю Activity, чтобы не остался пустой экран
        view.findViewById(R.id.backButton).setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            if (!navController.popBackStack()) {
                requireActivity().finish();
            }
        });
    }

    // каждый показ — проверяю, кто вошёл (могли выйти во вкладке профиля)
    @Override
    public void onResume() {
        super.onResume();
        vm.loadCurrentUser();
    }

    // гость кнопку не видит (избранное только для вошедших — требование 6 из ТЗ),
    // вошедший видит "В избранное" или "Убрать из избранного"
    private void updateFavoriteButton() {
        if (favoriteButton == null) return;
        boolean loggedIn = vm.getCurrentUser().getValue() != null;
        if (!loggedIn || shownMovie == null) {
            favoriteButton.setVisibility(View.GONE);
            return;
        }
        favoriteButton.setVisibility(View.VISIBLE);
        favoriteButton.setText(vm.isInFavorites(shownMovie.getId())
                ? "★ Убрать из избранного"
                : "☆ В избранное");
    }

    // view умерла — ссылку на кнопку отпускаю
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        favoriteButton = null;
    }
}
