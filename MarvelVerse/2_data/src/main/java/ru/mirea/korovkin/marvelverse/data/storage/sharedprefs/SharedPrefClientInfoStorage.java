package ru.mirea.korovkin.marvelverse.data.storage.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

import ru.mirea.korovkin.marvelverse.data.storage.sharedprefs.models.ClientInfo;

// Реализация ClientInfoStorage поверх обычных SharedPreferences.
public class SharedPrefClientInfoStorage implements ClientInfoStorage {

    private static final String PREFS_NAME = "client_info_prefs";
    private static final String KEY_UID = "uid";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences prefs;

    public SharedPrefClientInfoStorage(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void save(ClientInfo clientInfo) {
        prefs.edit()
                .putString(KEY_UID, clientInfo.getUid())
                .putString(KEY_EMAIL, clientInfo.getEmail())
                .apply();
    }

    @Override
    public ClientInfo get() {
        String uid = prefs.getString(KEY_UID, null);
        String email = prefs.getString(KEY_EMAIL, null);
        if (uid == null) {
            // uid обязателен: если его нет, считаем, что данные ещё не сохраняли.
            return null;
        }
        return new ClientInfo(uid, email);
    }

    @Override
    public void clear() {
        prefs.edit().clear().apply();
    }
}
