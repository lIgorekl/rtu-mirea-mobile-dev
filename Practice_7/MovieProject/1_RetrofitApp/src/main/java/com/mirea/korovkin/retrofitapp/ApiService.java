package com.mirea.korovkin.retrofitapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

// Тут только "что спросить у сервера", а сам код запросов Retrofit пишет за меня.
// Путь в аннотации приклеивается к BASE_URL из MainActivity.
public interface ApiService {

    // GET .../todos?_limit=20 — _limit понимает jsonplaceholder, чтобы не тащить все 200 штук
    @GET("todos")
    Call<List<Todo>> getTodos(@Query("_limit") int limit);

    // задание 1: PUT .../todos/5 — {id} подставится из @Path, тело (Todo -> JSON) из @Body
    @PUT("todos/{id}")
    Call<Todo> updateTodo(@Path("id") int id, @Body Todo todo);
}
