package com.mirea.korovkin.recyclerviewapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// ViewHolder = одна карточка. findViewById делаю один раз здесь, дальше при прокрутке только меняю данные.
public class EventViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final TextView yearView;
    private final TextView titleView;
    private final TextView descriptionView;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        this.imageView = itemView.findViewById(R.id.imageView);
        this.yearView = itemView.findViewById(R.id.textViewYear);
        this.titleView = itemView.findViewById(R.id.textViewTitle);
        this.descriptionView = itemView.findViewById(R.id.textViewDescription);
    }

    public ImageView getImageView() { return imageView; }
    public TextView getYearView() { return yearView; }
    public TextView getTitleView() { return titleView; }
    public TextView getDescriptionView() { return descriptionView; }
}
