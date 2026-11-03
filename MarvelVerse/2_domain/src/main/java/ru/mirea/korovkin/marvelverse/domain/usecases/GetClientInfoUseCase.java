package ru.mirea.korovkin.marvelverse.domain.usecases;

import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;
import ru.mirea.korovkin.marvelverse.domain.repository.ClientInfoRepository;

// Отдаёт сохранённые данные клиента (uid/email) из SharedPreferences, если уже входили.
public class GetClientInfoUseCase {
    private final ClientInfoRepository clientInfoRepository;

    public GetClientInfoUseCase(ClientInfoRepository clientInfoRepository) {
        this.clientInfoRepository = clientInfoRepository;
    }

    public ClientInfo execute() {
        return clientInfoRepository.getClientInfo();
    }
}
