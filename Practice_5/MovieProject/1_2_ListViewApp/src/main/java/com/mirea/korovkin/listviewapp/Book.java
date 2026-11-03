package com.mirea.korovkin.listviewapp;

// Одна книга в списке: автор + название.
public class Book {
    private final String author;
    private final String title;

    public Book(String author, String title) {
        this.author = author;
        this.title = title;
    }

    public String getAuthor() { return author; }
    public String getTitle() { return title; }

    // ArrayAdapter по умолчанию зовёт toString() — на случай простого адаптера без getView
    @Override
    public String toString() {
        return author + " — " + title;
    }
}
