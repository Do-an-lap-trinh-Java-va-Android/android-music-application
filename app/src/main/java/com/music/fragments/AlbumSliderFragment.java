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
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.music.R;
import com.music.adapters.AlbumSliderAdapter;
import com.music.models.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumSliderFragment extends Fragment {
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseFirestore mFirestore;

    @SuppressWarnings("FieldCanBeLocal")
    private CollectionReference mCollection;

    private ViewPager2 mViewPager;

    private final List<Album> mAlbums = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        mCollection = mFirestore.collection("albums");

        mCollection.whereEqualTo("recommend", true).get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot document : query.getDocuments()) {
                        mAlbums.add(document.toObject(Album.class));
                    }

                    if (mViewPager.getAdapter() != null) {
                        mViewPager.getAdapter().notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Không thể tải album bài hát.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_slider, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewPager = view.findViewById(R.id.image_slider);

        mViewPager.setAdapter(new AlbumSliderAdapter(mAlbums, mViewPager));

        // Thêm hiệu ứng chuyển cảnh khi cuộn hình ảnh
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(30));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        mViewPager.setPageTransformer(compositePageTransformer);
    }
}