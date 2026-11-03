package com.mirea.korovkin.fragmentmanagerapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

// второй фрагмент — детали выбранной страны, сам слушает ShareViewModel
public class DetailsFragment extends Fragment {

    private ShareViewModel viewModel;

    // раздуваем разметку деталей
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    // подписка на выбранную страну
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);

        TextView flag = view.findViewById(R.id.flagText);
        TextView name = view.findViewById(R.id.nameText);
        TextView capital = view.findViewById(R.id.capitalText);
        TextView population = view.findViewById(R.id.populationText);
        TextView description = view.findViewById(R.id.descriptionText);

        // getViewLifecycleOwner, а не this — чтобы не держать мёртвые view
        viewModel.getSelectedItem().observe(getViewLifecycleOwner(), country -> {
            Log.d(DetailsFragment.class.getSimpleName(), country.getName());
            flag.setText(country.getFlag());
            name.setText(country.getName());
            capital.setText("Столица: " + country.getCapital());
            population.setText("Население: " + country.getPopulation());
            description.setText(country.getDescription());
        });
    }
}
