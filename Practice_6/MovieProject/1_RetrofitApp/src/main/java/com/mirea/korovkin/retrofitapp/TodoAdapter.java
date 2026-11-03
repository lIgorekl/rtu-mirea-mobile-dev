package com.mirea.korovkin.retrofitapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

// Заполняет строки списка делами. Картинки — через Picasso (задание 2),
// клик по галочке отдаю наружу в MainActivity, там уходит PUT (задание 1).
public class TodoAdapter extends RecyclerView.Adapter<TodoViewHolder> {

    // как показывать картинку — переключается радиокнопками сверху
    public enum ImageMode { CENTER_CROP, CENTER_INSIDE, CIRCLE }

    // MainActivity сюда подписывается, чтобы узнать про новую галочку
    public interface OnTodoCheckedListener {
        void onTodoChecked(Todo todo, boolean isChecked);
    }

    private final LayoutInflater layoutInflater;
    private final List<Todo> todos;
    private final OnTodoCheckedListener listener;
    private ImageMode imageMode = ImageMode.CENTER_CROP;

    public TodoAdapter(Context context, List<Todo> todoList, OnTodoCheckedListener listener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.todos = todoList;
        this.listener = listener;
    }

    // поменяли режим → перерисовать все строки, Picasso перегрузит картинки (из кэша быстро)
    public void setImageMode(ImageMode mode) {
        this.imageMode = mode;
        notifyDataSetChanged();
    }

    // новая пустая строка из item.xml
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item, parent, false);
        return new TodoViewHolder(view);
    }

    // заполняю строку делом под номером position
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.textViewTitle.setText(todo.getTitle());

        // сначала снимаю старый слушатель! строки переиспользуются, и setChecked
        // иначе дёрнул бы слушатель от прошлого дела и ушёл бы лишний PUT
        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setChecked(Boolean.TRUE.equals(todo.getCompleted()));
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            todo.setCompleted(isChecked);
            listener.onTodoChecked(todo, isChecked);
        });

        loadPicture(holder, todo);
    }

    // задание 2: картинка из интернета через Picasso. picsum отдаёт фотку по номеру,
    // беру id дела + 10, чтобы у каждой строки была своя. 400x250 — специально не квадрат,
    // так видно разницу между centerCrop и centerInside.
    private void loadPicture(TodoViewHolder holder, Todo todo) {
        String url = "https://picsum.photos/id/" + (todo.getId() + 10) + "/400/250";
        RequestCreator request = Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_image_placeholder) // пока грузится
                .error(R.drawable.ic_image_error)             // если не скачалось
                .resize(200, 200);                            // ужать под 64dp, меньше памяти

        // а вот тут как раз "настройка параметров отображения"
        switch (imageMode) {
            case CENTER_INSIDE:
                request.centerInside(); // влезает целиком, по бокам пусто
                break;
            case CIRCLE:
                request.centerCrop().transform(new CircleTransformation()); // моя круглая
                break;
            case CENTER_CROP:
            default:
                request.centerCrop(); // заполняет квадрат, края обрезаются
                break;
        }
        request.into(holder.imageViewPicture);
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }
}
