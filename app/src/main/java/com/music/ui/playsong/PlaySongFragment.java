package com.music.ui.playsong;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.music.R;
import com.music.databinding.FragmentPlaySongBinding;
import com.music.models.Song;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaySongFragment extends Fragment {
    @Nullable
    private FragmentPlaySongBinding binding;

    @SuppressWarnings({"NotNullFieldNotInitialized", "FieldCanBeLocal"})
    @NonNull
    private PlaySongFragmentArgs args;

    @SuppressWarnings("FieldCanBeLocal")
    private PlaySongViewModel viewModel;

    @NonNull
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    @NonNull
    private final Handler handler = new Handler();

    @Nullable
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this, 500);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = PlaySongFragmentArgs.fromBundle(requireArguments());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaySongBinding.inflate(inflater, container, false);

        mediaPlayer.setOnBufferingUpdateListener((_mediaPlayer, percent) -> {
            binding.seekBar.setSecondaryProgress(percent);
        });

        mediaPlayer.setOnPreparedListener(_mediaPlayer -> {
            binding.seekBar.setMax(mediaPlayer.getDuration());
            binding.tvLengthOfSong.setText(formatDuration(mediaPlayer.getDuration()));
            binding.btnPlay.performClick();
        });

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.tvCurrentPosition.setText(formatDuration(mediaPlayer.getCurrentPosition()));

                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        binding.btnPlay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                binding.btnPlay.setImageResource(R.drawable.ic_outline_play_circle_light_64);
                handler.removeCallbacks(runnable);
            } else {
                mediaPlayer.start();
                binding.btnPlay.setImageResource(R.drawable.ic_outline_pause_circle_light_64);
                handler.postDelayed(runnable, 0);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(PlaySongViewModel.class);

        viewModel.getInfoOfSong(args.getSongUid());

        viewModel.getSongMutableLiveData().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case SUCCESS:
                    Song song = Objects.requireNonNull(response.data);
                    Glide.with(binding.ivThumbnail.getContext()).load(song.getThumbnail()).circleCrop().into(binding.ivThumbnail);
                    binding.tvSongName.setText(song.getName());
                    binding.tvSongArtists.setText(song.getArtistsNames());
                    try {
                        mediaPlayer.setDataSource(requireActivity(), Uri.parse(song.getMp3()));
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    binding.frmLoading.setVisibility(View.GONE);
                    break;
                case LOADING:
                    binding.frmLoading.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    Toast.makeText(requireActivity(), "Tải bài hát thất bại", Toast.LENGTH_SHORT).show();
                    binding.frmLoading.setVisibility(View.GONE);
                    break;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.GONE);
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    private String formatDuration(long duration) {
        long minutues = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutues);

        return String.format("%02d:%02d", minutues, seconds);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.VISIBLE);
        mediaPlayer.setOnBufferingUpdateListener(null);
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}