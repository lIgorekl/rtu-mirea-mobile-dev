package com.mirea.korovkin.navigationdrawerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirea.korovkin.navigationdrawerapp.databinding.FragmentMenuBinding;

// раздел "Меню": напитки с ценами, стартовый экран графа
public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // собираю меню строкой, данные захардкожены
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[][] drinks = {
                {"Эспрессо", "150 ₽"},
                {"Капучино", "220 ₽"},
                {"Латте на овсяном", "260 ₽"},
                {"Раф лавандовый", "290 ₽"},
                {"Какао с маршмеллоу", "240 ₽"}
        };
        StringBuilder sb = new StringBuilder();
        for (String[] d : drinks) {
            sb.append("☕ ").append(d[0]).append(" — ").append(d[1]).append("\n\n");
        }
        binding.menuText.setText(sb.toString().trim());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
