package ru.mirea.korovkin.marvelverse.data.storage.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Строка таблицы избранного — хранится только id фильма, сами детали фильма в БД не дублируем.
@Entity(tableName = "favorite_movies")
public class FavoriteMovieEntity {

    @PrimaryKey
    private int movieId;

    public FavoriteMovieEntity(int movieId) {
        this.movieId = movieId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}
