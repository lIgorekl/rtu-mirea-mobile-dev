package com.mirea.korovkin.bottomnavigationapp;

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

import com.mirea.korovkin.bottomnavigationapp.databinding.ActivityMainBinding;

// п.5 — Bottom Navigation. Концепция: «FitStep», мини-трекер тренировок:
// Сегодня (шаги за день) / Тренировки (план на неделю) / Профиль.
// Сама Activity переходы не делает — всё через NavController, меню привязано NavigationUI.
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BottomNavCheck";

    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // dark() = белые значки в статус-баре, раз под ним цветной тулбар
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        // View Binding вместо setContentView(R.layout...) + findViewById
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // сверху отступ под статус-бар (там будет цвет тулбара), снизу не трогаю —
        // BottomNavigationView сам добавит себе отступ под системную навигацию
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, 0);
            return insets;
        });

        // свой тулбар вместо системного ActionBar (тема NoActionBar)
        setSupportActionBar(binding.toolbar);

        // у FragmentContainerView NavController берём через сам NavHostFragment
        // (Navigation.findNavController(this, id) в onCreate тут ещё не сработает)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // все три вкладки — "верхние" экраны: на них нет стрелки Up в тулбаре
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_today,
                R.id.navigation_workouts,
                R.id.navigation_profile)
                .build();
        // заголовок тулбара = android:label текущего пункта графа
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // клик по вкладке = navigate() к пункту графа с тем же id, подсветка активной — сама
        NavigationUI.setupWithNavController(binding.navView, navController);

        // просто для проверки в Logcat, куда перешли
        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                Log.d(TAG, "Destination: " + destination.getLabel()));
    }

    // стрелка Up в тулбаре (тут почти не нужна, но пусть ведёт себя как в методичке)
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
