package com.mirea.korovkin.fragmentapp;

// интерфейс-посредник: фрагмент шлёт сюда, Activity реализует
// так фрагмент не знает, какая именно Activity его держит
public interface FragmentListener {
    void sendResult(String message);
}
