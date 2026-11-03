package com.mirea.korovkin.resultapifragmentapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// хост: кладёт DataFragment и сама тоже слушает результат
public class MainActivity extends AppCompatActivity {

    // слушатель на supportFragmentManager + первая транзакция
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView resultText = findViewById(R.id.activityResultText);

        // тот же ключ, но другой FragmentManager — поэтому нужен отдельный setFragmentResult
        getSupportFragmentManager().setFragmentResultListener(ResultKeys.REQUEST_KEY, this,
                (requestKey, bundle) -> {
                    String result = bundle.getString(ResultKeys.KEY_TEXT);
                    Log.d(MainActivity.class.getSimpleName(), "I'm MainActivity " + result);
                    resultText.setText("MainActivity получила: " + result);
                });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, DataFragment.class, null)
                    .commit();
        }
    }
}
