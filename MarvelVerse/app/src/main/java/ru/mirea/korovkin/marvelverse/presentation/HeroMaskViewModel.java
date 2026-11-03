package ru.mirea.korovkin.marvelverse.presentation;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.korovkin.marvelverse.domain.models.Face;
import ru.mirea.korovkin.marvelverse.domain.models.User;
import ru.mirea.korovkin.marvelverse.domain.repository.AuthRepository;
import ru.mirea.korovkin.marvelverse.domain.repository.FaceRecognitionRepository;
import ru.mirea.korovkin.marvelverse.domain.usecases.GetCurrentUserUseCase;
import ru.mirea.korovkin.marvelverse.domain.usecases.RecognizeFaceUseCase;

// Практика 8: ViewModel вкладки "Маска" (экран 6 из ТЗ). Своя, на фрагмент — как ProfileViewModel.
// Цепочка: фото (Uri) → Bitmap → RecognizeFaceUseCase (TFLite) → лица → MaskPainter → готовое фото.
// Тяжёлое (декодирование, модель, рисование) — в фоновом потоке, результат через postValue.
// Фото лежит в LiveData → переживает поворот экрана, модель второй раз не гоняю.
public class HeroMaskViewModel extends ViewModel {

    private static final String TAG = "MarvelVerseCheck";

    private final RecognizeFaceUseCase recognizeFaceUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    // держу, чтобы закрыть модель в onCleared()
    private final FaceRecognitionRepository faceRecognitionRepository;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // null = гость → вместо экрана заглушка (требование 6)
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    // фото уже с масками — его показываю и сохраняю
    private final MutableLiveData<Bitmap> resultPhoto = new MutableLiveData<>();
    // строка под фото: сколько лиц, уверенность, ошибки
    private final MutableLiveData<String> status = new MutableLiveData<>("Фото ещё не выбрано");
    // крутилка поверх фото, пока работает модель
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<MaskStyle> maskStyle = new MutableLiveData<>(MaskStyle.SCARLET);

    // последнее фото без маски и найденные на нём лица (трогаю только из executor)
    private Bitmap sourcePhoto;
    private List<Face> faces;

    public HeroMaskViewModel(FaceRecognitionRepository faceRecognitionRepository,
                             AuthRepository authRepository) {
        this.faceRecognitionRepository = faceRecognitionRepository;
        this.recognizeFaceUseCase = new RecognizeFaceUseCase(faceRecognitionRepository);
        this.getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Bitmap> getResultPhoto() {
        return resultPhoto;
    }

    public LiveData<String> getStatus() {
        return status;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<MaskStyle> getMaskStyle() {
        return maskStyle;
    }

    // Firebase хранит юзера локально → можно сразу setValue
    public void loadCurrentUser() {
        currentUser.setValue(getCurrentUserUseCase.execute());
    }

    // новое фото с камеры/из галереи: читаю → ищу лица → рисую маску
    public void processPhoto(ContentResolver resolver, Uri uri) {
        loading.setValue(true);
        status.setValue("Ищу лицо…");
        // стиль читаю тут, в главном потоке, и передаю внутрь
        MaskStyle style = maskStyle.getValue();
        executor.execute(() -> {
            try {
                Bitmap photo = PhotoDecoder.decode(resolver, uri);
                int w = photo.getWidth();
                int h = photo.getHeight();
                // Bitmap → массив пикселей: domain не знает про Bitmap
                int[] pixels = new int[w * h];
                photo.getPixels(pixels, 0, w, 0, 0, w, h);

                long start = System.currentTimeMillis();
                List<Face> found = recognizeFaceUseCase.execute(pixels, w, h);
                long time = System.currentTimeMillis() - start;
                Log.d(TAG, "HeroMask → photo " + w + "x" + h + ", faces: " + found.size() + ", " + time + " ms");

                sourcePhoto = photo;
                faces = found;
                resultPhoto.postValue(MaskPainter.draw(photo, found, style));
                status.postValue(buildStatus(found, time));
            } catch (Exception e) {
                // IOException от файла или ошибка модели (например, нет .tflite в assets)
                Log.e(TAG, "HeroMask failed", e);
                status.postValue("Ошибка: " + e.getMessage());
            } finally {
                loading.postValue(false);
            }
        });
    }

    // сменили маску: модель второй раз не гоняю, просто перерисовываю по старым лицам
    public void setMaskStyle(MaskStyle style) {
        if (style == maskStyle.getValue()) return;
        maskStyle.setValue(style);
        executor.execute(() -> {
            if (sourcePhoto != null && faces != null) {
                resultPhoto.postValue(MaskPainter.draw(sourcePhoto, faces, style));
            }
        });
    }

    // текст под фото
    private String buildStatus(List<Face> found, long timeMs) {
        if (found.isEmpty()) {
            return "Лицо не найдено. Нужно фото анфас, лицо покрупнее";
        }
        float best = 0f;
        for (Face face : found) {
            best = Math.max(best, face.getScore());
        }
        return String.format(Locale.getDefault(), "Найдено лиц: %d · уверенность %d%% · %d мс",
                found.size(), Math.round(best * 100), timeMs);
    }

    // ViewModel умирает насовсем — гашу поток и отпускаю модель
    @Override
    protected void onCleared() {
        executor.shutdownNow();
        faceRecognitionRepository.close();
        super.onCleared();
    }
}
