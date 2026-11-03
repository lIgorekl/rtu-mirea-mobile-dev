package com.mirea.korovkin.retrofitapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Практика 5, разделы 1-2: дела с jsonplaceholder через Retrofit + картинки через Picasso.
// Логи по тегу MainActivity.
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private RecyclerView recyclerView;
    private TextView errorText;
    private TodoAdapter todoAdapter;
    private ApiService apiService;
    // запоминаю режим, чтобы новый адаптер (если создастся позже) сразу его знал
    private TodoAdapter.ImageMode currentMode = TodoAdapter.ImageMode.CENTER_CROP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        errorText = findViewById(R.id.errorText);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrofit = базовый адрес + Gson, который сам гоняет JSON <-> Java-объекты
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // по моему интерфейсу Retrofit сам собирает рабочий объект
        apiService = retrofit.create(ApiService.class);

        setupImageModeSwitch();
        loadTodos();
    }

    // радиокнопки сверху → говорю адаптеру, как теперь рисовать картинки
    private void setupImageModeSwitch() {
        RadioGroup group = findViewById(R.id.imageModeGroup);
        group.setOnCheckedChangeListener((g, checkedId) -> {
            if (checkedId == R.id.modeCenterInside) {
                currentMode = TodoAdapter.ImageMode.CENTER_INSIDE;
            } else if (checkedId == R.id.modeCircle) {
                currentMode = TodoAdapter.ImageMode.CIRCLE;
            } else {
                currentMode = TodoAdapter.ImageMode.CENTER_CROP;
            }
            if (todoAdapter != null) {
                todoAdapter.setImageMode(currentMode);
            }
        });
    }

    // GET todos. enqueue = запрос в фоне, а ответ прилетит уже в главный поток,
    // поэтому в onResponse можно спокойно трогать вьюшки
    private void loadTodos() {
        Call<List<Todo>> call = apiService.getTodos(20);
        call.enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(Call<List<Todo>> call, Response<List<Todo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // всё ок (код 2xx) — показываю список
                    List<Todo> todos = response.body();
                    Log.d(TAG, "Загружено дел: " + todos.size());
                    errorText.setVisibility(View.GONE);
                    todoAdapter = new TodoAdapter(MainActivity.this, todos, MainActivity.this::updateTodo);
                    todoAdapter.setImageMode(currentMode);
                    recyclerView.setAdapter(todoAdapter);
                } else {
                    // сервер ответил, но с ошибкой (404, 500...) — это тоже onResponse, не onFailure!
                    Log.e(TAG, "onResponse: " + response.code());
                    showError("Сервер ответил ошибкой: код " + response.code());
                }
            }

            // сюда — когда до сервера вообще не достучались или JSON кривой
            @Override
            public void onFailure(Call<List<Todo>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                showError("Не удалось загрузить дела: " + t.getMessage());
            }
        });
    }

    // задание 1: галочку тыкнули → PUT todos/{id} с обновлённым делом.
    // jsonplaceholder ничего реально не сохраняет, просто отвечает тем же JSON —
    // по ответу видно, что запрос дошёл
    private void updateTodo(Todo todo, boolean isChecked) {
        apiService.updateTodo(todo.getId(), todo).enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Todo updated = response.body();
                    String msg = "PUT ок (код " + response.code() + "): дело #" + updated.getId()
                            + " completed=" + updated.getCompleted();
                    Log.d(TAG, msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "PUT onResponse: " + response.code());
                    Toast.makeText(MainActivity.this, "PUT: ошибка сервера " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                Log.e(TAG, "PUT onFailure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "PUT не ушёл: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ошибку показываю прямо на экране красным, а не только в логе
    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }
}
