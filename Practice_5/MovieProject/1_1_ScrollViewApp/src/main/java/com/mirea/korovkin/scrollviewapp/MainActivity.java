package com.mirea.korovkin.scrollviewapp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigInteger;

// 1.1 ScrollView: геометрическая прогрессия со знаменателем 2, первые 100 элементов (1, 2, 4, 8, ...).
// Все 100 строк создаются сразу и висят в памяти — для ScrollView это норм, пока список маленький.
public class MainActivity extends AppCompatActivity {

    private static final int COUNT = 100;
    private static final BigInteger RATIO = BigInteger.valueOf(2); // знаменатель прогрессии

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout wrapper = findViewById(R.id.wrapper);

        // BigInteger, а не long: 2^99 в long не влезает (long максимум ~2^63)
        BigInteger value = BigInteger.ONE;
        for (int i = 0; i < COUNT; i++) {
            // LayoutInflater превращает item.xml в живой View, дальше заполняем и кидаем в контейнер
            View view = getLayoutInflater().inflate(R.layout.item, wrapper, false);
            TextView text = view.findViewById(R.id.textView);
            text.setText(String.format("Элемент %d: 2^%d = %s", i + 1, i, value.toString()));
            wrapper.addView(view);

            value = value.multiply(RATIO); // следующий член = предыдущий * 2
        }
    }
}
