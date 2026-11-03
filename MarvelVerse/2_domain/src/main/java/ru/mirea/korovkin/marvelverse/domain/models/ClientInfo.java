package ru.mirea.korovkin.marvelverse.domain.models;

// Доменная модель данных клиента (uid + email). Не путать с data/storage/sharedprefs/models/ClientInfo —
// та описывает формат хранения в SharedPreferences, маппинг между ними делает ClientInfoRepositoryImpl.
public class ClientInfo {
    private final String uid;
    private final String email;

    public ClientInfo(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }
}
