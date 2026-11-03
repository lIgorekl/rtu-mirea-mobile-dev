package ru.mirea.korovkin.marvelverse.data.repository;

import android.content.Context;

import ru.mirea.korovkin.marvelverse.data.network.NetworkApi;
import ru.mirea.korovkin.marvelverse.data.storage.room.AppDatabase;
import ru.mirea.korovkin.marvelverse.data.storage.sharedprefs.SharedPrefClientInfoStorage;
import ru.mirea.korovkin.marvelverse.domain.repository.ClientInfoRepository;
import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;

/**
 * Точка входа presentation-слоя в data-слой.
 * Наружу отдаются только доменные интерфейсы — Room, SharedPreferences и NetworkApi
 * остаются деталями реализации внутри модуля data.
 */
public final class RepositoryFactory {

    private RepositoryFactory() {
    }

    public static MovieRepository createMovieRepository(Context context) {
        return new MovieRepositoryImpl(
                new NetworkApi(),
                AppDatabase.getInstance(context).favoriteMovieDao()
        );
    }

    public static ClientInfoRepository createClientInfoRepository(Context context) {
        return new ClientInfoRepositoryImpl(new SharedPrefClientInfoStorage(context));
    }
}
