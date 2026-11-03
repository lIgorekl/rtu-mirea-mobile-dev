package com.mirea.korovkin.bottomnavigationapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirea.korovkin.bottomnavigationapp.databinding.FragmentTodayBinding;

// вкладка "Сегодня": шаги за день и прогресс до цели. Данные пока захардкожены
public class TodayFragment extends Fragment {

    private static final int STEPS = 6540;
    private static final int GOAL = 10000;

    // binding живёт только пока жива view — в onDestroyView обнуляю
    private FragmentTodayBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // заполняю цифры и прогресс-бар
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.stepsText.setText(String.valueOf(STEPS));
        binding.goalText.setText("из " + GOAL + " шагов");
        binding.stepsProgress.setMax(GOAL);
        binding.stepsProgress.setProgress(STEPS);
        int left = GOAL - STEPS;
        binding.hintText.setText("Осталось " + left + " шагов — это примерно " + (left / 1300) + " км прогулки");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
