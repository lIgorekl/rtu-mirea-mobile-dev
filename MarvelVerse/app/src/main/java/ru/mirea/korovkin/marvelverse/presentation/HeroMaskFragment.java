package ru.mirea.korovkin.marvelverse.presentation;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.OutputStream;

import ru.mirea.korovkin.marvelverse.R;

// Практика 8: вкладка "Маска" — экран 6 из ТЗ, use-case "Сделать фото с маской супергероя".
// Фото с камеры (системное приложение камеры) или из галереи → модель TFLite ищет лицо →
// поверх рисуется маска → можно сохранить в галерею. Гостю — заглушка и "Войти" (требование 6).
public class HeroMaskFragment extends Fragment {

    private static final String TAG = "MarvelVerseCheck";
    private static final String KEY_CAMERA_URI = "camera_uri";

    private HeroMaskViewModel vm;

    // куда камера запишет снимок (сохраняю при повороте — камера могла открыться до него)
    private Uri cameraUri;

    // лаунчеры результата — регистрировать можно только до старта фрагмента, поэтому в onCreate
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<PickVisualMediaRequest> pickPhotoLauncher;

    private View guestBlock;
    private View contentBlock;
    private ImageView photoView;
    private ProgressBar progress;
    private TextView statusText;
    private Button saveButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // своя ViewModel на фрагмент (общая MainViewModel тут не нужна)
        vm = new ViewModelProvider(this, new ViewModelFactory(requireContext()))
                .get(HeroMaskViewModel.class);

        if (savedInstanceState != null) {
            String saved = savedInstanceState.getString(KEY_CAMERA_URI);
            if (saved != null) cameraUri = Uri.parse(saved);
        }

        // камера: отдаю ей Uri файла, она туда пишет полноразмерное фото, мне — true/false
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            Log.d(TAG, "HeroMask camera result: " + success);
            if (Boolean.TRUE.equals(success) && cameraUri != null) {
                vm.processPhoto(appResolver(), cameraUri);
            }
        });
        // галерея: системный Photo Picker, разрешение на чтение файлов не нужно
        pickPhotoLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            Log.d(TAG, "HeroMask picked: " + uri);
            if (uri != null) {
                vm.processPhoto(appResolver(), uri);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hero_mask, container, false);
    }

    // кнопки, чипы масок, подписки
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        guestBlock = view.findViewById(R.id.guestBlock);
        contentBlock = view.findViewById(R.id.contentBlock);
        photoView = view.findViewById(R.id.photoView);
        progress = view.findViewById(R.id.progress);
        statusText = view.findViewById(R.id.statusText);
        saveButton = view.findViewById(R.id.saveButton);

        view.findViewById(R.id.cameraButton).setOnClickListener(v -> openCamera());
        view.findViewById(R.id.galleryButton).setOnClickListener(v -> pickPhotoLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));
        saveButton.setOnClickListener(v -> saveToGallery(vm.getResultPhoto().getValue()));
        // гость → экран входа, после входа вернусь сюда (popBackStack в AuthFragment)
        view.findViewById(R.id.loginButton).setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_heroMask_to_auth));

        setupMaskChips(view.findViewById(R.id.maskChips));

        vm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            guestBlock.setVisibility(user == null ? View.VISIBLE : View.GONE);
            contentBlock.setVisibility(user == null ? View.GONE : View.VISIBLE);
        });
        vm.getResultPhoto().observe(getViewLifecycleOwner(), bitmap -> {
            photoView.setImageBitmap(bitmap);
            saveButton.setEnabled(bitmap != null);
        });
        vm.getStatus().observe(getViewLifecycleOwner(), statusText::setText);
        vm.getLoading().observe(getViewLifecycleOwner(), isLoading ->
                progress.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE));
    }

    // чип на каждую маску из MaskStyle; выбор → ViewModel перерисует фото
    private void setupMaskChips(ChipGroup group) {
        MaskStyle current = vm.getMaskStyle().getValue();
        for (MaskStyle style : MaskStyle.values()) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_mask_chip, group, false);
            // id нужен ChipGroup, чтобы помнить, какой чип выбран
            chip.setId(View.generateViewId());
            chip.setText(style.getTitle());
            chip.setChecked(style == current);
            chip.setOnClickListener(v -> vm.setMaskStyle(style));
            group.addView(chip);
        }
    }

    // при каждом показе вкладки — проверяю, вошёл ли (мог выйти на вкладке "Профиль")
    @Override
    public void onResume() {
        super.onResume();
        vm.loadCurrentUser();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cameraUri != null) outState.putString(KEY_CAMERA_URI, cameraUri.toString());
    }

    // файл в кэше приложения + FileProvider даёт на него content:// Uri, который можно отдать камере
    private void openCamera() {
        File dir = new File(requireContext().getCacheDir(), "photos");
        if (!dir.exists() && !dir.mkdirs()) {
            Toast.makeText(requireContext(), "Не удалось создать папку для фото", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(dir, "hero_photo.jpg");
        cameraUri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".fileprovider", file);
        try {
            takePictureLauncher.launch(cameraUri);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "На устройстве нет приложения камеры", Toast.LENGTH_SHORT).show();
        }
    }

    // готовое фото → Pictures/MarvelVerse через MediaStore (с Android 10 без разрешений)
    private void saveToGallery(@Nullable Bitmap bitmap) {
        if (bitmap == null) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(requireContext(), "Сохранение доступно с Android 10", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentResolver resolver = appResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "marvelverse_mask_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MarvelVerse");
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(requireContext(), "Не удалось сохранить", Toast.LENGTH_SHORT).show();
            return;
        }
        try (OutputStream out = resolver.openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
            Log.d(TAG, "HeroMask saved: " + uri);
            Toast.makeText(requireContext(), "Сохранено в Pictures/MarvelVerse", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "HeroMask save failed", e);
            Toast.makeText(requireContext(), "Не удалось сохранить", Toast.LENGTH_SHORT).show();
        }
    }

    // resolver от applicationContext — его можно спокойно унести в фоновый поток ViewModel
    private ContentResolver appResolver() {
        return requireContext().getApplicationContext().getContentResolver();
    }

    // view умерла — ссылки отпускаю
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        guestBlock = null;
        contentBlock = null;
        photoView = null;
        progress = null;
        statusText = null;
        saveButton = null;
    }
}
