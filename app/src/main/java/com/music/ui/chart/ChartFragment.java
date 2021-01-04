package com.music.ui.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.music.databinding.FragmentChartBinding;

public class ChartFragment extends Fragment {
    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private FragmentChartBinding binding;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private ChartViewModel chartViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChartBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        chartViewModel = new ViewModelProvider(this).get(ChartViewModel.class);
    }
}