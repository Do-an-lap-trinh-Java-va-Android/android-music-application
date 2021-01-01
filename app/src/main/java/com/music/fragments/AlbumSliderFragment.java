package com.music.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.music.adapters.AlbumSliderAdapter;
import com.music.databinding.FragmentImageSliderBinding;
import com.music.models.Collection;
import com.music.repositories.AlbumRepository;

public class AlbumSliderFragment extends Fragment {
    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private FragmentImageSliderBinding binding;

    @NonNull
    private final AlbumRepository albumRepository = new AlbumRepository();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albumRepository.getRecommendAlbums().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                onSuccess(task.getResult());
            } else {
                onFailure(task.getException());
            }

            binding.prbLoading.setVisibility(View.GONE);
        });
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImageSliderBinding.inflate(inflater, container, false);

        // Thêm hiệu ứng khi cuộn album
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(30));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        binding.imageSlider.setPageTransformer(compositePageTransformer);

        return binding.getRoot();
    }

    public void onSuccess(@NonNull Collection collection) {
        binding.imageSlider.setAdapter(new AlbumSliderAdapter(collection.getAlbums(), binding.imageSlider));
    }

    public void onFailure(@Nullable Exception e) {
        Toast.makeText(getActivity(), "Không thể tải album bài hát.", Toast.LENGTH_SHORT).show();
    }
}