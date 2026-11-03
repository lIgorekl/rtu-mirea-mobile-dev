package ru.mirea.korovkin.marvelverse.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import ru.mirea.korovkin.marvelverse.R;
import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;
import ru.mirea.korovkin.marvelverse.domain.models.User;

// Практика 6: экран "Профиль", собирается из данных авторизации.
// Гость — "?" и кнопка входа, залогиненный — email/uid и кнопка выхода.
// Практика 7: теперь это вкладка нижнего меню (верхний экран графа) — кнопку "Назад" убрал,
// на вход иду по action_profile_to_auth через NavController.
public class ProfileFragment extends Fragment {

    private ProfileViewModel vm;

    // ViewModel тут своя (scope = this), профиль ни с кем данные не делит
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(this, new ViewModelFactory(requireContext()))
                .get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // подписки + кнопки
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView avatarText = view.findViewById(R.id.avatarText);
        TextView roleText = view.findViewById(R.id.roleText);
        TextView emailText = view.findViewById(R.id.emailText);
        TextView uidText = view.findViewById(R.id.uidText);
        TextView cachedText = view.findViewById(R.id.cachedText);
        Button loginButton = view.findViewById(R.id.loginButton);
        Button logoutButton = view.findViewById(R.id.logoutButton);

        // главное: от того, есть ли юзер в Firebase, меняется весь экран
        vm.getUser().observe(getViewLifecycleOwner(), (User user) -> {
            boolean loggedIn = user != null;
            if (loggedIn) {
                String email = user.getEmail() != null ? user.getEmail() : "";
                avatarText.setText(email.isEmpty() ? "?" : email.substring(0, 1).toUpperCase());
                roleText.setText("Авторизованный пользователь");
                emailText.setText("Email: " + email);
                uidText.setText("UID: " + user.getUid());
            } else {
                avatarText.setText("?");
                roleText.setText("Гость");
                emailText.setText("Вы не вошли в аккаунт. Гостю доступен только каталог.");
                uidText.setText("");
            }
            loginButton.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
            logoutButton.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        });

        // кэш из SharedPreferences — показываю отдельной строкой, он переживает даже выход
        vm.getCachedInfo().observe(getViewLifecycleOwner(), (ClientInfo info) ->
                cachedText.setText("Последний вход (SharedPreferences): "
                        + (info != null ? info.getEmail() : "нет данных")));

        // вход — отдельный экран графа, после него popBackStack вернёт сюда
        loginButton.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_profile_to_auth));
        logoutButton.setOnClickListener(v -> vm.logout());
    }

    // каждый раз при показе перечитываю — после возврата со входа экран обновится сам
    @Override
    public void onResume() {
        super.onResume();
        vm.loadProfile();
    }
}
