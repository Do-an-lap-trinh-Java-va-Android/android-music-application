package com.music.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.music.R;
import com.music.databinding.FragmentSearchBinding;
import com.music.utils.ToolbarHelper;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment implements TextWatcher {
    @Nullable
    private FragmentSearchBinding binding;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private SearchViewModel viewModel;

    private final Handler handler = new Handler(Looper.myLooper());

    @Nullable
    private Runnable debounceRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(requireView()).popBackStack());

        binding.edtSearch.requestFocus();
        binding.edtSearch.addTextChangedListener(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        viewModel.getSongs().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case SUCCESS:
                    binding.frmLoading.setVisibility(View.GONE);

                    if (Objects.requireNonNull(response.data).isEmpty()) {
                        Toast.makeText(requireActivity(), "Không tìm thấy bài hát có từ khóa cần tìm", Toast.LENGTH_SHORT).show();
                    }

                    binding.layoutResultFindSong.setVisibility(View.VISIBLE);
                    binding.lvSongs.setAdapter(new SongVerticalArrayAdapter(requireActivity(),
                            R.layout.search_item_layout, response.data));
                    break;
                case LOADING:
                    binding.layoutResultFindSong.setVisibility(View.GONE);
                    binding.frmLoading.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    Toast.makeText(requireActivity(), response.message, Toast.LENGTH_SHORT).show();
                    binding.frmLoading.setVisibility(View.GONE);
                    break;
            }
        });

        viewModel.getArtists().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case SUCCESS:
                    binding.frmLoading.setVisibility(View.GONE);

                    if (Objects.requireNonNull(response.data).isEmpty()) {
                        Toast.makeText(requireActivity(), "Không tìm thấy nghệ sĩ có từ khóa cần tìm",
                                Toast.LENGTH_SHORT).show();
                    }

                    binding.layoutResultFindArtist.setVisibility(View.VISIBLE);
                    binding.lvArtists.setAdapter(new ArtistVerticalArrayAdapter(requireActivity(),
                            R.layout.search_item_layout, response.data));
                    break;
                case LOADING:
                    binding.layoutResultFindSong.setVisibility(View.GONE);
                    binding.frmLoading.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    Toast.makeText(requireActivity(), response.message, Toast.LENGTH_SHORT).show();
                    binding.frmLoading.setVisibility(View.GONE);
                    break;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        ToolbarHelper.hideToolbar(requireActivity());
        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();

        ToolbarHelper.showToolbar(requireActivity());
        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (debounceRunnable != null) {
            handler.removeCallbacks(debounceRunnable);
        }

        if (s.toString().trim().length() == 0) {
            return;
        }

        debounceRunnable = () -> {
            viewModel.searchSongByName(s.toString());
            viewModel.searchArtistByName(s.toString());
        };

        handler.postDelayed(debounceRunnable, 500);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}