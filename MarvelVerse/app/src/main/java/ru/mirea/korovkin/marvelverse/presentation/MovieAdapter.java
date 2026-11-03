package ru.mirea.korovkin.marvelverse.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.korovkin.marvelverse.R;
import ru.mirea.korovkin.marvelverse.domain.models.Movie;

// Адаптер для списка фильмов на главном экране (практика 4, постеры через Glide — практика 5).
// Сам данные не грузит — их сюда кладёт MovieListFragment через setItems(), когда приходит LiveData.
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    // практика 6: клик по карточке отдаю наружу (фрагмент откроет детали), сам адаптер никуда не ходит
    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private final OnMovieClickListener clickListener;
    private List<Movie> movies = new ArrayList<>();

    public MovieAdapter(OnMovieClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // Вызывается из observe() в MovieListFragment: подменили список и сказали ленте перерисоваться.
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
        holder.bind(movies.get(position), clickListener);
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

        void bind(Movie movie, OnMovieClickListener clickListener) {
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

            // раньше тут был тост, теперь — переход на детали через фрагмент
            itemView.setOnClickListener(v -> clickListener.onMovieClick(movie));
        }
    }
}