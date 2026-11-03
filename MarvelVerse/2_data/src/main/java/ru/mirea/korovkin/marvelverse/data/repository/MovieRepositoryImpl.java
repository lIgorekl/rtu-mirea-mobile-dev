package ru.mirea.korovkin.marvelverse.data.repository;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.korovkin.marvelverse.data.network.NetworkApi;
import ru.mirea.korovkin.marvelverse.data.network.models.MovieDto;
import ru.mirea.korovkin.marvelverse.data.storage.room.FavoriteMovieDao;
import ru.mirea.korovkin.marvelverse.data.storage.room.FavoriteMovieEntity;
import ru.mirea.korovkin.marvelverse.domain.models.Movie;
import ru.mirea.korovkin.marvelverse.domain.repository.MovieRepository;

// Единственная реализация MovieRepository — совмещает два источника:
// каталог фильмов берёт из TMDB (NetworkApi → Retrofit, с практики 5), а избранное — из Room.
// Сетевые методы блокирующие и могут кинуть NetworkException — вызывать только в фоне.
public class MovieRepositoryImpl implements MovieRepository {

    private final NetworkApi networkApi;
    private final FavoriteMovieDao favoriteMovieDao;
    // последний загруженный каталог: чтобы детали/избранное не ходили в сеть лишний раз
    private volatile List<Movie> cachedMovies;

    public MovieRepositoryImpl(NetworkApi networkApi, FavoriteMovieDao favoriteMovieDao) {
        this.networkApi = networkApi;
        this.favoriteMovieDao = favoriteMovieDao;
    }

    // каталог всегда свежий из сети, заодно обновляю кэш
    @Override
    public List<Movie> getMovies() {
        List<Movie> movies = new ArrayList<>();
        for (MovieDto dto : networkApi.fetchMovies()) {
            movies.add(mapToDomain(dto));
        }
        cachedMovies = movies;
        return movies;
    }

    // сначала ищу в кэше, если там нет — отдельный запрос movie/{id} к TMDB
    @Override
    public Movie getMovieDetails(int id) {
        List<Movie> cache = cachedMovies;
        if (cache != null) {
            for (Movie movie : cache) {
                if (movie.getId() == id) {
                    return movie;
                }
            }
        }
        MovieDto dto = networkApi.fetchMovie(id);
        return dto != null ? mapToDomain(dto) : null;
    }

    @Override
    public void addToFavorites(int movieId) {
        favoriteMovieDao.insert(new FavoriteMovieEntity(movieId));
    }

    @Override
    public void removeFromFavorites(int movieId) {
        favoriteMovieDao.delete(new FavoriteMovieEntity(movieId));
    }

    @Override
    public boolean isFavorite(int movieId) {
        return favoriteMovieDao.findById(movieId) != null;
    }

    @Override
    public List<Movie> getFavoriteMovies() {
        // В Room лежат только id избранных фильмов (FavoriteMovieEntity),
        // сами детали фильма берём из сети/кэша — через getMovieDetails.
        List<Movie> favorites = new ArrayList<>();
        for (FavoriteMovieEntity entity : favoriteMovieDao.getAll()) {
            Movie movie = getMovieDetails(entity.getMovieId());
            if (movie != null) {
                favorites.add(movie);
            }
        }
        return favorites;
    }

    // MovieDto (формат сети) -> Movie (доменная модель).
    private Movie mapToDomain(MovieDto dto) {
        // у TMDB в DTO только кусок пути постера — полный адрес собирает NetworkApi
        return new Movie(dto.getId(), dto.getTitle(), NetworkApi.buildPosterUrl(dto.getPosterPath()),
                dto.getOverview(), dto.getReleaseDate());
    }
}
