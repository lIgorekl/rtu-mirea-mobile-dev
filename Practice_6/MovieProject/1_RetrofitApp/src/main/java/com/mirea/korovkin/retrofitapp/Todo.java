package com.mirea.korovkin.retrofitapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// POJO под JSON одного дела с jsonplaceholder: {"userId":1,"id":1,"title":"...","completed":false}
// @SerializedName = как поле называется в JSON, Gson по нему и раскладывает.
public class Todo {
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("completed")
    @Expose
    private Boolean completed;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    // дёргаю, когда тыкают галочку, чтобы потом отправить на сервер уже новое значение
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
