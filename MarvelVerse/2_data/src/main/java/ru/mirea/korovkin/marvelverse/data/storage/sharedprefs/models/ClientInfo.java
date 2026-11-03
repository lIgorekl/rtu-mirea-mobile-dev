package ru.mirea.korovkin.marvelverse.data.storage.sharedprefs.models;

// Storage-модель клиента (то, что реально лежит в SharedPreferences).
// Не путать с domain.models.ClientInfo — маппинг между ними в ClientInfoRepositoryImpl.
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
