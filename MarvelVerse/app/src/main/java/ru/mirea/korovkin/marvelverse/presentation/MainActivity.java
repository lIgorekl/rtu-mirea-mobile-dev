package ru.mirea.korovkin.marvelverse.presentation;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ru.mirea.korovkin.marvelverse.R;

// Практика 6: Activity теперь только хост. Своего UI почти нет — один контейнер,
// а экраны (список, детали, профиль, вход) это фрагменты. Сама делает все транзакции
// FragmentManager'а, фрагменты просят её через интерфейс Navigator.
public class MainActivity extends AppCompatActivity implements Navigator {

    private static final String TAG = "MarvelVerseCheck";

    // имена записей в бэк-стеке (и теги фрагментов заодно)
    private static final String TAG_LIST = "movies";
    private static final String TAG_DETAILS = "details";
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_AUTH = "auth";

    // контейнер + первый фрагмент (список)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // отступы под статус-бар/навбар — как и раньше, только теперь на контейнере
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FragmentManager fm = getSupportFragmentManager();
        // просто для логов: сколько записей в бэк-стеке после каждого перехода
        fm.addOnBackStackChangedListener(() ->
                Log.d(TAG, "Back stack size: " + fm.getBackStackEntryCount()));

        // только при первом запуске — после поворота FragmentManager всё восстановит сам
        if (savedInstanceState == null) {
            // корневой экран кладу через add БЕЗ addToBackStack — назад с него = выход из приложения
            fm.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentContainer, MovieListFragment.class, null, TAG_LIST)
                    .commit();
        }
    }

    @Override
    public void openMovieDetails() {
        open(MovieDetailsFragment.class, TAG_DETAILS);
    }

    @Override
    public void openProfile() {
        open(ProfileFragment.class, TAG_PROFILE);
    }

    @Override
    public void openAuth() {
        open(AuthFragment.class, TAG_AUTH);
    }

    // снимаем верхнюю транзакцию — вернётся предыдущий фрагмент
    @Override
    public void goBack() {
        getSupportFragmentManager().popBackStack();
    }

    // общий переход: replace текущего экрана + запись в бэк-стек,
    // поэтому системный "назад" вернёт предыдущий фрагмент, а не закроет приложение
    private void open(Class<? extends Fragment> fragmentClass, String name) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, fragmentClass, null, name)
                .addToBackStack(name)
                .commit();
    }
}
