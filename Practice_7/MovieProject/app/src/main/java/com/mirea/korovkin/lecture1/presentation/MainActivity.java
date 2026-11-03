package com.mirea.korovkin.lecture1.presentation;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mirea.korovkin.lecture1.R;
import com.mirea.korovkin.lecture1.domain.models.Movie;

public class MainActivity extends AppCompatActivity {

    private MainViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(MainActivity.class.getSimpleName(), "MainActivity created");
        vm = new ViewModelProvider(this, new ViewModelFactory(this)).get(MainViewModel.class);

        EditText editText = findViewById(R.id.editTextMovie);
        TextView textView = findViewById(R.id.textViewMovie);

        vm.getFavoriteMovie().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });

        findViewById(R.id.buttonSaveMovie).setOnClickListener(v -> {
            Movie movie = new Movie(2, editText.getText().toString());
            vm.setText(movie);
        });

        findViewById(R.id.buttonGetMovie).setOnClickListener(v -> {
            vm.getText();
        });
    }
}