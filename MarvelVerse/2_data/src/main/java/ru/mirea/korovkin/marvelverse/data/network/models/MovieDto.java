package ru.mirea.korovkin.marvelverse.data.network.models;

import com.google.gson.annotations.SerializedName;

// DTO = как фильм приходит из TMDB (один элемент results или ответ movie/{id}).
// В JSON ещё куча полей (рейтинг, жанры...), Gson их просто пропускает — беру только нужные.
// Дальше MovieRepositoryImpl превращает его в доменный Movie (mapToDomain).
public class MovieDto {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    // у TMDB это не полный адрес, а кусок вида "/78lPtwv72eTNqFW9COBYI0dWDJa.jpg"
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;

    // пустой конструктор для Gson
    public MovieDto() {
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
