package ru.mirea.korovkin.marvelverse.data.storage.sharedprefs;

import ru.mirea.korovkin.marvelverse.data.storage.sharedprefs.models.ClientInfo;

// Контракт хранения данных клиента внутри data-слоя (реализация — SharedPrefClientInfoStorage).
// Используется только ClientInfoRepositoryImpl, наружу из data не выходит.
public interface ClientInfoStorage {
    void save(ClientInfo clientInfo);
    ClientInfo get();
    void clear();
}
