package ru.mirea.korovkin.marvelverse.presentation;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;
import ru.mirea.korovkin.marvelverse.domain.models.User;
import ru.mirea.korovkin.marvelverse.domain.repository.AuthRepository;
import ru.mirea.korovkin.marvelverse.domain.repository.ClientInfoRepository;
import ru.mirea.korovkin.marvelverse.domain.usecases.GetClientInfoUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.GetCurrentUserUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.LogoutUseCase;

// Практика 6: своя ViewModel у профиля. Данные авторизации из двух мест:
// Firebase Auth (кто залогинен прямо сейчас) и SharedPreferences (что сохранили при входе).
// use-case'ы уже были с практики 2, тут они наконец пригодились.
public class ProfileViewModel extends ViewModel {

    private static final String TAG = "MarvelVerseCheck";

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final GetClientInfoUseCase getClientInfoUseCase;
    private final LogoutUseCase logoutUseCase;

    // null = гость
    private final MutableLiveData<User> user = new MutableLiveData<>();
    // кэш из SharedPreferences, null = ещё ни разу не входили
    private final MutableLiveData<ClientInfo> cachedInfo = new MutableLiveData<>();

    // зависимости кидает ViewModelFactory
    public ProfileViewModel(AuthRepository authRepository, ClientInfoRepository clientInfoRepository) {
        this.getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
        this.getClientInfoUseCase = new GetClientInfoUseCase(clientInfoRepository);
        this.logoutUseCase = new LogoutUseCase(authRepository);
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<ClientInfo> getCachedInfo() {
        return cachedInfo;
    }

    // оба источника быстрые и без сети (Firebase держит юзера локально) — setValue прямо тут
    public void loadProfile() {
        User current = getCurrentUserUseCase.execute();
        Log.d(TAG, "Profile: " + (current != null ? current.getEmail() : "guest"));
        user.setValue(current);
        cachedInfo.setValue(getClientInfoUseCase.execute());
    }

    // выход из Firebase и сразу перечитываю — экран сам станет "гостевым"
    public void logout() {
        logoutUseCase.execute();
        loadProfile();
    }
}
