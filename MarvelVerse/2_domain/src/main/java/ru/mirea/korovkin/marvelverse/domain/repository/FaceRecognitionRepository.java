package ru.mirea.korovkin.marvelverse.domain.repository;

import java.util.List;

import ru.mirea.korovkin.marvelverse.domain.models.Face;

// Практика 8: контракт "найди лица на фото". Как именно ищем (TFLite, MediaPipe) — дело data-слоя.
// Картинку передаю массивом пикселей ARGB: Bitmap — это Android, а domain про Android не знает.
public interface FaceRecognitionRepository {

    // все лица на фото; ничего не нашли — пустой список (не null)
    List<Face> recognizeFaces(int[] argbPixels, int width, int height);

    // отпустить модель (она держит нативную память) — зову из onCleared() ViewModel
    void close();
}
