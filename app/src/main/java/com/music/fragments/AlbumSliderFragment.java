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

    private final AlbumRepository albumRepository = new AlbumRepository();

    private final List<Album> albums = new ArrayList<>();

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

    /**
     * Lấy thông tin các album trong bộ sưu tập và thêm vào {@link #albums}
     * Sau đó nó sẽ báo cho adapter rằng dữ liệu đã được thêm vào và phải render lại giao diện
     *
     * @param collection Bộ sưu tập chứa tên bộ sưu tập và các album
     */
    public void onSuccess(Collection collection) {
        for (DocumentReference albumReference : collection.getAlbums()) {
            albumReference.get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    onFailure(task.getException());
                    return;
                }

                albums.add(task.getResult().toObject(Album.class));

                if (binding.imageSlider.getAdapter() != null) {
                    binding.imageSlider.getAdapter().notifyDataSetChanged();
                }
            });
        }
    }

    public void onFailure(@NonNull Exception e) {
        Toast.makeText(getActivity(), "Không thể tải album bài hát.", Toast.LENGTH_SHORT).show();
    }
}