package ru.mirea.korovkin.marvelverse.presentation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.mirea.korovkin.marvelverse.R;
import ru.mirea.korovkin.marvelverse.data.repository.AuthRepositoryImpl;
import ru.mirea.korovkin.marvelverse.data.repository.RepositoryFactory;
import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;
import ru.mirea.korovkin.marvelverse.domain.models.User;
import ru.mirea.korovkin.marvelverse.domain.repository.AuthRepository;
import ru.mirea.korovkin.marvelverse.domain.usecases.LoginUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.RegisterUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.SaveClientInfoUseCase;

// Практика 6: бывшая AuthActivity, теперь фрагмент (Firebase Auth из практики 2).
// Логика та же: use-case'ы прямо тут, без ViewModel — в практике 3 переводили только главный экран.
// Отличие: после входа не finish(), а шаг назад по бэк-стеку — вернусь в профиль.
public class AuthFragment extends Fragment {

    private EditText emailInput;
    private EditText passwordInput;
    private TextView statusText;

    private LoginUseCase loginUseCase;
    private RegisterUseCase registerUseCase;
    private SaveClientInfoUseCase saveClientInfoUseCase;

    private Navigator navigator;

    // Activity = Navigator
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Navigator)) {
            throw new ClassCastException(context + " must implement Navigator");
        }
        navigator = (Navigator) context;
    }

    // use-case'ы собираю один раз на фрагмент
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthRepository authRepository = new AuthRepositoryImpl();
        loginUseCase = new LoginUseCase(authRepository);
        registerUseCase = new RegisterUseCase(authRepository);
        saveClientInfoUseCase = new SaveClientInfoUseCase(
                RepositoryFactory.createClientInfoRepository(requireContext())
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    // поля + кнопки
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        statusText = view.findViewById(R.id.statusText);

        view.findViewById(R.id.loginButton).setOnClickListener(v -> login());
        view.findViewById(R.id.registerButton).setOnClickListener(v -> register());
    }

    // вход в Firebase
    private void login() {
        loginUseCase.execute(email(), password(), new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                onAuthSuccess(user, "Добро пожаловать, ");
            }

            @Override
            public void onError(String message) {
                showError("Ошибка входа: " + message);
            }
        });
    }

    // регистрация в Firebase
    private void register() {
        registerUseCase.execute(email(), password(), new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                onAuthSuccess(user, "Аккаунт создан: ");
            }

            @Override
            public void onError(String message) {
                showError("Ошибка регистрации: " + message);
            }
        });
    }

    // общий успех: кэш в SharedPreferences, тост и назад в профиль
    private void onAuthSuccess(User user, String toastPrefix) {
        // ответ Firebase асинхронный — пока ждали, фрагмент могли уже закрыть
        if (!isAdded()) return;
        saveClientInfoUseCase.execute(new ClientInfo(user.getUid(), user.getEmail()));
        Toast.makeText(requireContext(), toastPrefix + user.getEmail(), Toast.LENGTH_SHORT).show();
        navigator.goBack();
    }

    private void showError(String text) {
        if (statusText != null) statusText.setText(text);
    }

    private String email() {
        return emailInput.getText().toString().trim();
    }

    private String password() {
        return passwordInput.getText().toString().trim();
    }

    // view умерла — ссылки на поля обнуляю
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        emailInput = null;
        passwordInput = null;
        statusText = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigator = null;
    }
}
