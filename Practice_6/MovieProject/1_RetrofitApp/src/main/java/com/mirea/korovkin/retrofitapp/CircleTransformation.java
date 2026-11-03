package com.mirea.korovkin.retrofitapp;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

// Своя трансформация для Picasso (.transform(...)) — вырезает из картинки круг.
public class CircleTransformation implements Transformation {

    // Picasso отдаёт сюда уже скачанный bitmap, я возвращаю новый — круглый.
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        // берём квадрат из середины, иначе круг выйдет овалом
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // рисую круг, "залитый" самой картинкой
        paint.setShader(new BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        // старые bitmap'ы больше не нужны — Picasso требует, чтобы source я освободил сам
        if (squared != source) {
            squared.recycle();
        }
        source.recycle();
        return result;
    }

    // ключ для кэша Picasso: круглая версия кэшируется отдельно от обычной
    @Override
    public String key() {
        return "circle";
    }
}
