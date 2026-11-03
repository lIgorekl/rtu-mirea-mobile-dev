package ru.mirea.korovkin.marvelverse.domain.usecases;

import java.util.List;

import ru.mirea.korovkin.marvelverse.domain.models.Face;
import ru.mirea.korovkin.marvelverse.domain.repository.FaceRecognitionRepository;

// Практика 8: use-case "Распознать лицо (TensorFlow Lite)" с диаграммы из практики 1
// (его «include»-ит "Сделать фото с маской супергероя"). Сам ничего не считает — просит репозиторий.
public class RecognizeFaceUseCase {

    private final FaceRecognitionRepository faceRecognitionRepository;

    public RecognizeFaceUseCase(FaceRecognitionRepository faceRecognitionRepository) {
        this.faceRecognitionRepository = faceRecognitionRepository;
    }

    // пустая картинка — даже не дёргаю модель
    public List<Face> execute(int[] argbPixels, int width, int height) {
        if (argbPixels == null || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Пустое фото");
        }
        return faceRecognitionRepository.recognizeFaces(argbPixels, width, height);
    }
}
