package com.mirea.korovkin.navigationdrawerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mirea.korovkin.navigationdrawerapp.databinding.FragmentContactsBinding;

// раздел "Контакты": адрес и часы работы, тоже только разметка
public class ContactsFragment extends Fragment {

    private FragmentContactsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
