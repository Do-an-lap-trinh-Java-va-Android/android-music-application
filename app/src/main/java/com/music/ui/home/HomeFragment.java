package com.music.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private FragmentHomeBinding binding;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        initAlbumSliderViewPager();
        initTopSongRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        fetchData();

        homeViewModel.getTopSongList().observe(getViewLifecycleOwner(), request -> {
            switch (request.status) {
                case SUCCESS:
                    binding.rvRandomSong.setAdapter(new SongAdapter(request.data));
                    binding.prbRandomSongLoading.setVisibility(View.GONE);
                    break;
                case ERROR:
                    binding.prbRandomSongLoading.setVisibility(View.GONE);
                    break;
                case LOADING:
                    binding.prbRandomSongLoading.setVisibility(View.VISIBLE);
                    break;
            }
        });

        homeViewModel.getAlbumSlider().observe(getViewLifecycleOwner(), albums -> {
            switch (albums.status) {
                case SUCCESS:
                    binding.imageSlider.setAdapter(new AlbumSliderAdapter(albums.data));
                    binding.prbAlbumSliderLoading.setVisibility(View.GONE);
                    break;
                case LOADING:
                    binding.prbAlbumSliderLoading.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    Toast.makeText(getActivity(), albums.message, Toast.LENGTH_SHORT).show();
                    binding.prbAlbumSliderLoading.setVisibility(View.GONE);
                    break;
            }
        });
    }

    /**
     * Cấu hình RecyclerView của bảng xếp hạng
     */
    private void initTopSongRecyclerView() {
        final LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvRandomSong.setHasFixedSize(true);
        binding.rvRandomSong.setLayoutManager(linearLayoutManager);
        binding.rvRandomSong.addItemDecoration(new SongItemDecoration());
    }

    /**
     * Cấu hình hiệu ứng cho ViewPager2 của album slider
     */
    private void initAlbumSliderViewPager() {
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(30));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        binding.imageSlider.setPageTransformer(compositePageTransformer);
    }

    /**
     * Lấy dữ liệu từ Firebase
     */
    private void fetchData() {
        // Tải bảng xếp hạng bài hát
        homeViewModel.fetchTopSongs();

        // Tải danh sách album để làm slider
        homeViewModel.fetchCollectionAlbumSlider();
    }
}