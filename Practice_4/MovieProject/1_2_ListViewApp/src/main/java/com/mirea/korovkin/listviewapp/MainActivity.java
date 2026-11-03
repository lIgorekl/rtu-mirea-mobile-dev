package com.mirea.korovkin.listviewapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// 1.2 ListView: список авторов и книг, которые планирую прочитать в ближайшие 30 лет (> 30 штук).
// Три части: виджет (ListView) + источник данных (массив books) + адаптер (ArrayAdapter) между ними.
public class MainActivity extends AppCompatActivity {

    private final Book[] books = {
            new Book("Михаил Булгаков", "Мастер и Маргарита"),
            new Book("Фёдор Достоевский", "Преступление и наказание"),
            new Book("Фёдор Достоевский", "Братья Карамазовы"),
            new Book("Лев Толстой", "Война и мир"),
            new Book("Лев Толстой", "Анна Каренина"),
            new Book("Александр Пушкин", "Евгений Онегин"),
            new Book("Михаил Лермонтов", "Герой нашего времени"),
            new Book("Николай Гоголь", "Мёртвые души"),
            new Book("Иван Гончаров", "Обломов"),
            new Book("Антон Чехов", "Рассказы"),
            new Book("Михаил Шолохов", "Тихий Дон"),
            new Book("Аркадий и Борис Стругацкие", "Пикник на обочине"),
            new Book("Аркадий и Борис Стругацкие", "Трудно быть богом"),
            new Book("Иван Ефремов", "Туманность Андромеды"),
            new Book("Джордж Оруэлл", "1984"),
            new Book("Олдос Хаксли", "О дивный новый мир"),
            new Book("Рэй Брэдбери", "451° по Фаренгейту"),
            new Book("Айзек Азимов", "Основание"),
            new Book("Фрэнк Герберт", "Дюна"),
            new Book("Станислав Лем", "Солярис"),
            new Book("Габриэль Гарсиа Маркес", "Сто лет одиночества"),
            new Book("Эрих Мария Ремарк", "Три товарища"),
            new Book("Эрнест Хемингуэй", "Старик и море"),
            new Book("Джером Сэлинджер", "Над пропастью во ржи"),
            new Book("Юваль Ной Харари", "Sapiens. Краткая история человечества"),
            new Book("Даниэль Канеман", "Думай медленно… решай быстро"),
            new Book("Наполеон Хилл", "Думай и богатей"),
            new Book("Роберт Мартин", "Чистый код"),
            new Book("Роберт Мартин", "Чистая архитектура"),
            new Book("Эрих Гамма и др.", "Приёмы объектно-ориентированного проектирования"),
            new Book("Мартин Фаулер", "Рефакторинг"),
            new Book("Мартин Клеппман", "Высоконагруженные приложения"),
            new Book("Эндрю Хант, Дэвид Томас", "Программист-прагматик"),
            new Book("Стив Макконнелл", "Совершенный код"),
    };

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

        ListView bookList = findViewById(R.id.book_list_view);

        // simple_list_item_2 — стандартная разметка из SDK: два TextView друг под другом (text1, text2).
        // getView переопределяю, чтобы сверху было "№. Название", снизу — автор.
        ArrayAdapter<Book> adapter = new ArrayAdapter<Book>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, books) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                // super.getView сам переиспользует convertView, если он не null
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                Book book = getItem(position);
                text1.setText((position + 1) + ". " + book.getTitle());
                text2.setText(book.getAuthor());
                return view;
            }
        };

        bookList.setAdapter(adapter); // без этого список пустой
    }
}
