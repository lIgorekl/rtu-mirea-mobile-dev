package com.mirea.korovkin.retrofitapp;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Держит ссылки на вьюшки одной строки item.xml, чтобы не искать их каждый раз заново.
public class TodoViewHolder extends RecyclerView.ViewHolder {
    ImageView imageViewPicture;
    TextView textViewTitle;
    CheckBox checkBoxCompleted;

    public TodoViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewPicture = itemView.findViewById(R.id.imageViewPicture);
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
    }
}
