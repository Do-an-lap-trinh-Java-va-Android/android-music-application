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

import com.google.firebase.firestore.DocumentReference;
import com.music.adapters.AlbumSliderAdapter;
import com.music.databinding.FragmentImageSliderBinding;
import com.music.models.Album;
import com.music.models.Collection;
import com.music.repositories.AlbumRepository;

import java.util.ArrayList;
import java.util.List;

public class AlbumSliderFragment extends Fragment {
    private FragmentImageSliderBinding binding;

    private final AlbumRepository albumRepository;

    private final List<Album> albums = new ArrayList<>();

    public AlbumSliderFragment() {
        albumRepository = new AlbumRepository();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albumRepository.getRecommendAlbums()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Collection> collections = task.getResult().toObjects(Collection.class);

                        for (Collection collection : collections) {
                            for (DocumentReference album : collection.getAlbums()) {
                                album.get().addOnSuccessListener(documentSnapshot -> {
                                    albums.add(documentSnapshot.toObject(Album.class));

                                    if (binding.imageSlider.getAdapter() != null) {
                                        binding.imageSlider.getAdapter().notifyDataSetChanged();
                                    }
                                });
                            }
                        }
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
        binding.imageSlider.setAdapter(new AlbumSliderAdapter(albums, binding.imageSlider));

        return binding.getRoot();
    }
}