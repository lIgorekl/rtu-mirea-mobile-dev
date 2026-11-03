package ru.mirea.korovkin.marvelverse.presentation;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

// Практика 8: Uri фото (камера или галерея) → Bitmap, который можно отдать модели.
// Две засады: 1) фото с камеры огромные (12+ Мп) — уменьшаю, модели всё равно нужно 128x128;
// 2) камера часто пишет картинку "боком", а поворот хранит в EXIF — разворачиваю сама.
public final class PhotoDecoder {

    // длинная сторона после уменьшения — с запасом, чтобы маска на сохранённом фото была чёткой
    private static final int MAX_SIDE = 1600;

    private PhotoDecoder() {
    }

    // зовётся в фоновом потоке (чтение файла + декодирование — долго)
    public static Bitmap decode(ContentResolver resolver, Uri uri) throws IOException {
        // 1. только размеры, без пикселей
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream in = resolver.openInputStream(uri)) {
            BitmapFactory.decodeStream(in, null, bounds);
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw new IOException("Не удалось прочитать фото");
        }

        // 2. inSampleSize — во сколько раз ужать при чтении (степень двойки)
        int sample = 1;
        while (Math.max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_SIDE) {
            sample *= 2;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sample;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap;
        try (InputStream in = resolver.openInputStream(uri)) {
            bitmap = BitmapFactory.decodeStream(in, null, options);
        }
        if (bitmap == null) {
            throw new IOException("Не удалось прочитать фото");
        }

        // 3. поворот из EXIF
        int rotation = 0;
        try (InputStream in = resolver.openInputStream(uri)) {
            if (in != null) {
                rotation = new ExifInterface(in).getRotationDegrees();
            }
        }
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }
}
