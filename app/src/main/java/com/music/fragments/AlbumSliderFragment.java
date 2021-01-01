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

import com.google.firebase.firestore.QuerySnapshot;
import com.music.adapters.AlbumSliderAdapter;
import com.music.databinding.FragmentImageSliderBinding;
import com.music.models.Album;
import com.music.repositories.AlbumRepository;

import java.util.List;
import java.util.stream.Collectors;

public class AlbumSliderFragment extends Fragment {
    private FragmentImageSliderBinding binding;

    private final AlbumRepository albumRepository;

    public AlbumSliderFragment() {
        albumRepository = new AlbumRepository();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albumRepository.getRecommendAlbums()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot query = task.getResult();

                        List<Album> albums = query.getDocuments().stream()
                                .map(document -> document.toObject(Album.class))
                                .collect(Collectors.toList());

                        binding.imageSlider.setAdapter(new AlbumSliderAdapter(albums, binding.imageSlider));
                    } else {
                        Toast.makeText(getActivity(), "Không thể tải album bài hát.", Toast.LENGTH_SHORT).show();
                    }

                    binding.prbLoading.setVisibility(View.GONE);
                });
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
}