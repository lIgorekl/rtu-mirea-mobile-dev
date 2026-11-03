package ru.mirea.korovkin.marvelverse.data.network;

// Моя ошибка сети. Unchecked, чтобы не трогать сигнатуры в domain (MovieRepository).
// В message сразу человеческий текст — его ViewModel и покажет на экране.
public class NetworkException extends RuntimeException {
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkException(String message) {
        super(message);
    }
}
