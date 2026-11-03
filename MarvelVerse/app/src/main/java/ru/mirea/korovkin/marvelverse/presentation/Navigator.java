package ru.mirea.korovkin.marvelverse.presentation;

// практика 6: все переходы между экранами — через этот интерфейс
// фрагменты друг про друга не знают, транзакции делает только MainActivity
public interface Navigator {
    // список -> детали (сам фильм уже лежит в общей MainViewModel)
    void openMovieDetails();

    // список -> профиль
    void openProfile();

    // профиль -> вход/регистрация
    void openAuth();

    // шаг назад по бэк-стеку, как системная кнопка "назад"
    void goBack();
}
