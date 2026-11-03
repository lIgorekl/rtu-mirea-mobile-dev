package ru.mirea.korovkin.marvelverse.domain.usecases;

import ru.mirea.korovkin.marvelverse.domain.repository.AuthRepository;

// Регистрация нового пользователя через Firebase Auth (тот же колбэк, что и у входа).
public class RegisterUseCase {

    private final AuthRepository authRepository;

    public RegisterUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String email, String password, AuthRepository.AuthCallback callback) {
        authRepository.register(email, password, callback);
    }
}
