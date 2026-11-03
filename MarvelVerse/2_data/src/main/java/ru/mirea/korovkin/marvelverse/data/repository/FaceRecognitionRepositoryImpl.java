package ru.mirea.korovkin.marvelverse.data.repository;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.google.mediapipe.tasks.components.containers.Detection;
import com.google.mediapipe.tasks.components.containers.NormalizedKeypoint;
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.korovkin.marvelverse.data.ml.BlazeFaceDetector;
import ru.mirea.korovkin.marvelverse.domain.models.Face;
import ru.mirea.korovkin.marvelverse.domain.repository.FaceRecognitionRepository;

// Практика 8: реализация доменного FaceRecognitionRepository через BlazeFace.
// Работа репозитория — перевести "язык MediaPipe" (Detection, нормированные точки)
// в доменный Face в пикселях. Как раньше MovieDto → Movie.
public class FaceRecognitionRepositoryImpl implements FaceRecognitionRepository {

    // в выдаче BlazeFace точки идут по порядку: 0,1 — глаза, 2 — нос, 3 — рот, 4,5 — уши
    private static final int EYE_1 = 0;
    private static final int EYE_2 = 1;

    private final BlazeFaceDetector detector;

    public FaceRecognitionRepositoryImpl(BlazeFaceDetector detector) {
        this.detector = detector;
    }

    @Override
    public List<Face> recognizeFaces(int[] argbPixels, int width, int height) {
        // пиксели из domain обратно в Bitmap — модель ест именно его
        Bitmap bitmap = Bitmap.createBitmap(argbPixels, width, height, Bitmap.Config.ARGB_8888);
        FaceDetectorResult result = detector.detect(bitmap);

        List<Face> faces = new ArrayList<>();
        for (Detection detection : result.detections()) {
            faces.add(toFace(detection, width, height));
        }
        return faces;
    }

    // один Detection → Face. Рамка уже в пикселях, а точки в долях 0..1 → умножаю на размер
    private Face toFace(Detection detection, int width, int height) {
        RectF box = detection.boundingBox();
        float score = detection.categories().isEmpty() ? 0f : detection.categories().get(0).score();

        // если вдруг точек нет — прикидываю глаза по рамке (примерно на 40% высоты лица)
        float eye1X = box.left + box.width() * 0.3f;
        float eye2X = box.left + box.width() * 0.7f;
        float eye1Y = box.top + box.height() * 0.4f;
        float eye2Y = eye1Y;

        if (detection.keypoints().isPresent()) {
            List<NormalizedKeypoint> points = detection.keypoints().get();
            if (points.size() > EYE_2) {
                eye1X = points.get(EYE_1).x() * width;
                eye1Y = points.get(EYE_1).y() * height;
                eye2X = points.get(EYE_2).x() * width;
                eye2Y = points.get(EYE_2).y() * height;
            }
        }
        return new Face(box.left, box.top, box.right, box.bottom,
                eye1X, eye1Y, eye2X, eye2Y, score);
    }

    @Override
    public void close() {
        detector.close();
    }
}
