package ru.mirea.korovkin.marvelverse.presentation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.korovkin.marvelverse.R;
import ru.mirea.korovkin.marvelverse.data.repository.AuthRepositoryImpl;
import ru.mirea.korovkin.marvelverse.data.repository.RepositoryFactory;
import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;
import ru.mirea.korovkin.marvelverse.domain.models.User;
import ru.mirea.korovkin.marvelverse.domain.repository.AuthRepository;
import ru.mirea.korovkin.marvelverse.domain.usecases.LoginUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.RegisterUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.SaveClientInfoUseCase;

// Экран входа/регистрации через Firebase Auth (практика 2).
// Ещё не переведён на ViewModel/LiveData — в практике 3 это делали только для MainActivity,
// не забегаем вперёд, поэтому тут use-case'ы создаются и дёргаются прямо в Activity.
public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "MarvelVerseCheck";

    private EditText emailInput;
    private EditText passwordInput;
    private TextView statusText;

    private LoginUseCase loginUseCase;
    private RegisterUseCase registerUseCase;
    private SaveClientInfoUseCase saveClientInfoUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        AuthRepository authRepository = new AuthRepositoryImpl();
        loginUseCase = new LoginUseCase(authRepository);
        registerUseCase = new RegisterUseCase(authRepository);
        saveClientInfoUseCase = new SaveClientInfoUseCase(
                RepositoryFactory.createClientInfoRepository(this)
        );

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        statusText = findViewById(R.id.statusText);

        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> login());
        registerButton.setOnClickListener(v -> register());
    }

    private void login() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        loginUseCase.execute(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                // После успешного входа сразу кэшируем uid/email в SharedPreferences,
                // чтобы MainActivity могла их показать без повторного похода в Firebase.
                saveClientInfoUseCase.execute(new ClientInfo(user.getUid(), user.getEmail()));
                statusText.setText("Вход выполнен: " + user.getEmail());
                Toast.makeText(AuthActivity.this, "Добро пожаловать, " + user.getEmail(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                statusText.setText("Ошибка входа: " + message);
            }
        });
    }

    private void register() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        registerUseCase.execute(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                saveClientInfoUseCase.execute(new ClientInfo(user.getUid(), user.getEmail()));
                statusText.setText("Аккаунт создан: " + user.getEmail());
                Toast.makeText(AuthActivity.this, "Аккаунт создан: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                statusText.setText("Ошибка регистрации: " + message);
            }
        });
    }
}
