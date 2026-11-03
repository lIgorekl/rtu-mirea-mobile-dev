package ru.mirea.korovkin.marvelverse.data.storage.room;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

// Доступ к таблице favorite_movies. REPLACE на insert — чтобы повторное добавление
// того же фильма не падало с ошибкой конфликта первичного ключа.
@Dao
public interface FavoriteMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteMovieEntity entity);

    @Delete
    void delete(FavoriteMovieEntity entity);

    @Query("SELECT * FROM favorite_movies WHERE movieId = :movieId LIMIT 1")
    FavoriteMovieEntity findById(int movieId);

    @Query("SELECT * FROM favorite_movies")
    List<FavoriteMovieEntity> getAll();
}
