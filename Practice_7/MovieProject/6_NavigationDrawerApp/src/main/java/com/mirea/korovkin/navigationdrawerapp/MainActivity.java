package com.mirea.korovkin.navigationdrawerapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.mirea.korovkin.navigationdrawerapp.databinding.ActivityMainBinding;

// п.6 — Navigation Drawer. Концепция: приложение кофейни «Зерно»:
// Меню (напитки), Акции, Контакты. Разделы в боковой шторке, открывается гамбургером или свайпом.
// Переходы делает NavController, шторку с графом связывает NavigationUI.
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DrawerCheck";

    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // dark() = белые значки в статус-баре, раз под ним цветной тулбар
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // отступы: сверху под статус-бар (фон там коричневый, как тулбар), снизу — у контента,
        // чтобы низ экрана не уезжал под системную навигацию
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarMain.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, 0);
            binding.appBarMain.contentMain.getRoot().setPadding(0, 0, 0, bars.bottom);
            return insets;
        });

        // тулбар из app_bar_main.xml становится ActionBar'ом
        setSupportActionBar(binding.appBarMain.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // три раздела верхнего уровня + setOpenableLayout: вместо стрелки Up будет гамбургер,
        // который открывает именно эту шторку
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_menu, R.id.nav_promo, R.id.nav_contacts)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // клик по пункту шторки = переход по графу + шторка сама закроется
        NavigationUI.setupWithNavController(binding.navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                Log.d(TAG, "Destination: " + destination.getLabel()));

        setupBackClosesDrawer();
    }

    // задание п.6: "Back" при открытой шторке её закрывает, а не выходит из приложения.
    // колбэк включён ТОЛЬКО пока шторка открыта — иначе "назад" работает как обычно.
    // NB: на Android 13+ (у нас targetSdk 37, predictive back) открытая DrawerLayout сама
    // регистрирует системный back-колбэк, и он срабатывает раньше моего — тогда шторку
    // закрывает она, а мой колбэк — запасной (старые Android / если её колбэк не сработал).
    // Поэтому факт закрытия логирую в onDrawerClosed — он срабатывает в любом случае
    private void setupBackClosesDrawer() {
        OnBackPressedCallback closeDrawerCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed → закрываю шторку");
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, closeDrawerCallback);

        // слежу за шторкой и включаю/выключаю колбэк
        binding.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                Log.d(TAG, "Шторка открыта");
                closeDrawerCallback.setEnabled(true);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                Log.d(TAG, "Шторка закрыта (Back / свайп / выбор пункта)");
                closeDrawerCallback.setEnabled(false);
            }
        });
        // после поворота шторка может быть уже открыта — сразу выставляю правильно
        closeDrawerCallback.setEnabled(binding.drawerLayout.isDrawerOpen(GravityCompat.START));
    }

    // гамбургер / стрелка в тулбаре: на верхних разделах откроет шторку
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
