package ru.mirea.korovkin.marvelverse.domain.usecases;

import ru.mirea.korovkin.marvelverse.domain.models.ClientInfo;
import ru.mirea.korovkin.marvelverse.domain.repository.ClientInfoRepository;

// Сохраняет данные клиента (uid/email) в SharedPreferences — вызывается после успешного входа/регистрации.
public class SaveClientInfoUseCase {
    private final ClientInfoRepository clientInfoRepository;

    public SaveClientInfoUseCase(ClientInfoRepository clientInfoRepository) {
        this.clientInfoRepository = clientInfoRepository;
    }

    public void execute(ClientInfo clientInfo) {
        clientInfoRepository.saveClientInfo(clientInfo);
    }
}
