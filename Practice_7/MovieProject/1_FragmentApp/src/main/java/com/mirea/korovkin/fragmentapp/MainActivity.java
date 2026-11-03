package com.mirea.korovkin.fragmentapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// AppCompatActivity уже FragmentActivity, так что getSupportFragmentManager есть
public class MainActivity extends AppCompatActivity implements FragmentListener {

    // мой номер по списку группы — его и передаём во фрагмент
    private static final int MY_NUMBER = 13;

    private TextView listenerText;

    // кладём BlankFragment в контейнер с номером в Bundle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listenerText = findViewById(R.id.listenerText);

        // только при первом запуске — после поворота фрагмент восстановится сам
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt(BlankFragment.KEY_NUMBER, MY_NUMBER);

            // в методичке bundle забыли передать — тут он идёт третьим аргументом (это arguments)
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, BlankFragment.class, bundle)
                    .commit();
        }
    }

    // сюда стучится BlankFragment через интерфейс
    @Override
    public void sendResult(String message) {
        Log.d(MainActivity.class.getSimpleName(), "message: " + message);
        listenerText.setText("Activity получила от фрагмента: " + message);
    }
}
