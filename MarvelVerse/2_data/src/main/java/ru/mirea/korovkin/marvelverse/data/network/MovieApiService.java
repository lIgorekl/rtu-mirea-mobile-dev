package ru.mirea.korovkin.marvelverse.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.mirea.korovkin.marvelverse.data.network.models.MovieDto;
import ru.mirea.korovkin.marvelverse.data.network.models.MoviesResponseDto;

// Retrofit-интерфейс к TMDB API v3. Пути приклеиваются к BASE_URL из NetworkApi.
// Ключ передаю через @Query("api_key") в каждом запросе — так TMDB v3 и просит.
public interface MovieApiService {

    // GET discover/movie — фильмы студии Marvel Studios (id компании в TMDB = 420),
    // самые популярные сверху. Неизменные параметры прямо в пути, меняющиеся — через @Query
    @GET("discover/movie?with_companies=420&sort_by=popularity.desc")
    Call<MoviesResponseDto> getMarvelMovies(@Query("api_key") String apiKey,
                                            @Query("language") String language);

    // GET movie/1726 — детали одного фильма, {movie_id} подставится из @Path
    @GET("movie/{movie_id}")
    Call<MovieDto> getMovie(@Path("movie_id") int movieId,
                            @Query("api_key") String apiKey,
                            @Query("language") String language);
}
