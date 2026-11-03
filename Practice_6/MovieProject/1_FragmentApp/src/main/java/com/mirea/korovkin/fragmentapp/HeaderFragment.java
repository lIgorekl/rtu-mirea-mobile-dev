package com.mirea.korovkin.fragmentapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// статичный фрагмент — создаётся сам из разметки (android:name в activity_main)
public class HeaderFragment extends Fragment {

    // просто раздуваем шапку
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(HeaderFragment.class.getSimpleName(), "onCreateView");
        return inflater.inflate(R.layout.fragment_header, container, false);
    }
}
