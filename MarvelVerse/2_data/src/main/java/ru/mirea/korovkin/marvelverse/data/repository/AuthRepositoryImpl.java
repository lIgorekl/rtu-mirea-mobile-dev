package ru.mirea.korovkin.marvelverse.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mirea.korovkin.marvelverse.domain.models.User;
import ru.mirea.korovkin.marvelverse.domain.repository.AuthRepository;

// Реализация AuthRepository поверх Firebase Auth — единственное место в проекте,
// где напрямую используются FirebaseAuth/FirebaseUser.
public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuth firebaseAuth;

    public AuthRepositoryImpl() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(mapToDomain(result.getUser())))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void register(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(mapToDomain(result.getUser())))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
    }

    @Override
    public User getCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        return firebaseUser != null ? mapToDomain(firebaseUser) : null;
    }

    // Firebase отдаёт свой FirebaseUser — превращаем его в доменного User,
    // чтобы остальной код про Firebase не знал.
    private User mapToDomain(FirebaseUser firebaseUser) {
        if (firebaseUser == null) {
            return null;
        }
        return new User(firebaseUser.getUid(), firebaseUser.getEmail());
    }
}
