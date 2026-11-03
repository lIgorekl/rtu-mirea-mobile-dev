package ru.mirea.korovkin.marvelverse.data.repository;

import android.content.Context;

import ru.mirea.korovkin.marvelverse.data.ml.BlazeFaceDetector;
import ru.mirea.korovkin.marvelverse.data.network.NetworkApi;
import ru.mirea.korovkin.marvelverse.data.storage.room.AppDatabase;
import ru.mirea.korovkin.marvelverse.data.storage.sharedprefs.SharedPrefClientInfoStorage;
import ru.mirea.korovkin.marvelverse.domain.repository.ClientInfoRepository;
import ru.mirea.korovkin.marvelverse.domain.repository.FaceRecognitionRepository;
import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;

/**
 * Точка входа presentation-слоя в data-слой.
 * Наружу отдаются только доменные интерфейсы — Room, SharedPreferences и NetworkApi
 * остаются деталями реализации внутри модуля data.
 * Практика 8: + распознавание лиц (TFLite-модель BlazeFace через MediaPipe) — тоже прячется здесь.
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

    // практика 8: MediaPipe тоже implementation-зависимость data → снаружи видно только интерфейс
    public static FaceRecognitionRepository createFaceRecognitionRepository(Context context) {
        return new FaceRecognitionRepositoryImpl(new BlazeFaceDetector(context));
    }
}
