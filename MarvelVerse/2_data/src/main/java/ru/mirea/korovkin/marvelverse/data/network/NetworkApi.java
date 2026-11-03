package ru.mirea.korovkin.marvelverse.data.network;

import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mirea.korovkin.marvelverse.data.BuildConfig;
import ru.mirea.korovkin.marvelverse.data.network.models.MovieDto;
import ru.mirea.korovkin.marvelverse.data.network.models.MoviesResponseDto;

/**
 * Практика 5: раньше тут был мок, теперь настоящий TMDB API через Retrofit.
 * Имя и методы оставил те же, поэтому RepositoryFactory и MovieRepositoryImpl почти не поменялись.
 * Вызовы синхронные (execute) — значит, звать ТОЛЬКО не из главного потока (это делает MainViewModel).
 * Из России TMDB открывается только с VPN.
 */
public class NetworkApi {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    // картинки у TMDB на отдельном сервере; w342 — ширина постера в пикселях
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w342";
    private static final String LANGUAGE = "ru-RU";
    // ключ из local.properties → build.gradle.kts → BuildConfig
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    private final MovieApiService service;

    // один раз собираю Retrofit: адрес + Gson для JSON -> DTO
    public NetworkApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MovieApiService.class);
    }

    // poster_path от TMDB → полный адрес картинки для Glide (постера может и не быть)
    public static String buildPosterUrl(String posterPath) {
        return posterPath != null ? IMAGE_BASE_URL + posterPath : null;
    }

    // фильмы Marvel Studios; любая проблема → NetworkException с понятным текстом
    public List<MovieDto> fetchMovies() {
        checkApiKey();
        try {
            Response<MoviesResponseDto> response = service.getMarvelMovies(API_KEY, LANGUAGE).execute();
            if (!response.isSuccessful()) {
                // сервер ответил, но кодом ошибки — Retrofit это исключением не считает
                throw new NetworkException(describeHttpError(response.code()));
            }
            MoviesResponseDto body = response.body();
            if (body == null || body.getResults() == null) {
                throw new NetworkException("TMDB прислал пустой ответ");
            }
            return body.getResults();
        } catch (IOException e) {
            // нет интернета / VPN выключен / таймаут
            throw new NetworkException("Нет связи с TMDB (VPN включён?): " + e.getMessage(), e);
        } catch (JsonParseException e) {
            // JSON пришёл, но не того формата
            throw new NetworkException("Не удалось разобрать ответ TMDB", e);
        }
    }

    // один фильм по id; если такого нет (404) — просто null, это не ошибка сети
    public MovieDto fetchMovie(int id) {
        checkApiKey();
        try {
            Response<MovieDto> response = service.getMovie(id, API_KEY, LANGUAGE).execute();
            if (response.code() == 404) {
                return null;
            }
            if (!response.isSuccessful()) {
                throw new NetworkException(describeHttpError(response.code()));
            }
            return response.body();
        } catch (IOException e) {
            throw new NetworkException("Нет связи с TMDB (VPN включён?): " + e.getMessage(), e);
        } catch (JsonParseException e) {
            throw new NetworkException("Не удалось разобрать ответ TMDB", e);
        }
    }

    // забыл вписать ключ — даже не иду в сеть, сразу говорю, что не так
    private void checkApiKey() {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new NetworkException("Нет API-ключа TMDB: добавь TMDB_API_KEY в local.properties");
        }
    }

    // самые частые коды TMDB — по-человечески
    private String describeHttpError(int code) {
        switch (code) {
            case 401:
                return "TMDB не принял API-ключ (код 401)";
            case 429:
                return "Слишком много запросов к TMDB (код 429), подожди немного";
            default:
                return "TMDB ответил ошибкой: код " + code;
        }
    }
}
