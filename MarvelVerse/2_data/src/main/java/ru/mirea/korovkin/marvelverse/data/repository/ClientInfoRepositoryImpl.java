package ru.mirea.korovkin.marvelverse.data.repository;

import ru.mirea.korovkin.marvelverse.data.storage.sharedprefs.ClientInfoStorage;
import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;
import ru.mirea.korovkin.marvelverse.domain.repository.ClientInfoRepository;

// Реализация ClientInfoRepository поверх SharedPreferences (через ClientInfoStorage).
// Тут два разных класса ClientInfo — доменный (domain.models) и storage-шный
// (data.storage.sharedprefs.models) — эта прослойка маппит их друг в друга.
public class ClientInfoRepositoryImpl implements ClientInfoRepository {

    private final ClientInfoStorage clientInfoStorage;

    public ClientInfoRepositoryImpl(ClientInfoStorage clientInfoStorage) {
        this.clientInfoStorage = clientInfoStorage;
    }

    @Override
    public void saveClientInfo(ClientInfo clientInfo) {
        clientInfoStorage.save(mapToStorage(clientInfo));
    }

    @Override
    public ClientInfo getClientInfo() {
        ru.mirea.korovkin.marvelverse.data.storage.sharedprefs.models.ClientInfo stored = clientInfoStorage.get();
        return stored != null ? mapToDomain(stored) : null;
    }

    @Override
    public void clearClientInfo() {
        clientInfoStorage.clear();
    }

    private ru.mirea.korovkin.marvelverse.data.storage.sharedprefs.models.ClientInfo mapToStorage(ClientInfo clientInfo) {
        return new ru.mirea.korovkin.marvelverse.data.storage.sharedprefs.models.ClientInfo(clientInfo.getUid(), clientInfo.getEmail());
    }

    private ClientInfo mapToDomain(ru.mirea.korovkin.marvelverse.data.storage.sharedprefs.models.ClientInfo stored) {
        return new ClientInfo(stored.getUid(), stored.getEmail());
    }
}
