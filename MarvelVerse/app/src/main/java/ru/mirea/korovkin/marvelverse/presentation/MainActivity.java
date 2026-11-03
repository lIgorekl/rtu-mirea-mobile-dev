package ru.mirea.korovkin.marvelverse.presentation;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ru.mirea.korovkin.marvelverse.R;
import ru.mirea.korovkin.marvelverse.databinding.ActivityMainBinding;

// Практика 7: Activity всё ещё просто хост, но ручных транзакций FragmentManager'а больше нет.
// Экраны и переходы описаны в res/navigation/nav_graph.xml, меняет их NavHostFragment,
// а командует NavController. Внизу BottomNavigationView: Фильмы / Избранное / Профиль.
// Интерфейс Navigator из практики 6 убрал — фрагменты сами зовут findNavController().navigate(...)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MarvelVerseCheck";

    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // dark() — белые значки статус-бара на красном тулбаре
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        // п.1 методички: View Binding вместо findViewById
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // сверху/по бокам отступ под системные панели, снизу нет —
        // BottomNavigationView сам поднимется над системной навигацией
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

        // NavController достаю из самого NavHostFragment (с FragmentContainerView так надёжнее)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // три вкладки — экраны верхнего уровня: на них нет стрелки Up,
        // а на деталях и входе она появится сама
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.movieListFragment,
                R.id.favoritesFragment,
                R.id.profileFragment)
                .build();
        // заголовок тулбара берётся из android:label пункта графа
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // клик по вкладке → переход к пункту графа с тем же id (у каждой вкладки свой бэк-стек)
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        // вместо "Back stack size" из практики 6 — лог, куда пришли
        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                Log.d(TAG, "Navigation → " + destination.getLabel()));
    }

    // стрелка Up в тулбаре: делает то же, что "назад", но никогда не закрывает приложение
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
