package com.music.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.music.adapters.AlbumSliderAdapter;
import com.music.adapters.song.SongAdapter;
import com.music.adapters.song.SongItemDecoration;
import com.music.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        initAlbumSliderViewPager();
        initTopSongRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        homeViewModel.getTopSongList().observe(getViewLifecycleOwner(), songs -> {
            binding.rvRandomSong.setAdapter(new SongAdapter(songs));
        });

        homeViewModel.getAlbumSlider().observe(getViewLifecycleOwner(), albums -> {
            binding.imageSlider.setAdapter(new AlbumSliderAdapter(albums));

            if (!albums.isEmpty()) {
                binding.prbLoading.setVisibility(View.GONE);
            }
        });
    }

    private void initTopSongRecyclerView() {
        final LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvRandomSong.setHasFixedSize(true);
        binding.rvRandomSong.setLayoutManager(linearLayoutManager);
        binding.rvRandomSong.addItemDecoration(new SongItemDecoration());

        // Tải bảng xếp hạng bài hát
        homeViewModel.fetchTopSongs();
    }

    private void initAlbumSliderViewPager() {
        // Thêm hiệu ứng khi cuộn album
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(30));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        binding.imageSlider.setPageTransformer(compositePageTransformer);

        // Tải danh sách album để làm slider
        homeViewModel.fetchCollectionAlbumSlider();
    }
}