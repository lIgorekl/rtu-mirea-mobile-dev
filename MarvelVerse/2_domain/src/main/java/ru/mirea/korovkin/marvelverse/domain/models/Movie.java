package ru.mirea.korovkin.marvelverse.domain.models;

// Доменная модель фильма. Собирается в MovieRepositoryImpl: основные поля — из "сети"
// (MovieDto через NetworkApi), факт "в избранном" — отдельно, через Room.
public class Movie {
    private int id;
    private String title;
    private String posterUrl;
    private String overview;
    private String releaseDate;

    public Movie(int id, String title, String posterUrl, String overview, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterUrl() { return posterUrl; }
    public String getOverview() { return overview; }
    public String getReleaseDate() { return releaseDate; }
}
