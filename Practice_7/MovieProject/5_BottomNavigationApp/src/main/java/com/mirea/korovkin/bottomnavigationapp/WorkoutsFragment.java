package com.mirea.korovkin.bottomnavigationapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirea.korovkin.bottomnavigationapp.databinding.FragmentWorkoutsBinding;

// вкладка "Тренировки": план на неделю, просто текстом
public class WorkoutsFragment extends Fragment {

    private FragmentWorkoutsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // собираю список строкой — RecyclerView тут ради трёх строк не нужен
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] plan = {
                "Пн — Бег 5 км",
                "Ср — Силовая: ноги + пресс",
                "Пт — Плавание 40 минут",
                "Сб — Растяжка и йога"
        };
        StringBuilder sb = new StringBuilder();
        for (String item : plan) {
            sb.append("• ").append(item).append("\n\n");
        }
        binding.planText.setText(sb.toString().trim());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
