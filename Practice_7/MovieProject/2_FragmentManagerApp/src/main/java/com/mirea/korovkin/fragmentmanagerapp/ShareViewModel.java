package com.mirea.korovkin.fragmentmanagerapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// общая ViewModel на Activity: список пишет сюда, детали отсюда читают
// фрагменты друг про друга не знают вообще
public class ShareViewModel extends ViewModel {

    private final MutableLiveData<Country> selectedItem = new MutableLiveData<>();

    // список зовёт при клике
    public void selectItem(Country item) {
        selectedItem.setValue(item);
    }

    // детали и Activity подписываются
    public LiveData<Country> getSelectedItem() {
        return selectedItem;
    }
}
