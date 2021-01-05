package com.music.ui.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.music.databinding.FragmentChartBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChartFragment extends Fragment {
    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private FragmentChartBinding binding;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private ChartViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChartBinding.inflate(inflater, container, false);

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        binding.rvSongChartHorizontal.setHasFixedSize(true);
        binding.rvSongChartHorizontal.setLayoutManager(linearLayoutManager);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ChartViewModel.class);

        viewModel.fetchTopSongList();

        viewModel.getTopSongList().observe(getViewLifecycleOwner(), request -> {
            switch (request.status) {
                case SUCCESS:
                    binding.rvSongChartHorizontal.setAdapter(new SongChartHorizontalAdapter(request.data));
                    break;
                case LOADING:
                    break;
                case ERROR:
                    break;
            }
        });
    }
}