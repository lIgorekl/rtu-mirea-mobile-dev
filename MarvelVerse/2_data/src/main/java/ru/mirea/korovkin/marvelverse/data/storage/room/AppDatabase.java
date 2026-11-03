package ru.mirea.korovkin.marvelverse.data.storage.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Room-база для избранного. Синглтон (instance) — чтобы на весь процесс была одна база,
// а не создавать её заново при каждом обращении.
@Database(entities = {FavoriteMovieEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract FavoriteMovieDao favoriteMovieDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "marvelverse-db")
                            // Практика 2: упрощённо разрешаем запросы на главном потоке.
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
