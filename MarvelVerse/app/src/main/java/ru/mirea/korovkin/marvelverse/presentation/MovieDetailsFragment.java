package ru.mirea.korovkin.marvelverse.presentation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import ru.mirea.korovkin.marvelverse.R;

// Практика 6: детали фильма. Фильм не передаю через Bundle — список кладёт его
// в общую MainViewModel (scope Activity), а я отсюда просто подписываюсь.
// Как ShareViewModel в методичке: фрагменты друг про друга не знают.
public class MovieDetailsFragment extends Fragment {

    private static final String TAG = "MarvelVerseCheck";

    private Navigator navigator;

    // берём Activity как Navigator (для кнопки "назад")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Navigator)) {
            throw new ClassCastException(context + " must implement Navigator");
        }
        navigator = (Navigator) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_details, container, false);
    }

    // подписка на выбранный фильм + заполнение экрана
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // тот же экземпляр, что у списка, потому что scope — requireActivity()
        MainViewModel vm = new ViewModelProvider(requireActivity(), new ViewModelFactory(requireContext()))
                .get(MainViewModel.class);

        ImageView poster = view.findViewById(R.id.detailsPoster);
        TextView title = view.findViewById(R.id.detailsTitle);
        TextView release = view.findViewById(R.id.detailsRelease);
        TextView overview = view.findViewById(R.id.detailsOverview);

        vm.getSelectedMovie().observe(getViewLifecycleOwner(), movie -> {
            if (movie == null) return;
            Log.d(TAG, "Details fragment shows: " + movie.getTitle());
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
        });

        // кнопка делает то же, что системный "назад" — popBackStack
        view.findViewById(R.id.backButton).setOnClickListener(v -> navigator.goBack());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigator = null;
    }
}
