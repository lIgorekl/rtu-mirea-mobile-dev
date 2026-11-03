package ru.mirea.korovkin.marvelverse.domain.repository;

import ru.mirea.korovkin.marvelverse.domain.models.User;

// Контракт авторизации. Реализация (AuthRepositoryImpl, модуль 2_data) прячет за собой
// Firebase Auth — domain и presentation про Firebase ничего не знают.
public interface AuthRepository {

    // Вход/регистрация у Firebase асинхронные, поэтому результат приходит через колбэк,
    // а не через обычный return.
    interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    void login(String email, String password, AuthCallback callback);

    void register(String email, String password, AuthCallback callback);

    void logout();

    User getCurrentUser();
}
