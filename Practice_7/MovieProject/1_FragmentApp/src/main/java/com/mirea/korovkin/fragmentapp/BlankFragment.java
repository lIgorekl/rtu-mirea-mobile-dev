package com.mirea.korovkin.fragmentapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// динамический фрагмент: кладётся транзакцией из MainActivity
public class BlankFragment extends Fragment {

    // ключ для номера в Bundle — тот же, что в MainActivity
    public static final String KEY_NUMBER = "my_number_student";

    private FragmentListener listener;

    // как только прицепились к Activity — запоминаем её как слушателя
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FragmentListener) context;
        } catch (ClassCastException e) {
            // если Activity забыла implements — сразу понятно, где косяк
            throw new ClassCastException(context + " must implement FragmentListener");
        }
    }

    // раздуваем разметку + вешаем клик по картинке
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(BlankFragment.class.getSimpleName(), "onCreateView");
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        ImageView image = view.findViewById(R.id.imageView);
        // клик -> через интерфейс в Activity, сам фрагмент её не трогает
        image.setOnClickListener(v -> listener.sendResult("image pushed"));
        return view;
    }

    // view уже есть — читаем номер и заполняем список дел
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // номер пришёл через Bundle (setArguments внутри add(..., bundle))
        int numberStudent = requireArguments().getInt(KEY_NUMBER);
        Log.d(BlankFragment.class.getSimpleName(), String.valueOf(numberStudent));
        ((TextView) view.findViewById(R.id.numberText))
                .setText("Мой номер по списку: " + numberStudent);

        // список дел со статусом, просто строки в ListView
        String[] todos = {
                "✔ Практика 1 — Clean Architecture",
                "✔ Практика 2 — data/domain, Firebase",
                "✔ Практика 3 — ViewModel, LiveData",
                "✔ Практика 4 — RecyclerView",
                "✔ Практика 5 — Retrofit",
                "✘ Практика 6 — Fragment (в процессе)",
                "✘ Практика 7 — Навигация"
        };
        ListView list = view.findViewById(R.id.todoList);
        list.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, todos));
    }

    // отцепились — ссылку на Activity отпускаем
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
