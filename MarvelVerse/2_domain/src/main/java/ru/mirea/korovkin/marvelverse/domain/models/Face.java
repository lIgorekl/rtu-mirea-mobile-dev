package ru.mirea.korovkin.marvelverse.domain.models;

// Практика 8: одно найденное лицо на фото. Всё в пикселях исходной картинки.
// Чистая Java, без Android (RectF/PointF сюда нельзя — domain это java-library).
// Рамка лица + две точки глаз — по глазам потом ставлю и поворачиваю маску.
public class Face {

    // рамка лица
    private final float left;
    private final float top;
    private final float right;
    private final float bottom;

    // глаза (порядок как отдала модель, painter сам разберётся, кто левее)
    private final float leftEyeX;
    private final float leftEyeY;
    private final float rightEyeX;
    private final float rightEyeY;

    // уверенность модели 0..1
    private final float score;

    public Face(float left, float top, float right, float bottom,
                float leftEyeX, float leftEyeY, float rightEyeX, float rightEyeY,
                float score) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.leftEyeX = leftEyeX;
        this.leftEyeY = leftEyeY;
        this.rightEyeX = rightEyeX;
        this.rightEyeY = rightEyeY;
        this.score = score;
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }

    public float getLeftEyeX() {
        return leftEyeX;
    }

    public float getLeftEyeY() {
        return leftEyeY;
    }

    public float getRightEyeX() {
        return rightEyeX;
    }

    public float getRightEyeY() {
        return rightEyeY;
    }

    public float getScore() {
        return score;
    }
}
