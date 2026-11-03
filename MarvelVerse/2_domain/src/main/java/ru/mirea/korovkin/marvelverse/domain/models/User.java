package ru.mirea.korovkin.marvelverse.domain.models;

// Доменная модель авторизованного пользователя. Возвращается AuthRepository
// после входа/регистрации через Firebase Auth.
public class User {
    private final String uid;
    private final String email;

    public User(String uid, String email) {
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
