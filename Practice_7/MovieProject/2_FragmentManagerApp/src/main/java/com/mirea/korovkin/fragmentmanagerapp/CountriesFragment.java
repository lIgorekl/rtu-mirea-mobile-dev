package com.mirea.korovkin.fragmentmanagerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.List;

// первый фрагмент — список стран
public class CountriesFragment extends Fragment {

    private static final String ARG_TITLE = "title";

    private ShareViewModel viewModel;

    // фабричный метод как HeaderFragment.newInstance("MIREA") — заголовок через arguments
    public static CountriesFragment newInstance(String title) {
        CountriesFragment fragment = new CountriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    // просто раздуваем разметку
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countries, container, false);
    }

    // заполняем список, клик -> в общую ViewModel
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // requireActivity() — значит та же ViewModel, что у Activity и DetailsFragment
        viewModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);

        ((TextView) view.findViewById(R.id.titleText))
                .setText(requireArguments().getString(ARG_TITLE));

        List<Country> countries = getCountries();
        ListView list = view.findViewById(R.id.countryList);
        // activated_1 — чтобы выбранная строка подсвечивалась
        list.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_activated_1, countries));
        list.setOnItemClickListener((parent, v, position, id) ->
                viewModel.selectItem(countries.get(position)));
    }

    // данные захардкожены, сеть тут не нужна
    private List<Country> getCountries() {
        return Arrays.asList(
                new Country("🇷🇺", "Россия", "Москва", "~146 млн",
                        "Самая большая страна в мире по площади, 11 часовых поясов."),
                new Country("🇨🇳", "Китай", "Пекин", "~1,41 млрд",
                        "Великая Китайская стена тянется больше чем на 20 тыс. км."),
                new Country("🇯🇵", "Япония", "Токио", "~124 млн",
                        "Почти 7 тысяч островов, родина синкансэна."),
                new Country("🇧🇷", "Бразилия", "Бразилиа", "~212 млн",
                        "Большая часть лесов Амазонии и пятикратный чемпион мира по футболу."),
                new Country("🇪🇬", "Египет", "Каир", "~107 млн",
                        "Пирамиды Гизы и река Нил — одна из самых длинных в мире."),
                new Country("🇮🇳", "Индия", "Нью-Дели", "~1,44 млрд",
                        "Больше 20 официальных языков, родина шахмат."),
                new Country("🇮🇹", "Италия", "Рим", "~59 млн",
                        "Колизей, Венеция, пицца и паста."),
                new Country("🇨🇦", "Канада", "Оттава", "~41 млн",
                        "Вторая по площади страна, самая длинная береговая линия.")
        );
    }
}
