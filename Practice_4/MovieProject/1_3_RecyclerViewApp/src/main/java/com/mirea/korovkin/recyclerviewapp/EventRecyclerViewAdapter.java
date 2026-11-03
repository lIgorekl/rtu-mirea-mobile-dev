package com.mirea.korovkin.recyclerviewapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Адаптер: мост между списком событий и RecyclerView. Обязательные методы — create / bind / count.
public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private final List<HistoricalEvent> events;
    private Context context;

    public EventRecyclerViewAdapter(List<HistoricalEvent> events) {
        this.events = events;
    }

    // Нужна новая пустая карточка — надуваю её из event_item_view.xml
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.event_item_view, parent, false);
        return new EventViewHolder(itemView);
    }

    // Карточка появилась на экране — заполняю её событием под номером position
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        HistoricalEvent event = events.get(position);

        // картинку ищу по имени ресурса; если не нашлась — вернётся 0
        int resId = context.getResources().getIdentifier(event.getImageName(), "drawable", context.getPackageName());
        holder.getImageView().setImageResource(resId);
        holder.getYearView().setText(event.getYear() + " год");
        holder.getTitleView().setText(event.getTitle());
        holder.getDescriptionView().setText(event.getDescription());

        // клик по карточке — просто Toast с названием
        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Выбрано: " + event.getTitle(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
