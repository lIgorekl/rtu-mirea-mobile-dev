package ru.mirea.korovkin.marvelverse.domain.repository;

import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;

// Контракт хранения данных клиента после входа (uid/email).
// Реализация (ClientInfoRepositoryImpl) держит их в SharedPreferences.
public interface ClientInfoRepository {
    void saveClientInfo(ClientInfo clientInfo);
    ClientInfo getClientInfo();
    void clearClientInfo();
}
