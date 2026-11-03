package ru.mirea.korovkin.marvelverse.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.korovkin.marvelverse.R;
import ru.mirea.korovkin.marvelverse.domain.models.Movie;

// Адаптер для списка фильмов на главном экране (практика 4, постеры через Glide — практика 5).
// Сам данные не грузит — их сюда кладёт MainActivity через setItems(), когда приходит LiveData.
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies = new ArrayList<>();

    // Вызывается из observe() в MainActivity: подменили список и сказали ленте перерисоваться.
    public void setItems(List<Movie> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    // Лента просит новую пустую карточку — "надуваем" её из item_movie.xml.
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(itemView);
    }

    // Лента просит заполнить карточку фильмом под номером position.
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    // Сколько всего элементов в списке.
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // Одна карточка. findViewById делаем один раз в конструкторе, потом только меняем данные.
    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView posterImage;
        private final TextView titleText;
        private final TextView releaseText;
        private final TextView overviewText;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.posterImage);
            titleText = itemView.findViewById(R.id.titleText);
            releaseText = itemView.findViewById(R.id.releaseText);
            overviewText = itemView.findViewById(R.id.overviewText);
        }

        void bind(Movie movie) {
            titleText.setText(movie.getTitle());
            releaseText.setText("Дата выхода: " + movie.getReleaseDate());
            overviewText.setText(movie.getOverview());
            // Практика 5: постер из сети через Glide. Сам качает в фоне и кэширует.
            // Пока качается — хлопушка, не скачалось — значок "нельзя".
            Glide.with(posterImage)
                    .load(movie.getPosterUrl())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_error)
                    .centerCrop()
                    .into(posterImage);

            itemView.setOnClickListener(v ->
                    Toast.makeText(v.getContext(), "Выбран: " + movie.getTitle(), Toast.LENGTH_SHORT).show());
        }
    }
}