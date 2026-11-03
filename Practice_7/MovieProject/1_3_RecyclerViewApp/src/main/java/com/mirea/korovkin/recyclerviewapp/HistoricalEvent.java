package com.mirea.korovkin.recyclerviewapp;

// Модель для слоя представления: одно историческое событие.
// imageName — имя drawable-ресурса, по нему адаптер ищет картинку (как flagName у стран в методичке).
public class HistoricalEvent {
    private final String title;
    private final int year;
    private final String description;
    private final String imageName;

    public HistoricalEvent(String title, int year, String description, String imageName) {
        this.title = title;
        this.year = year;
        this.description = description;
        this.imageName = imageName;
    }

    public String getTitle() { return title; }
    public int getYear() { return year; }
    public String getDescription() { return description; }
    public String getImageName() { return imageName; }

    @Override
    public String toString() {
        return year + " — " + title;
    }
}
