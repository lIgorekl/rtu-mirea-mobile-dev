package com.mirea.korovkin.fragmentmanagerapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

// хост: кладёт два фрагмента в свои контейнеры
public class MainActivity extends AppCompatActivity {

    private ShareViewModel viewModel;

    // ViewModel + первая транзакция с двумя add
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Activity тоже может слушать общую ViewModel — просто логирую выбор
        viewModel = new ViewModelProvider(this).get(ShareViewModel.class);
        viewModel.getSelectedItem().observe(this, item ->
                Log.d(MainActivity.class.getSimpleName(), "Выбрана: " + item.getName()));

        // только при первом запуске, после поворота FragmentManager восстановит сам
        if (savedInstanceState == null) {
            CountriesFragment countriesFragment = CountriesFragment.newInstance("Страны мира");
            // одна транзакция — сразу оба фрагмента, с тегами
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.headerContainerView, countriesFragment, "header")
                    .add(R.id.detailsContainerView, new DetailsFragment(), "details")
                    .commit();
        }
    }
}
