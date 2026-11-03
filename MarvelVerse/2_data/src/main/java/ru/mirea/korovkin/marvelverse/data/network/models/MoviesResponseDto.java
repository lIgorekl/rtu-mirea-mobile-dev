package ru.mirea.korovkin.marvelverse.data.network.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// TMDB отдаёт список не голым массивом, а обёрткой:
// {"page":1, "results":[ ...фильмы... ], "total_pages":..., "total_results":...}
// мне нужен только results
public class MoviesResponseDto {
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<MovieDto> results;

    public MoviesResponseDto() {
    }

    public int getPage() {
        return page;
    }

    public List<MovieDto> getResults() {
        return results;
    }
}
