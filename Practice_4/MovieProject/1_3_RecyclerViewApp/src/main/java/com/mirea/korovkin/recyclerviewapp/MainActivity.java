package com.mirea.korovkin.recyclerviewapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// 1.3 RecyclerView: список исторических событий с кратким описанием и картинкой.
public class MainActivity extends AppCompatActivity {

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

        List<HistoricalEvent> events = getListData();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new EventRecyclerViewAdapter(events));

        // LayoutManager решает, КАК раскладывать: тут обычный вертикальный список
        // (можно GridLayoutManager(this, 2) — будет сетка, или StaggeredGridLayoutManager)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    // Захардкоженные события по порядку лет
    private List<HistoricalEvent> getListData() {
        List<HistoricalEvent> list = new ArrayList<>();
        list.add(new HistoricalEvent("Крещение Руси", 988,
                "Князь Владимир принимает христианство, Русь становится православной.", "event_baptism"));
        list.add(new HistoricalEvent("Куликовская битва", 1380,
                "Дмитрий Донской разбивает войско Мамая на Куликовом поле.", "event_kulikovo"));
        list.add(new HistoricalEvent("Изобретение книгопечатания", 1440,
                "Иоганн Гутенберг создаёт печатный станок с подвижными литерами.", "event_printing"));
        list.add(new HistoricalEvent("Открытие Америки", 1492,
                "Экспедиция Христофора Колумба достигает островов Нового Света.", "event_columbus"));
        list.add(new HistoricalEvent("Основание Санкт-Петербурга", 1703,
                "Пётр I закладывает Петропавловскую крепость в устье Невы.", "event_petersburg"));
        list.add(new HistoricalEvent("Бородинское сражение", 1812,
                "Крупнейшая битва Отечественной войны с армией Наполеона.", "event_borodino"));
        list.add(new HistoricalEvent("Отмена крепостного права", 1861,
                "Александр II подписывает манифест об освобождении крестьян.", "event_serfdom"));
        list.add(new HistoricalEvent("Изобретение радио", 1895,
                "Александр Попов демонстрирует первый радиоприёмник.", "event_radio"));
        list.add(new HistoricalEvent("Победа в Великой Отечественной войне", 1945,
                "9 мая подписан акт о капитуляции Германии.", "event_victory"));
        list.add(new HistoricalEvent("Первый полёт человека в космос", 1961,
                "Юрий Гагарин на корабле «Восток-1» облетел Землю за 108 минут.", "event_gagarin"));
        list.add(new HistoricalEvent("Высадка человека на Луну", 1969,
                "Нил Армстронг и Базз Олдрин ступают на поверхность Луны.", "event_moon"));
        list.add(new HistoricalEvent("Падение Берлинской стены", 1989,
                "Граница между Восточным и Западным Берлином открывается.", "event_berlin_wall"));
        return list;
    }
}
