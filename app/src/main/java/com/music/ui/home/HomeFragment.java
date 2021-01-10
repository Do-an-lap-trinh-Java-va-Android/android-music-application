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

import com.music.databinding.FragmentHomeBinding;
import com.music.ui.home.adapters.AlbumSliderVerticalAdapter;
import com.music.ui.home.adapters.song.SongChartVerticalAdapter;
import com.music.ui.home.adapters.song.SongChartVerticalItemDecoration;
import com.music.utils.ToolbarHelper;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    @Nullable
    private FragmentHomeBinding binding;

    @SuppressWarnings({"NotNullFieldNotInitialized", "FieldCanBeLocal"})
    @NonNull
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        ToolbarHelper.hideToolbar(getActivity());

        initAlbumSliderViewPager();
        initTopSongRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.getTopSongList().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case SUCCESS:
                    binding.rvRandomSong.setAdapter(new SongChartVerticalAdapter(Objects.requireNonNull(response.data)));
                    binding.prbRandomSongLoading.setVisibility(View.GONE);
                    break;
                case LOADING:
                    binding.prbRandomSongLoading.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    Toast.makeText(getActivity(), response.message, Toast.LENGTH_SHORT).show();
                    binding.prbRandomSongLoading.setVisibility(View.GONE);
                    break;
            }
        });

        homeViewModel.getAlbumSlider().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case SUCCESS:
                    binding.imageSlider.setAdapter(new AlbumSliderVerticalAdapter(Objects.requireNonNull(response.data)));
                    binding.prbAlbumSliderLoading.setVisibility(View.GONE);
                    break;
                case LOADING:
                    binding.prbAlbumSliderLoading.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    Toast.makeText(getActivity(), response.message, Toast.LENGTH_SHORT).show();
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
        binding.rvRandomSong.addItemDecoration(new SongChartVerticalItemDecoration());
    }

    /**
     * Cấu hình hiệu ứng cho ViewPager2 của album slider
     */
    private void initAlbumSliderViewPager() {
        final CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(30));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        binding.imageSlider.setPageTransformer(compositePageTransformer);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}