package ru.mirea.korovkin.marvelverse.data.ml;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector;
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult;

import java.io.IOException;
import java.io.InputStream;

// Практика 8: обёртка над обученной моделью TensorFlow Lite BlazeFace (short-range).
// Сам .tflite лежит в 2_data/src/main/assets (при сборке assets модуля сливаются в apk).
// Запускаю его через MediaPipe Tasks (FaceDetector): он сам ужимает фото до 128x128,
// гоняет сеть, декодирует якоря и убирает дубли (NMS) — на выходе рамки + 6 точек лица.
// Аналог NetworkApi из практики 5: наружу из data торчит только репозиторий.
public class BlazeFaceDetector {

    // имя файла в assets — должно совпадать один в один
    public static final String MODEL_FILE = "blaze_face_short_range.tflite";

    // ниже 0.5 считаю "не лицо" (это и дефолт MediaPipe)
    private static final float MIN_CONFIDENCE = 0.5f;

    private final Context context;
    // создаю лениво: загрузка модели не бесплатная, а вкладку могут и не открыть
    private FaceDetector detector;

    public BlazeFaceDetector(Context context) {
        this.context = context.getApplicationContext();
    }

    // synchronized — на случай двух вызовов из разных потоков, детектор один
    public synchronized FaceDetectorResult detect(Bitmap bitmap) {
        if (detector == null) {
            detector = createDetector();
        }
        // MediaPipe работает со своим типом картинки MPImage, Bitmap оборачиваю
        MPImage image = new BitmapImageBuilder(bitmap).build();
        // режим IMAGE: одна картинка → сразу результат (без колбэков, как у видеопотока)
        return detector.detect(image);
    }

    // настройка модели
    private FaceDetector createDetector() {
        checkModelExists();
        BaseOptions baseOptions = BaseOptions.builder()
                .setModelAssetPath(MODEL_FILE)
                .build();
        FaceDetector.FaceDetectorOptions options = FaceDetector.FaceDetectorOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setMinDetectionConfidence(MIN_CONFIDENCE)
                .build();
        return FaceDetector.createFromOptions(context, options);
    }

    // забыл положить файл в assets — нормальный текст ошибки вместо нативного падения
    private void checkModelExists() {
        try (InputStream ignored = context.getAssets().open(MODEL_FILE)) {
            // файл есть — ок
        } catch (IOException e) {
            throw new IllegalStateException("Нет модели " + MODEL_FILE + " в 2_data/src/main/assets");
        }
    }

    // освобождаю нативную память модели
    public synchronized void close() {
        if (detector != null) {
            detector.close();
            detector = null;
        }
    }
}
