package com.mirea.korovkin.resultapifragmentapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// фрагмент с вводом — отправляет текст в шторку и в Activity
public class DataFragment extends Fragment {

    // разметка + клик по кнопке
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        Button button = view.findViewById(R.id.buttonOpenBottomSheet);
        EditText editText = view.findViewById(R.id.editTextInfo);

        button.setOnClickListener(click -> {
            String text = editText.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString(ResultKeys.KEY_TEXT, text);

            // 1) для шторки — через СВОЙ child FM (шторка будет его ребёнком)
            getChildFragmentManager().setFragmentResult(ResultKeys.REQUEST_KEY, bundle);

            // 2) для Activity — через parent FM (это supportFragmentManager Activity)
            getParentFragmentManager().setFragmentResult(ResultKeys.REQUEST_KEY, bundle);

            // шторка ещё не создана, результат подождёт её в FragmentManager
            BottomSheetFragment bottomSheet = new BottomSheetFragment();
            bottomSheet.show(getChildFragmentManager(), "ModalBottomSheet");
        });
        return view;
    }
}
