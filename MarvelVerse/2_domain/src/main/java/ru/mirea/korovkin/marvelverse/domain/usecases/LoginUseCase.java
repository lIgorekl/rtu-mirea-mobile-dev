package ru.mirea.korovkin.marvelverse.domain.usecases;

import ru.mirea.korovkin.marvelverse.domain.repository.AuthRepository;

// Вход по email/паролю через Firebase Auth (результат приходит в колбэке, см. AuthRepository.AuthCallback).
public class LoginUseCase {

    private final AuthRepository authRepository;

    public LoginUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String email, String password, AuthRepository.AuthCallback callback) {
        authRepository.login(email, password, callback);
    }
}
