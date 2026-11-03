package com.mirea.korovkin.resultapifragmentapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

// шторка снизу, принимает результат через Fragment Result API
public class BottomSheetFragment extends BottomSheetDialogFragment {

    private String receivedText = "—";
    private TextView receivedView;

    // слушатель вешаем на parentFragmentManager — это childFragmentManager у DataFragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener(ResultKeys.REQUEST_KEY, this,
                (requestKey, bundle) -> {
                    // результат лежал в FragmentManager и прилетает, когда мы STARTED
                    receivedText = bundle.getString(ResultKeys.KEY_TEXT);
                    Log.d(BottomSheetFragment.class.getSimpleName(), "Get text " + receivedText);
                    if (receivedView != null) receivedView.setText(receivedText);
                });
    }

    // раздуваем шторку, кнопка закрывает
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        receivedView = view.findViewById(R.id.receivedText);
        receivedView.setText(receivedText);
        view.findViewById(R.id.buttonClose).setOnClickListener(v -> dismiss());
        return view;
    }

    // view умерла — ссылку чистим
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        receivedView = null;
    }
}
