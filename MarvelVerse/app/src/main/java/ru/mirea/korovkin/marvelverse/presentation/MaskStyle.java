package ru.mirea.korovkin.marvelverse.presentation;

// Практика 8: какие маски можно выбрать. Маски свои (простая "домино"-маска на глаза),
// отличаются только цветом. fill — заливка, stroke — обводка.
public enum MaskStyle {
    SCARLET("Алая", 0xFFC62828, 0xFF5D0000),
    NIGHT("Ночная", 0xFF263238, 0xFF000000),
    GOLD("Золотая", 0xFFFFB300, 0xFF7A4F00),
    EMERALD("Изумрудная", 0xFF2E7D32, 0xFF0B3D0E);

    private final String title;
    private final int fillColor;
    private final int strokeColor;

    MaskStyle(String title, int fillColor, int strokeColor) {
        this.title = title;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
    }

    public String getTitle() {
        return title;
    }

    public int getFillColor() {
        return fillColor;
    }

    public int getStrokeColor() {
        return strokeColor;
    }
}
