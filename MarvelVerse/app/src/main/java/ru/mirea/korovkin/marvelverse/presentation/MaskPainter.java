package ru.mirea.korovkin.marvelverse.presentation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.List;

import ru.mirea.korovkin.marvelverse.domain.models.Face;

// Практика 8: рисует маску поверх фото по тому, что нашла модель.
// Модель знает "где глаза", а эта штука — "как выглядит маска". Чистый UI → живёт в presentation.
// Идея: переношу начало координат в середину между глазами, поворачиваю холст на наклон головы
// и рисую маску всегда "ровно" — Canvas сам её повернёт и сдвинет.
public final class MaskPainter {

    private MaskPainter() {
    }

    // новое фото = копия исходного + маски на всех лицах (исходник не трогаю — смена цвета без пересчёта)
    public static Bitmap draw(Bitmap source, List<Face> faces, MaskStyle style) {
        Bitmap result = source.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        for (Face face : faces) {
            drawOnFace(canvas, face, style);
        }
        return result;
    }

    // одна маска на одно лицо
    private static void drawOnFace(Canvas canvas, Face face, MaskStyle style) {
        // кто из глаз левее на фото — тот "первый", чтобы угол не перевернулся на 180°
        float x1 = face.getLeftEyeX(), y1 = face.getLeftEyeY();
        float x2 = face.getRightEyeX(), y2 = face.getRightEyeY();
        if (x1 > x2) {
            float tx = x1, ty = y1;
            x1 = x2; y1 = y2;
            x2 = tx; y2 = ty;
        }

        // всё меряю в "расстояниях между глазами" — маска сама подстраивается под размер лица
        float eyeDistance = (float) Math.hypot(x2 - x1, y2 - y1);
        if (eyeDistance < 1f) return;
        // наклон головы в градусах
        float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));

        canvas.save();
        canvas.translate((x1 + x2) / 2f, (y1 + y2) / 2f);
        canvas.rotate(angle);
        drawDominoMask(canvas, eyeDistance, style);
        canvas.restore();
    }

    // сама маска в "своих" координатах: (0,0) — переносица, глаза в (-d/2, 0) и (d/2, 0)
    private static void drawDominoMask(Canvas canvas, float d, MaskStyle style) {
        float w = d * 1.15f; // половина ширины (чуть шире висков)
        float h = d * 0.45f; // половина высоты

        Path mask = new Path();
        mask.moveTo(-w, -h * 0.3f);                                                   // левый край
        mask.cubicTo(-w * 0.7f, -h * 1.3f, -w * 0.15f, -h * 1.1f, 0, -h * 0.55f);     // верх левой половины
        mask.cubicTo(w * 0.15f, -h * 1.1f, w * 0.7f, -h * 1.3f, w, -h * 0.3f);        // верх правой
        mask.cubicTo(w * 0.9f, h * 0.9f, w * 0.3f, h * 1.2f, w * 0.08f, h * 0.45f);   // низ правой
        mask.quadTo(0, h * 0.2f, -w * 0.08f, h * 0.45f);                              // выемка под нос
        mask.cubicTo(-w * 0.3f, h * 1.2f, -w * 0.9f, h * 0.9f, -w, -h * 0.3f);        // низ левой
        mask.close();

        // прорези для глаз. EVEN_ODD: где фигуры перекрываются — дырка
        float eyeW = d * 0.22f;
        float eyeH = d * 0.12f;
        mask.addOval(new RectF(-d / 2f - eyeW, -eyeH, -d / 2f + eyeW, eyeH), Path.Direction.CW);
        mask.addOval(new RectF(d / 2f - eyeW, -eyeH, d / 2f + eyeW, eyeH), Path.Direction.CW);
        mask.setFillType(Path.FillType.EVEN_ODD);

        // заливка
        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(style.getFillColor());
        canvas.drawPath(mask, fill);

        // обводка — толщина тоже от размера лица
        Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(Math.max(2f, d * 0.04f));
        stroke.setColor(style.getStrokeColor());
        canvas.drawPath(mask, stroke);
    }
}
