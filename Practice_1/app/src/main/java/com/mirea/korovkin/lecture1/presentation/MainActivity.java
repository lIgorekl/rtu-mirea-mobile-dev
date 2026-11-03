package com.mirea.korovkin.lecture1.presentation;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mirea.korovkin.lecture1.R;
import com.mirea.korovkin.lecture1.domain.models.Movie;
import com.mirea.korovkin.lecture1.domain.repository.MovieRepository;
import com.mirea.korovkin.lecture1.domain.usecases.GetFavoriteFilmUseCase;
import com.mirea.korovkin.lecture1.domain.usecases.SaveMovieToFavoriteUseCase;
import com.mirea.korovkin.lecture1.data.repository.MovieRepositoryImpl;

public class MainActivity extends AppCompatActivity {

    private MovieRepository movieRepository; // тип - интерфейс, не Impl

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // тут единственное место во всём проекте, где встречаются domain и data
        movieRepository = new MovieRepositoryImpl(this);

        EditText editText = findViewById(R.id.editTextMovie);
        TextView textView = findViewById(R.id.textViewMovie);

        // сохранение - беру текст из поля, оборачиваю в Movie(id=2, ...) и вызываю use-case
        findViewById(R.id.buttonSaveMovie).setOnClickListener(v -> {
            Movie movie = new Movie(2, editText.getText().toString());
            boolean result = new SaveMovieToFavoriteUseCase(movieRepository).execute(movie);
            textView.setText(String.format("Save result %s", result));
        });

        // отображение сохраненного фильма
        findViewById(R.id.buttonGetMovie).setOnClickListener(v -> {
            Movie movie = new GetFavoriteFilmUseCase(movieRepository).execute();
            if (movie.getName() == null || movie.getName().isEmpty()) {
                textView.setText("Нет сохранённого фильма");
            } else {
                textView.setText(String.format("My favorite movie is %s", movie.getName()));
            }
        });
    }
}